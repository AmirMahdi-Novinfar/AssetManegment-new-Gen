package com.tehranmunicipality.assetmanagement.ui.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.andreseko.SweetAlert.SweetAlertDialog
import com.tehranmunicipality.assetmanagement.data.INACTIVITY_TIMEOUT
import com.tehranmunicipality.assetmanagement.ui.login.ActivityLogin
import com.tehranmunicipality.assetmanagement.ui.main.MainViewModel
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private var inactivityJob: Job? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

    }

    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)

    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        resetInactivityTimer()
    }

    private fun startInactivityTimer() {
        inactivityJob?.cancel() // Cancel the previous job if it exists
        inactivityJob = CoroutineScope(Dispatchers.Main).launch {
            delay(INACTIVITY_TIMEOUT)

            mainViewModel.deleteUser()
            delay(1000)
            mainViewModel.getUser()
            mainViewModel.appUserResponse.observe(this@BaseActivity) {

                when (it.status) {

                    Status.SUCCESS -> {
                        if (it.data == null) {
                            mainViewModel.deleteUser()
                            val message = "زمان شما به پایان رسیده\n و از سیستم خارج میشوید"
                            showCustomDialog(this@BaseActivity, DialogType.ERROR, message,
                                object : IClickListener {
                                    override fun onClick(view: View?, dialog: Dialog) {
                                        val loginIntent =
                                            Intent(this@BaseActivity, ActivityLogin::class.java)
                                        loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(loginIntent)
                                        finishAffinity()
                                    }

                                })
                        }
                    }

                    Status.LOADING -> {

                    }

                    Status.ERROR -> {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startInactivityTimer()
        Log.i("DEBUG", "BaseActivity onResume raised")
    }

    private fun resetInactivityTimer() {
        inactivityJob?.cancel() // Cancel the previous job if it exists
        startInactivityTimer()
    }

    private fun checkInternetConnection() {
        if (applicationContext?.let { isNetworkAvailable(it) } == false) {
            Log.i("DEBUG", "internet connection is not available")
            var errorMessage = "اتصال اینترنت برقرار نیست"

            showCustomDialog(this@BaseActivity, DialogType.ERROR, errorMessage,
                object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        dialog.dismiss()
                    }
                })
        }
    }

    override fun onPause() {
        super.onPause()
        inactivityJob?.cancel()
    }

    open fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}