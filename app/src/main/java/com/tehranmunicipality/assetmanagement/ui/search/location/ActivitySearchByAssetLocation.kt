package com.tehranmunicipality.assetmanagement.ui.search.location

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.CustomDialog
import com.tehranmunicipality.assetmanagement.ui.search.SearchViewModel
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivitySearchByAssetLocation : BaseActivity(), View.OnClickListener {

    private val searchByAssetLocationViewModel: SearchViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var tvLocationList: TextView
    private lateinit var ivDropDownLocation: ImageView
    private lateinit var rlChooseSearchMode: RelativeLayout
    private lateinit var btnInquiry: Button
    private lateinit var vwLoading: View
    private lateinit var locationListItem: LocationListItem
    private lateinit var locationListResponse: GetLocationListResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_asset_by_location)

        bindView()
        setupClicks()
        getUser()
        observeViewModel()
    }

    private fun getUser() {
        searchByAssetLocationViewModel.getUser()
    }

        private fun initializeLocationsListSpinner() {

        val locationNameList = mutableListOf<String>()
        for (location in locationListResponse.locationList!!) {
            if (location != null) {
                locationNameList.add(location.assetLocationName.toString())
            } else {
                val errorMessage = locationListResponse.detailError?.get(0)?.errorDesc.toString()
                showCustomDialog(this, DialogType.ERROR, errorMessage)
                lifecycleScope.launch(Dispatchers.IO) {
                    delay(2000)
                    finish()
                }
            }
        }
    }

    override fun onClick(p0: View?) {

        when (p0) {

            ivBack -> {
                finish()
            }

            rlChooseSearchMode -> {

                if (::locationListResponse.isInitialized) {
                    val customDialog1 = CustomDialog(this,
                        "محل استقرار را انتخاب نمایید",
                        locationListResponse.locationList as List<LocationListItem>,
                        ObjectType.LOCATION_LIST,
                        object : ItemClickListener {
                            override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                                TODO("Not yet implemented")
                            }

                            override fun goodListItemClicked(goodListItem: GoodList) {
                                TODO("Not yet implemented")
                            }

                            override fun locationItemClicked(_locationListItem: LocationListItem) {
                                locationListItem = _locationListItem
                                tvLocationList.setText(locationListItem.assetLocationName)
                                btnInquiry.visibility = View.VISIBLE
                            }

                            override fun costCenterItemClicked(costCenterListItem: CostCenterListItem) {
                                TODO("Not yet implemented")
                            }

                            override fun subCostCenterItemClicked(subCostCenterListItem: SubCostCenterListItem) {
                                TODO("Not yet implemented")
                            }

                            override fun personListItemClicked(personListItem: PersonListItem) {
                                TODO("Not yet implemented")
                            }

                            override fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem) {
                                TODO("Not yet implemented")
                            }

                        })
                }

            }

            btnInquiry -> {

                if (isInputValid()) {
                    val intent = Intent(this, ActivityShowAssetInformation::class.java)
                    intent.putExtra("fromActivity", javaClass.simpleName)
                    Log.i("DEBUG", "this.localClassName=${this.localClassName}")
                    intent.putExtra("assetLocationId", locationListItem.assetLocationID.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        vwLoading = findViewById(R.id.vwLoading)
        tvLocationList = findViewById(R.id.tvLocationList)
        ivDropDownLocation = findViewById(R.id.ivDropDownLocation)
        rlChooseSearchMode = findViewById(R.id.rlChooseSearchMode)
        btnInquiry = findViewById(R.id.btnInquiry)
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        rlChooseSearchMode.setOnClickListener(this)
        btnInquiry.setOnClickListener(this)
    }

    private fun observeViewModel() {

        searchByAssetLocationViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    if (it.data != null) {
                        if (!it.data.token.isEmpty()) {
                            if (!applicationContext.let { isNetworkAvailable(it) }) {
                                val errorMessage = "اتصال اینترنت برقرار نیست"
                                showCustomDialog(this, DialogType.ERROR, errorMessage)
                            } else {
                                searchByAssetLocationViewModel.getESBToken(
                                    it.data.username,
                                    it.data.password
                                )
                            }
                        } else {
                            Log.i(
                                "DEBUG",
                                "ActivitySearchByAssetLocation observeViewModel appUserResponse token is empty"
                            )
                            val errorMessage = "خطا در دریافت اطلاعات کاربر از دیتابیس محلی "
                            showCustomDialog(
                                this,
                                DialogType.ERROR,
                                errorMessage, object : IClickListener {
                                    override fun onClick(view: View?, dialog: Dialog) {
                                        dialog.dismiss()
                                        finish()
                                    }
                                })
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel appUserResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel appUserResponse error.->${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage, object : IClickListener {
                            override fun onClick(view: View?, dialog: Dialog) {
                                dialog.dismiss()
                                finish()
                            }
                        })
                }
            }
        }

        searchByAssetLocationViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    if (!it.data!!.accessToken!!.isEmpty()) {
                        getLocationsList(it.data.accessToken.toString())
                    } else {
                        val errorMessage = "خطا در دریافت توکن ESB"
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(2000)
                            finish()
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel esbTokenResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel esbTokenResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(2000)
                        finish()
                    }
                }
            }
        }

        searchByAssetLocationViewModel.getLocationListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel getLocationListResponse success.data=${it.data}"
                    )
                    if (it.data?.locationList != null) {
                        //val customDialog = CustomDialog(this, it.data.locationList)
                        locationListResponse = it.data
                        initializeLocationsListSpinner()
                    } else {
                        val errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(2000)
                            finish()
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel getLocationListResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByAssetLocation observeViewModel getLocationListResponse error.${it.message}"
                    )
                    var errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                    if (errorMessage.isEmpty()) {
                        errorMessage = it.message.toString()
                    }
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                    lifecycleScope.launch(Dispatchers.IO) {
                        delay(2000)
                        finish()
                    }
                }
            }
        }
    }

    private fun getLocationsList(accessToken: String) {
        searchByAssetLocationViewModel.getLocationList(accessToken)
    }

    private fun isInputValid(): Boolean {

        var isValid = true
        var errorMessage = ""
        //btnInquiry.isEnabled = true
        //btnInquiry.visibility = View.VISIBLE

        if (!applicationContext.let { isNetworkAvailable(it) }) {
            isValid = false
            errorMessage = "اتصال اینترنت برقرار نیست"
            //btnInquiry.isEnabled = false
            //btnInquiry.visibility = View.GONE
        }

        if (!::locationListItem.isInitialized) {
            isValid = false
            errorMessage = "محل های استقرار مقداری ندارند"
            //btnInquiry.isEnabled = false
            //btnInquiry.visibility = View.GONE
        }

        if (::locationListItem.isInitialized) {
            if (locationListItem.assetLocationID == -1) {
                isValid = false
                errorMessage = "نام محل استقرار اموال را انتخاب نمایید"
                //btnInquiry.isEnabled = false
                //btnInquiry.visibility = View.GONE
            }
        }

        if (!isValid) {
            showSnackBarMessage(btnInquiry, errorMessage)
        }
        return isValid
    }
}