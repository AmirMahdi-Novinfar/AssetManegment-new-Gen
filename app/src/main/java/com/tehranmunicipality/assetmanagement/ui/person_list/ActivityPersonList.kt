package com.tehranmunicipality.assetmanagement.ui.person_list

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.asset_information.ShowAssetInformationViewModel
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityPersonList : AppCompatActivity(), View.OnClickListener {

    private val personViewModel: PersonViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var rvPersonList: RecyclerView
    private lateinit var personListAdapter: PersonListAdapter
    private lateinit var vwLoading: View
    private var actorName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_person_list)

        bindView()
        setupClicks()
        initializeRecyclerView()
        getSearchedName()
        getUser()
        observeViewModel()

    }

    private fun getSearchedName() {
        if (intent.hasExtra("username")) {
            actorName = intent.getStringExtra("username").toString()
        } else {
            val errorMessage = "خطا در دریافت نام جستجو شده"
            showSnackBarMessage(rvPersonList, errorMessage)
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1500)
                finish()
            }
        }
    }

    private fun observeViewModel() {

        personViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    if (it.data != null) {
                        if (!applicationContext.let { isNetworkAvailable(it) }) {
                            val errorMessage = "اتصال اینترنت برقرار نیست"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        } else {
                            getEsbToken(it.data.username, it.data.password)
                        }
                    }
                }

                Status.LOADING -> {

                }

                Status.ERROR -> {
                    val errorMessage = "error getting user from database"
                    showSnackBarMessage(rvPersonList, errorMessage)
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(1500)
                        finish()
                    }
                }
            }
        }

        personViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    if (it.data != null) {
                        getPersonList(it.data.accessToken.toString(), actorName)
                    }
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.ERROR -> {
                    hideLoading()
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }

        personViewModel.getPersonListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    if (it.data != null) {
                        if (it.data.personList?.isNotEmpty() == true) {
                            personListAdapter.addData(it.data.personList)
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                val errorMessage = "لیست اشخاص خالی است"
                                showCustomDialog(
                                    this@ActivityPersonList,
                                    DialogType.ERROR,
                                    errorMessage
                                )
                                delay(2000)
                                finish()
                            }
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.ERROR -> {
                    hideLoading()
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
                        override fun onClick(view: View?, dialog: Dialog) {
                            finish()
                        }
                    })
                }
            }
        }
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun getUser() {
        personViewModel.getUser()
    }

    private fun getEsbToken(username: String, password: String) {
        personViewModel.getESBToken(username, password)
    }

    private fun getPersonList(accessToken: String, name: String) {
        personViewModel.getPersonList(accessToken, name)
    }

    private fun initializeRecyclerView() {

        personListAdapter = PersonListAdapter(object : IPersonItemClickListener {
            override fun itemClicked(lastName: String) {
                val intent =
                    Intent(this@ActivityPersonList, ActivityShowAssetInformation::class.java)
                intent.putExtra("fromActivity", this@ActivityPersonList.javaClass.simpleName)
                intent.putExtra(
                    "username",
                    lastName
                )

                startActivity(intent)
            }
        })
        rvPersonList.layoutManager = LinearLayoutManager(this)
        rvPersonList.itemAnimator = DefaultItemAnimator()
        rvPersonList.adapter = personListAdapter
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        rvPersonList = findViewById(R.id.rvPersonList)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {

            ivBack -> {
                finish()
            }
        }
    }

}