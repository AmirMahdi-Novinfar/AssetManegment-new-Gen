package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.ui.asset.ActivityModifyAsset
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ActivityShowAssetInformation : BaseActivity(), View.OnClickListener {

    private val showAssetInformationViewModel: ShowAssetInformationViewModel by viewModels()
    private lateinit var ivBack: ImageView
   // private lateinit var ivInfo: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvAssetTypeName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var rvAssetInformation: RecyclerView
    private lateinit var acetSearchText: AppCompatEditText
    private lateinit var ivSearch: ImageView
    private lateinit var expandableListAdapter2: AssetExpandableListAdapter2
    private lateinit var vwLoading: View
    private lateinit var tvRecordCount: TextView
    private lateinit var tvItem1: TextView
    private lateinit var tvItem2: TextView
    private var searchText = ""
    private var accessToken = ""
    private var assetId = 0
    private var barcode = "-1"
    private lateinit var searchTypeFromAPI: SearchType
    private var searchTypeLocally = SearchType.AssetName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_asset_information)

        bindView()
        setupClicks()
        if (intent.hasExtra("fromActivity")) {

            when (intent.getStringExtra("fromActivity")) {

                "ActivitySearchByBarcode" -> {
                    if (intent.hasExtra("barcodeId")) {
                        //assetTag = intent.getStringExtra("barcodeId").toString()
                        searchText = intent.getStringExtra("barcodeId").toString()
                        searchTypeFromAPI = SearchType.Barcode
                        searchTypeLocally = SearchType.Barcode
                        Log.i("DEBUG", "searchType is barcode")
                        acetSearchText.filters = arrayOf(InputFilter.LengthFilter(BARCODE_LENGTH))
                        acetSearchText.keyListener = DigitsKeyListener.getInstance("0123456789")
                    } else if ((intent.hasExtra("tagText"))) {
                        searchText = intent.getStringExtra("tagText").toString()
                        searchTypeFromAPI = SearchType.TagText
                        searchTypeLocally = SearchType.TagText
                        Log.i("DEBUG", "searchType is tagtext")
                        acetSearchText.filters = arrayOf(InputFilter.LengthFilter(10))
                        acetSearchText.keyListener = DigitsKeyListener.getInstance("0123456789")
                    }
                }

                "ActivitySearchByAssetLocation" -> {
                    if (intent.hasExtra("assetLocationId")) {
                        //assetLocationId = intent.getStringExtra("assetLocationId").toString()
                        searchText = intent.getStringExtra("assetLocationId").toString()
                        searchTypeFromAPI = SearchType.AssetName
                        searchTypeLocally = SearchType.AssetName
                        Log.i("DEBUG", "searchType is location")
                        acetSearchText.filters = arrayOf(InputFilter.LengthFilter(20))
                        acetSearchText.keyListener = DigitsKeyListener.getInstance("0123456789")
                        acetSearchText.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }

                "ActivitySearchByUsernameOrNationalCode" -> {

                    if (intent.hasExtra("nationalCode")) {
                        //nationalCode = intent.getStringExtra("nationalCode").toString()
                        searchText = intent.getStringExtra("nationalCode").toString()
                        searchTypeFromAPI = SearchType.NationalCode
                    }
                }

                "ActivityPersonList" -> {

                    if (intent.hasExtra("username")) {
                        searchText = intent.getStringExtra("username").toString()
                        searchTypeFromAPI = SearchType.Username
                    }
                }
            }
            getUser()
            observeViewModel()
        }
    }

    private fun setupTextChangeListener() {
        acetSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                expandableListAdapter2.search(searchTypeLocally, p0.toString())
                tvRecordCount.text =
                    "تعداد:${englishToPersian(expandableListAdapter2.itemCount.toString())}"
            }
        })
    }

    private fun initializeExpandableListView(
        assetList: List<AssetListItem>
    ) {
        val intent = Intent(this, ActivityModifyAsset::class.java)
        expandableListAdapter2 = AssetExpandableListAdapter2(this, object : AssetItemClickListener {
            override fun editItemClicked(assetListItem: AssetListItem) {

                Log.i("DEBUG", "ActivityShowUserInformation assetID=${assetListItem.assetID}")
                Log.i(
                    "DEBUG",
                    "ActivityShowUserInformation assetTypeCode=${assetListItem.assetTypeCode}"
                )
                Log.i("DEBUG", "ActivityShowUserInformation barCode=${assetListItem.barCode}")
                Log.i("amirnovin", "ActivityShowUserInformation barCode=${assetListItem.barCode}")

                intent.putExtra("type", "edit")

                if (assetListItem.assetID != null) {
                    intent.putExtra("assetId", assetListItem.assetID.toString())
                }
                if (assetListItem.assetCode != null) {
                    intent.putExtra("assetCode", assetListItem.assetCode.toString())
                }
                if (assetListItem.assetTypeCode != null) {
                    intent.putExtra("assetTypeCode", assetListItem.assetTypeCode.toString())
                }
                if (assetListItem.barCode != null) {
                    intent.putExtra("barcode", assetListItem.barCode.toString())
                }
                if (assetListItem.assetTag != null) {
                    intent.putExtra("assetTag", assetListItem.assetTag.toString())
                }
                if (assetListItem.productName != null) {
                    var productName=assetListItem.productName.toString()
                    intent.putExtra("productName", productName)
                    Log.i("ghandman",productName)
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

                if (assetListItem.barCode == null || assetListItem.barCode == -1) {
                    Log.i("DEBUG", "ActivityShowUserInformation ${assetListItem.barCode}")
                    val message = "امکان ویرایش به دلیل عدم ثبت شدن بارکد وجود ندارد"
                    showCustomDialog(this@ActivityShowAssetInformation, DialogType.WARNING,
                        message, object : IClickListener {
                            override fun onClick(view: View?, dialog: Dialog) {
                                dialog.dismiss()
                            }
                        })
                    return
                }

                startActivity(intent)

            }

            override fun setBarcodeClicked(assetListItem: AssetListItem) {
                showBarcodeDialog(this@ActivityShowAssetInformation, object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {

                        barcode = (view as EditText).text.toString()
                        if (barcode.length == 9) {
                            Log.i(
                                "DEBUG",
                                "ActivityShowUserInformation showBarcodeDialog barcode=$barcode"
                            )
                            if (applicationContext?.let { isNetworkAvailable(it) } == true) {
                                Log.i(
                                    "DEBUG",
                                    "raw result detected.try to call setBarcodeForAsset"
                                )
                                assetId = assetListItem.assetID.toString().toInt()
                                showAssetInformationViewModel.setBarcodeForAsset(
                                    accessToken,
                                    assetListItem.assetID.toString(),
                                    assetListItem.assetTypeCode!!,
                                    barcode
                                )
                            } else {
                                val message = "اتصال شبکه برقرار نیست"
                                showCustomDialog(
                                    this@ActivityShowAssetInformation,
                                    DialogType.ERROR,
                                    message
                                )
                            }
                        } else {
                            val errorMessage = "طول بارکد باید ۹ رقم باشد"
                            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            }

        })

        rvAssetInformation.layoutManager = LinearLayoutManager(this)
        rvAssetInformation.itemAnimator = DefaultItemAnimator()
        expandableListAdapter2.addData(assetList)
        rvAssetInformation.adapter = expandableListAdapter2
        setupTextChangeListener()
    }

    private fun getUser() {
        showAssetInformationViewModel.getUser()
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        //ivInfo = findViewById(R.id.ivInfo)
        tvItem1 = findViewById(R.id.tvItem1)
        tvItem2 = findViewById(R.id.tvItem2)
        tvRecordCount = findViewById(R.id.tvRecordCount)
        tvProductName = findViewById(R.id.tvProductName)
        tvLocation = findViewById(R.id.tvLocation)
        tvAssetTypeName = findViewById(R.id.tvAssetTypeName)
        tvUsername = findViewById(R.id.tvUsername)
        acetSearchText = findViewById(R.id.acetSearchText)
        ivSearch = findViewById(R.id.ivSearch)
        //elvAssetInformation = findViewById(R.id.elvAssetInformation)
        rvAssetInformation = findViewById(R.id.rvAssetInformation)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun searchAsset(searchType: SearchType, accessToken: String = "", searchText: String = "") {
        Log.i("DEBUG", "ActivityShowUserInformation searchType=$searchType")
        showAssetInformationViewModel.getAssetList(searchType, accessToken, searchText)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        //ivInfo.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun observeViewModel() {

        showAssetInformationViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel appUserResponse success. dat=${it.data}"
                    )
                    if (!it.data?.token.isNullOrEmpty()) {
                        if (!applicationContext.let { isNetworkAvailable(it) }) {
                            val errorMessage = "اتصال اینترنت برقرار نیست"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        } else {
                            showAssetInformationViewModel.getESBToken(
                                it.data?.username.toString(),
                                it.data?.password.toString()
                            )
                        }
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityShowUserInformation observeViewModel appUSerResponse user token is null or empty!!!"
                        )
                        val errorMessage = "خطا در دریافت توکن کاربر از دیتابیس محلی"
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
                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
                        override fun onClick(view: View?, dialog: Dialog) {
                            dialog.dismiss()
                            finish()
                        }
                    })
                }
            }
        }

        showAssetInformationViewModel.esbTokenResponse.observe(this) {
            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    if (!it.data?.accessToken.isNullOrEmpty()) {
                        accessToken = it.data?.accessToken.toString()
                        searchAsset(searchTypeFromAPI, accessToken, searchText)

                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityShowUserInformation observeViewModel esbTokenResponse token is empty or null!!!"
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
                    var errorMessage = it.data?.error_description.toString()
                    if (errorMessage.isEmpty()) {
                        errorMessage = it.message.toString()
                    }
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

        showAssetInformationViewModel.getAssetListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel getAssetListResponse success.data=${it.data} "
                    )
                    if (it.data?.assetList != null) {
                        if (it.data.assetList.size > 0) {

                            when (searchTypeFromAPI) {

                                SearchType.Username, SearchType.NationalCode -> {
                                    setFormattedText(
                                        tvItem1, Color.WHITE,
                                        "کد ملی : ", englishToPersian(
                                            it.data.assetList[0]?.identityNo.toString()
                                        )
                                    )
                                    setFormattedText(
                                        tvItem2, Color.WHITE,
                                        "تحویل گیرنده : ", englishToPersian(
                                            it.data.assetList[0]?.actorName.toString()
                                        )
                                    )
                                }

                                SearchType.Barcode -> {
                                    setFormattedText(
                                        tvItem1, Color.WHITE,
                                        "بارکد : ", englishToPersian(
                                            it.data.assetList[0]?.barCode.toString()
                                        )
                                    )
                                }

                                SearchType.TagText -> {
                                    setFormattedText(
                                        tvItem1, Color.WHITE,
                                        "برچسب : ", englishToPersian(
                                            it.data.assetList[0]?.assetTag.toString()
                                        )
                                    )
                                }

                                SearchType.Location -> {
                                    setFormattedText(
                                        tvItem1, Color.WHITE,
                                        "محل استقرار : ", englishToPersian(
                                            it.data.assetList[0]?.assetLocationName.toString()
                                        )
                                    )
                                }
                                SearchType.AssetName -> {
                                    setFormattedText(
                                        tvItem1, Color.WHITE,
                                        "نام اموال : ", englishToPersian(
                                            it.data.assetList[0]?.assetLocationName.toString()
                                        )
                                    )
                                }

                            }

                            initializeExpandableListView(
                                it.data.assetList as List<AssetListItem>
                            )
                            tvRecordCount.text =
                                "تعداد : ${englishToPersian(it.data.assetList.size.toString())}"
                        } else {
                            Log.i(
                                "DEBUG",
                                "ActivityShowUserInformation observeViewModel getAssetListResponse success. list is empty. so we return back"
                            )
                            val message = "اموالی یافت نشد"
                            showSnackBarMessage(rvAssetInformation, message)
                            lifecycleScope.launch(Dispatchers.Main) {
                                delay(2000)
                                finish()
                            }
                        }
                    } else {
                        var errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                        if (errorMessage.isEmpty()) {
                            errorMessage = it.message.toString()
                        }
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
                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
                        override fun onClick(view: View?, dialog: Dialog) {
                            dialog.dismiss()
                            finish()
                        }
                    })
                }
            }
        }

        showAssetInformationViewModel.setBarcodeForAssetResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel setBarcodeForAssetResponse success.data=${it.data}"
                    )
                    if (it.data?.result == 100) {
                        val message = "بارکد به اموال با کد $assetId اضافه شد"
                        //showSnackBarMessage(elvAssetInformation, message)
                        showSnackBarMessage(rvAssetInformation, message)
                        getUser()
                    } else {
                        val errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
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

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel setBarcodeForAssetResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel setBarcodeForAssetResponse error.${it.message}"
                    )
                    var errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                    if (errorMessage.isEmpty()) {
                        errorMessage = it.message.toString()
                    }
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
    }

    override fun onResume() {
        super.onResume()
        getUser()
    }

    override fun onClick(p0: View?) {
        when (p0) {

            ivBack -> {
                finish()
            }

//            ivInfo -> {
//                val message = "این یک متن تستی است."
//                openHelpDialog(this@ActivityShowAssetInformation, message)
//            }

            ivSearch -> {
                if (acetSearchText.text?.isEmpty() == false) {
                    expandableListAdapter2.search(searchTypeLocally, acetSearchText.text.toString())
                } else {
                    val errorMessage = "متن جستجو را وارد نمایید"
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}