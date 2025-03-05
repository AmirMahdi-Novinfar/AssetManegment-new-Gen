package com.tehranmunicipality.assetmanagement.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import com.tehranmunicipality.assetmanagement.ui.asset_allocation.ActivityAssetAllocation
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.login.ActivityLogin
import com.tehranmunicipality.assetmanagement.ui.profile.ActivityProfile
import com.tehranmunicipality.assetmanagement.ui.profile.ProfileViewModel
import com.tehranmunicipality.assetmanagement.ui.search.barcode.ActivitySearchByBarcode
import com.tehranmunicipality.assetmanagement.ui.search.location.ActivitySearchByAssetLocation
import com.tehranmunicipality.assetmanagement.ui.search.username_or_national_code.ActivitySearchByUsernameOrNationalCode
import com.tehranmunicipality.assetmanagement.ui.user_information.HelpActivity
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityMain : BaseActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var ivProfile: ImageView
    private lateinit var acbSearchByNationalCode: AppCompatButton
    private lateinit var acbSearchByBarcode: AppCompatButton
    private lateinit var rlDenominateAsset: AppCompatButton
    private lateinit var makhdoosh_activity: AppCompatButton
    private lateinit var acbSearchByAssetLocation: AppCompatButton
    private var appUser: AppUser? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView :NavigationView
    private val profileViewModel: ProfileViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
        setupClicks()
        getDataFromIntent()
        observeViewModel()
        setUpNavigation()
    }

    private fun refreshESBToken(username: String, password: String) {
        Log.i("DEBUG", "ActivityMain refreshESBToken")
        mainViewModel.getESBToken(username, password)
    }

    private fun getDataFromIntent() {
        val extras = intent.extras
        val displayName = extras?.getString("displayName").toString()
        val username = extras?.getString("username").toString()
        val password = extras?.getString("password").toString()
        val token = extras?.getString("token").toString()
        appUser = AppUser(username, password, displayName, token)
    }

    private fun bindView() {
        ivProfile = findViewById(R.id.ivProfile)
        acbSearchByNationalCode = findViewById(R.id.acbSearchByNationalCode)
        acbSearchByBarcode = findViewById(R.id.acbSearchByBarcode)
        rlDenominateAsset = findViewById(R.id.rlDenominateAsset)
        makhdoosh_activity = findViewById(R.id.makhdoosh_activity)
        navigationView=findViewById(R.id.nav_view)
        acbSearchByAssetLocation = findViewById(R.id.acbSearchByAssetLocation)

    }

    private fun setupClicks() {
        ivProfile.setOnClickListener(this)
        acbSearchByNationalCode.setOnClickListener(this)
        acbSearchByBarcode.setOnClickListener(this)
        rlDenominateAsset.setOnClickListener(this)
        makhdoosh_activity.setOnClickListener(this)
        acbSearchByAssetLocation.setOnClickListener(this)
    }

    private fun observeViewModel() {

        mainViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i("DEBUG", "ActivityMain observeViewModel esbTokenResponse success.")
                    if (!it.data?.accessToken.isNullOrEmpty()) {
                        mainViewModel.insertUser(
                            appUser!!.username, appUser!!.password,
                            appUser!!.displayName, it.data?.accessToken.toString()
                        )
                    }
                }

                Status.LOADING -> {
                    Log.i("DEBUG", "ActivityMain observeViewModel esbTokenResponse loading")
                }

                Status.ERROR -> {
                    Log.i(
                        "DEBUG",
                        "ActivityMain observeViewModel esbTokenResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {

            ivProfile -> {
//                val intent = Intent(this, ActivityProfile::class.java)
//                startActivity(intent)

                openCloseNavigationDrawer()
            }

            acbSearchByNationalCode -> {
                Log.i("DEBUG", "ActivityMain acbSearchByNationalCode clicked")
                val intent = Intent(this, ActivitySearchByUsernameOrNationalCode::class.java)
                startActivity(intent)
            }

            acbSearchByBarcode -> {
                Log.i("DEBUG", "ActivityMain acbSearchByBarcode clicked")
                val intent = Intent(this, ActivitySearchByBarcode::class.java)
                startActivity(intent)
            }

            rlDenominateAsset -> {
                Log.i("DEBUG", "ActivityMain acbDenominateAsset clicked")
                val intent = Intent(this, ActivityAssetAllocation::class.java)
                intent.putExtra("fromactivitynewormakhdoosh","rlDenominateAssetamir")
                startActivity(intent)
            }
            makhdoosh_activity -> {
                Log.i("DEBUG", "ActivityMain acbDenominateAsset clicked")
                val intent = Intent(this, ActivityAssetAllocation::class.java)
                intent.putExtra("fromactivitynewormakhdoosh","makhdoosh_activity")
                startActivity(intent)
            }

            acbSearchByAssetLocation -> {
                Log.i("DEBUG", "ActivityMain acbSearchByAssetLocation clicked")
                val intent = Intent(this, ActivitySearchByAssetLocation::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            if (!applicationContext.let { isNetworkAvailable(it) }) {
                val errorMessage = "اتصال اینترنت برقرار نیست"
                showCustomDialog(this@ActivityMain, DialogType.ERROR, errorMessage)
            } else {
                refreshESBToken(appUser!!.username, appUser!!.password)
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onBackPressed() {
        finishAffinity()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }




    private fun setUpNavigation(){
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pro -> {
                val intent = Intent(this, ActivityProfile::class.java)
                startActivity(intent)
            }
            R.id.nav_faq -> {

            }R.id.nav_help -> {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
            }


            R.id.nav_logout -> {

                Log.i("DEBUG", "ActivityProfile rlExit clicked")
                openConfirmExitDialog(this@ActivityMain, object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        profileViewModel.deleteUser()
                        finishAffinity()
                        val intent = Intent(this@ActivityMain, ActivityLogin::class.java)
                        startActivity(intent)
                    }
                })

            }
            // موارد دیگر...
                    // موارد دیگر...
        }


        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }



    fun openCloseNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

}