package com.tehranmunicipality.assetmanagement.ui.user_information

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.ui.asset.ActivityModifyAsset
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class ActivityShowUserInformation : BaseActivity(), View.OnClickListener {

    private val showUserInformationViewModel: ShowUserInformationViewModel by viewModels()
    private lateinit var tvProductName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvAssetTypeName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvRegionInfo: TextView
    private lateinit var rvUserInformation: RecyclerView
    private lateinit var assetListAdapter: AssetListAdapter
    private lateinit var vwLoading: View
    private lateinit var tvRecordCount: TextView
    private var searchText = ""
    private lateinit var searchType: SearchType


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user_information)

        bindView()
        setupClicks()
        if (intent.hasExtra("fromActivity")) {

            when (intent.getStringExtra("fromActivity")) {

                "ui.search.barcode.ActivitySearchByBarcode" -> {
                    if (intent.hasExtra("barcodeId")) {
                        //assetTag = intent.getStringExtra("barcodeId").toString()
                        searchText = intent.getStringExtra("barcodeId").toString()
                        searchType = SearchType.Barcode
                    } else if ((intent.hasExtra("tagText"))) {
                        searchText = intent.getStringExtra("tagText").toString()
                        searchType = SearchType.TagText
                    }
                }

                "ui.search.location.ActivitySearchByAssetLocation" -> {
                    if (intent.hasExtra("assetLocationId")) {
                        //assetLocationId = intent.getStringExtra("assetLocationId").toString()
                        searchText = intent.getStringExtra("assetLocationId").toString()
                        searchType = SearchType.Location
                    }
                }

                "ui.search.username_or_national_code.ActivitySearchByUsernameOrNationalCode" -> {

                    if (intent.hasExtra("username")) {
                        searchText = intent.getStringExtra("username").toString()
                        searchType = SearchType.Username
                    } else if (intent.hasExtra("nationalCode")) {
                        //nationalCode = intent.getStringExtra("nationalCode").toString()
                        searchText = intent.getStringExtra("nationalCode").toString()
                        searchType = SearchType.NationalCode
                    }
                }
            }
        }
        initializeRecyclerView()
        getUser()
        observeViewModel()
    }

    private fun initializeRecyclerView() {
        val intent = Intent(this, ActivityModifyAsset::class.java)
        rvUserInformation.layoutManager = LinearLayoutManager(applicationContext)
        assetListAdapter =
            AssetListAdapter(object : ItemClickListener {
                override fun itemClicked(assetListItem: AssetListItem) {
                    Log.i("DEBUG", "ActivityShowUserInformation assetID=${assetListItem.assetID}")
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation assetTypeCode=${assetListItem.assetTypeCode}"
                    )
                    Log.i("DEBUG", "ActivityShowUserInformation barCode=${assetListItem.barCode}")

                    intent.putExtra("type", "edit")

                    if (assetListItem.assetID != null) {
                        intent.putExtra("assetId", assetListItem.assetID.toString())
                    }
                    if (assetListItem.assetTypeCode != null) {
                        intent.putExtra("assetTypeCode", assetListItem.assetTypeCode.toString())
                    }
                    if (assetListItem.barCode != null) {
                        intent.putExtra("barcode", assetListItem.barCode.toString())
                    }
                    if (assetListItem.assetHistoryId != null) {
                        intent.putExtra("assetHistoryId", assetListItem.assetHistoryId.toString())
                    }

                    if (assetListItem.costCenterID != null) {
                        intent.putExtra("costCenterID", assetListItem.costCenterID.toString())
                    }

                    if (assetListItem.costCenterName != null) {
                        intent.putExtra("costCenterName", assetListItem.costCenterName.toString())
                    }

                    if (assetListItem.costCenterCode != null) {
                        intent.putExtra("costCenterCode", assetListItem.costCenterCode.toString())
                    }

                    if (assetListItem.subCostCenterID != null) {
                        intent.putExtra("subCostCenterID", assetListItem.subCostCenterID.toString())
                    }

                    if (assetListItem.subCostCenterName != null) {
                        intent.putExtra(
                            "subCostCenterName",
                            assetListItem.subCostCenterName.toString()
                        )
                    }

                    if (assetListItem.subCostCenterCode != null) {
                        intent.putExtra(
                            "subCostCenterCode",
                            assetListItem.subCostCenterCode.toString()
                        )
                    }

                    if (assetListItem.assetLocationID != null) {
                        intent.putExtra("assetLocationID", assetListItem.assetLocationID.toString())
                    }

                    if (assetListItem.assetLocationName != null) {
                        intent.putExtra(
                            "assetLocationName",
                            assetListItem.assetLocationName.toString()
                        )
                    }

                    if (assetListItem.actorID != null) {
                        intent.putExtra("actorID", assetListItem.actorID.toString())
                    }

                    if (assetListItem.actorName != null) {
                        intent.putExtra("actorName", assetListItem.actorName.toString())
                    }

                    if (assetListItem.assetStatusCode != null) {
                        intent.putExtra("assetStatusCode", assetListItem.assetStatusCode.toString())
                    }

                    if (assetListItem.assetTypeName != null) {
                        intent.putExtra("assetTypeName", assetListItem.assetTypeName.toString())
                    }

                    startActivity(intent)
                }
            })
        rvUserInformation.addItemDecoration(DividerItemDecoration(applicationContext, 0))
        rvUserInformation.adapter = assetListAdapter
    }

    private fun getUser() {
        showUserInformationViewModel.getUser()
    }

    private fun bindView() {
        tvRegionInfo = findViewById(R.id.tvRegionInfo)
        tvRecordCount = findViewById(R.id.tvRecordCount)
        tvProductName = findViewById(R.id.tvProductName)
        tvLocation = findViewById(R.id.tvLocation)
        tvAssetTypeName = findViewById(R.id.tvAssetTypeName)
        tvUsername = findViewById(R.id.tvUsername)
        rvUserInformation = findViewById(R.id.rvUserInformation)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun searchAsset(
        searchType: SearchType,
        accessToken: String = "",
        searchText: String = ""
    ) {
        showUserInformationViewModel.getAssetList(searchType, accessToken, searchText)
    }

    private fun setupClicks() {

    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun observeViewModel() {

        showUserInformationViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel appUserResponse success. dat=${it.data}"
                    )
                    if (!it.data?.token.isNullOrEmpty()) {
                        showUserInformationViewModel.getESBToken(
                            it.data?.username.toString(),
                            it.data?.password.toString()
                        )
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityShowUserInformation observeViewModel appUSerResponse user token is null or empty!!!"
                        )
                        val errorMessage = "خطا در دریافت توکن کاربر از دیتابیس محلی"
                        showDialog2(
                            this,
                            DialogType.ERROR,
                            errorMessage
                        ) { sweetAlertDialog ->
                            sweetAlertDialog.dismiss()
                            finish()
                        }

                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel appUserResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel appUserResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showDialog2(this, DialogType.ERROR, errorMessage) { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                        finish()
                    }
                }
            }
        }

        showUserInformationViewModel.esbTokenResponse.observe(this) {
            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    if (!it.data?.accessToken.isNullOrEmpty()) {
                        searchAsset(searchType, it.data?.accessToken.toString(), searchText)
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityShowUserInformation observeViewModel esbTokenResponse token is empty or null!!!"
                        )
                        val errorMessage = "خطا در دریافت توکن ESB"
                        showDialog2(
                            this,
                            DialogType.ERROR,
                            errorMessage
                        ) { sweetAlertDialog ->
                            sweetAlertDialog.dismiss()
                            finish()
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel esbTokenResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel esbTokenResponse error.${it.message}"
                    )
                    val errorMessage = it.data?.error_description.toString()
                    showDialog2(
                        this,
                        DialogType.ERROR,
                        errorMessage
                    ) { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                        finish()
                    }
                }
            }
        }

        showUserInformationViewModel.getAssetListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel getAssetListResponse success.data=${it.data} "
                    )
                    if (it.data?.assetList != null) {
                        if (it.data?.assetList?.size!! > 0) {
                            tvRegionInfo.text =
                                englishToPersian(
                                    "واحد اجرایی : ${it.data.assetList.get(0)?.regionCode}-${
                                        it.data.assetList.get(
                                            0
                                        )?.regionName
                                    }"
                                )
                            assetListAdapter.addData(it.data?.assetList as List<AssetListItem>)
                            tvRecordCount.text =
                                "تعداد:${englishToPersian(it.data.assetList.size.toString())}"
                        } else {
                            Log.i(
                                "DEBUG",
                                "ActivityShowUserInformation observeViewModel getAssetListResponse success. list is empty. so we return back"
                            )
                            val message = "اموالی یافت نشد"
                            showSnackBarMessage(rvUserInformation, message)
                            lifecycleScope.launch(Dispatchers.Main) {
                                delay(2000)
                                finish()
                            }
                        }
                    } else {
                        val errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                        showDialog2(
                            this,
                            DialogType.ERROR,
                            errorMessage
                        ) { sweetAlertDialog ->
                            sweetAlertDialog.dismiss()
                            finish()
                        }
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel getAssetListResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel getAssetListResponse error.error=${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showDialog2(this, DialogType.ERROR, errorMessage) { sweetAlertDialog ->
                        sweetAlertDialog.dismiss()
                        finish()
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {

    }
}