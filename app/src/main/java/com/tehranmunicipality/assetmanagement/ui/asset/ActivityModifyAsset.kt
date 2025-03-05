package com.tehranmunicipality.assetmanagement.ui.asset

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.CustomDialog
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityModifyAsset : BaseActivity(), View.OnClickListener {

    private val modifyAssetViewModel: ModifyAssetViewModel by viewModels()
    private lateinit var tvCostCenter: TextView
    private lateinit var tvSubCostCenter: TextView
    private lateinit var tvLocation: TextView

    private lateinit var tvPerson: TextView
    private lateinit var tvAssetStatus: TextView
    private lateinit var acbRegisterBarcode: AppCompatButton
    private lateinit var acbRegisterAsset: AppCompatButton
    private lateinit var tvBarcodeNumberShow2Header: TextView
    private lateinit var etNationalCode: EditText
    private lateinit var tvBarcode: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAssetName: TextView
    private lateinit var tvAssetTag: TextView
    private lateinit var vwLoading: View
    private var type = ""
    private var fromActivity = ""
    private var accessToken = ""
    private var assetId = 0
    private var assetHistoryId: Int? = 0
    private var assetTypeCode = 1
    private var assetTypeName = ""
    private var barcode = ""
    private var assetTag = ""
    private var actorId = 0
    private var actorName = ""
    private var costCenterId = 0
    private var costCenterName = ""
    private var costCenterCode = ""
    private var subCostCenterId = 0
    private var subCostCenterName = ""
    private var subCostCenterCode = ""
    private var assetLocationId = 0
    private var assetLocationName = ""
    private var assetStatusCode = 0

    private lateinit var costCenterList: List<CostCenterListItem>
    private lateinit var subCostCenterList: List<SubCostCenterListItem>
    private lateinit var locationList: List<LocationListItem>
    private lateinit var assetStatusList: List<AssetStatusListItem>

    private lateinit var costCenterListItem: CostCenterListItem
    private lateinit var subCostCenterListItem: SubCostCenterListItem
    private lateinit var locationListItem: LocationListItem
    private lateinit var personListItem: PersonListItem
    private lateinit var assetStatusListItem: AssetStatusListItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_asset)

        bindView()
        setupClicks()
        getAssetInfoFromIntent()
        setupTextChangeListener()
        getUser()
        observeViewModel()
    }

    private fun getAssetInfoFromIntent() {
        if (intent.hasExtra("fromActivity")) {
            fromActivity = (intent.getStringExtra("fromActivity").toString())
        }

        if (intent.hasExtra("assetId")) {
            assetId = (intent.getStringExtra("assetId").toString()).toInt()
        }
        if (intent.hasExtra("assetHistoryId")) {
            assetHistoryId = (intent.getStringExtra("assetHistoryId").toString()).toInt()
        }
        if (intent.hasExtra("assetTypeCode")) {
            assetTypeCode = (intent.getStringExtra("assetTypeCode").toString()).toInt()
        }
        if (intent.hasExtra("barcode")) {
            barcode = intent.getStringExtra("barcode").toString()
        }
        if (intent.hasExtra("assetTag")) {
            assetTag = intent.getStringExtra("assetTag").toString()
        }
        Log.i("DEBUG", "ActivityModifyAsset getAssetInfoFromIntent assetId=$assetId")
        Log.i("DEBUG", "ActivityModifyAsset getAssetInfoFromIntent barcode=$barcode")
        Log.i("DEBUG", "ActivityModifyAsset getAssetInfoFromIntent assetTypeCode=$assetTypeCode")

        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type").toString()
            if (type == "edit") {
                costCenterId = (intent.getStringExtra("costCenterID").toString()).toInt()
                costCenterName = intent.getStringExtra("costCenterName").toString()
                costCenterCode = intent.getStringExtra("costCenterCode").toString()
                subCostCenterId = (intent.getStringExtra("subCostCenterID").toString()).toInt()
                subCostCenterName = intent.getStringExtra("subCostCenterName").toString()
                subCostCenterCode = intent.getStringExtra("subCostCenterCode").toString()
                assetLocationId = (intent.getStringExtra("assetLocationID")).toString().toInt()
                assetLocationName = (intent.getStringExtra("assetLocationName")).toString()
                actorId = (intent.getStringExtra("actorID")).toString().toInt()
                actorName = (intent.getStringExtra("actorName")).toString()
                assetTypeCode = (intent.getStringExtra("assetTypeCode")).toString().toInt()
                assetStatusCode = (intent.getStringExtra("assetStatusCode")).toString().toInt()
                assetTypeName = intent.getStringExtra("assetTypeName").toString()

                Log.i("DEBUG", "ActivityModifyAsset intent data costCenterId=$costCenterId")
                Log.i("DEBUG", "ActivityModifyAsset intent data subCostCenterId=$subCostCenterId")
                Log.i("DEBUG", "ActivityModifyAsset intent data assetLocationId=$assetLocationId")
                Log.i("DEBUG", "ActivityModifyAsset intent data actorId=${actorId}")
                Log.i("DEBUG", "ActivityModifyAsset intent data assetTypeCode=$assetTypeCode")
                tvTitle.text = " ویرایش اموال"

            }
        } else {
            acbRegisterBarcode.visibility = View.GONE
            tvTitle.text = "تخصیص اموال"
        }
       var amirnovin= intent.getStringExtra("productName").toString()
        tvAssetName.text =amirnovin
        setFormattedText(
            tvAssetTag,
            " شماره برچسب : ",
            englishToPersian(assetTag)
        )
        tvBarcode.text = englishToPersian(barcode)

        if (barcode!=""){
            tvBarcode.text = englishToPersian(barcode)
        }else{
            tvBarcode.text = "فاقد بارکد"

        }
    }

    private fun getUser() {
        modifyAssetViewModel.getUser()
    }

    private fun bindView() {
        tvTitle = findViewById(R.id.tvTitle)
        ivBack = findViewById(R.id.ivBack)
        tvBarcodeNumberShow2Header = findViewById(R.id.tvBarcodeNumberShow2Header)
        tvBarcode = findViewById(R.id.tvBarcode)
        tvAssetName = findViewById(R.id.tvAssetName)
        tvAssetTag = findViewById(R.id.tvAssetTag)
        etNationalCode = findViewById(R.id.etNationalCode)
        tvCostCenter = findViewById(R.id.tvCostCenter)
        tvSubCostCenter = findViewById(R.id.tvSubCostCenter)
        tvLocation = findViewById(R.id.tvLocation)
        tvPerson = findViewById(R.id.tvPerson)
       tvAssetStatus = findViewById(R.id.tvAssetStatus)
        acbRegisterBarcode = findViewById(R.id.acbRegisterBarcode)
        acbRegisterAsset = findViewById(R.id.acbRegisterAsset)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        acbRegisterBarcode.setOnClickListener(this)
        acbRegisterAsset.setOnClickListener(this)
        //tvCostCenter.setOnClickListener(this)
        //tvSubCostCenter.setOnClickListener(this)
        tvLocation.setOnClickListener(this)
        //tvPerson.setOnClickListener(this)
        tvAssetStatus.setOnClickListener(this)
    }

    private fun setupTextChangeListener() {

        etNationalCode.addTextChangedListener {
            Log.i("DEBUG", "count=${it?.length}")
            if (it?.length == 10) {
                if (!applicationContext.let { isNetworkAvailable(it) }) {
                    val errorMessage = "اتصال اینترنت برقرار نیست"
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                } else {
                    modifyAssetViewModel.getPersonList(accessToken, identityNo = it.toString())
                }
            }
        }
    }

    private fun observeViewModel() {

        modifyAssetViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    if (it.data != null) {
                        if (!applicationContext.let { isNetworkAvailable(it) }) {
                            val errorMessage = "اتصال اینترنت برقرار نیست"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        } else {
                            modifyAssetViewModel.getESBToken(it.data.username, it.data.password)
                        }
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityModifyAsset observeViewModel appUserResponse success. appUser has no data!!!"
                        )
                        val errorMessage = "خطا در دریافت اطلاعات کاربر از دیتابیس محلی"
                        showCustomDialog(this@ActivityModifyAsset, DialogType.ERROR, errorMessage)
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i("DEBUG", "ActivityModifyAsset observeViewModel appUserResponse loading")
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel appUserResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR,
                        errorMessage, object : IClickListener {
                            override fun onClick(view: View?, dialog: Dialog) {
                                dialog.dismiss()
                                finish()
                            }
                        })
                }
            }
        }

        modifyAssetViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i("DEBUG", "ActivityModifyAsset observeViewModel esbTokenResponse success")
                    if (!it.data?.accessToken.isNullOrEmpty()) {
                        accessToken = it.data?.accessToken.toString()
                        modifyAssetViewModel.getCostCenterListAsync(accessToken)
                    } else {
                        val errorMessage = "خطا در دریافت توکن ESB"

                        showCustomDialog(this, DialogType.ERROR, errorMessage,
                            object : IClickListener {
                                override fun onClick(view: View?, dialog: Dialog) {
                                    dialog.dismiss()
                                    finish()
                                }
                            })
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i("DEBUG", "ActivityModifyAsset observeViewModel esbTokenResponse loading")
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel esbTokenResponse error.${it.message}"
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

        modifyAssetViewModel.getPersonListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getPersonListResponse Success.data=${it.data}"
                    )
                    if (it.data != null) {
                        if (it.data.personList?.isNotEmpty() == true) {
                            costCenterId =
                                it.data.personList.get(0)?.costCenterId.toString().toInt()
                            subCostCenterId =
                                it.data.personList.get(0)?.subCostCenterId.toString().toInt()
                            actorId = it.data.personList.get(0)?.actorId.toString().toInt()
                            tvCostCenter.text = it.data.personList.get(0)?.costCenterName
                            tvSubCostCenter.text = it.data.personList.get(0)?.subCostCenterName
                            tvPerson.text =
                                "${it.data.personList.get(0)?.firstName} ${it.data.personList.get(0)?.lastName}"
                            modifyAssetViewModel.getLocationList(accessToken)
                        } else {
                            val errorMessage = "اطلاعاتی برای کد ملی وارد شده یافت نشد"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                            clearInputs()
                        }
                    }
                }

                Status.LOADING -> {

                }
                Status.ERROR -> {

                }
            }
        }

        modifyAssetViewModel.getCostCenterListAsyncResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getCostCenterListAsyncResponse success.data=${it.data}"
                    )
                    if (!it.data?.costCenterList.isNullOrEmpty()) {
                        costCenterList = it.data?.costCenterList as List<CostCenterListItem>
                    } else {
                        val errorMessage = "لیست مرکز هزینه اصلی خالی است"
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
                        "ActivityModifyAsset observeViewModel getCostCenterListAsync loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getCostCenterListAsync error.${it.message}"
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

        modifyAssetViewModel.getSubCostCenterListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getSubCostCenterListResponse success.${it.data}"
                    )
                    if (!it.data?.subCostCenterList.isNullOrEmpty()) {
                        subCostCenterList =
                            it.data?.subCostCenterList as List<SubCostCenterListItem>
                    } else {
                        val errorMessage = "لیست مرکز هزینه فرعی خالی است"
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
                        "ActivityModifyAsset observeViewModel getSubCostCenterListResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getSubCostCenterList error.${it.message}"
                    )
                    val errorMesage = it.message.toString()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMesage, object : IClickListener {
                            override fun onClick(view: View?, dialog: Dialog) {
                                dialog.dismiss()
                                finish()
                            }
                        })
                }
            }
        }

        modifyAssetViewModel.getLocationListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getLocationListResponse success.${it.data}"
                    )
                    if (!it.data?.locationList.isNullOrEmpty()) {
                        locationList = it.data?.locationList as List<LocationListItem>
                    } else {
                        val errorMessage = "لیست محل های استقرار خالی است"
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                    }
                    modifyAssetViewModel.getAssetStatusList(accessToken)
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getLocationListResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getLocationListResponse error.${it.message}"
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

        modifyAssetViewModel.assetStatusListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getAssetStatusListResponse success.${it.data}"
                    )
                    if (!it.data?.assetStatusList.isNullOrEmpty()) {
                        assetStatusList = it.data?.assetStatusList as List<AssetStatusListItem>
                    } else {
                        val errorMessage = "لیست وضعیت اموال خالی است"
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
                        "ActivityModifyAsset observeViewModel getAssetStatusListResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getAssetStatusListResponse error.${it.message}"
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

        modifyAssetViewModel.modifyAssetResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset modifyAssetResponse success.data=${it.data}"
                    )
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i("DEBUG", "ActivityModifyAsset modifyAssetResponse loading")
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset modifyAssetResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }

        modifyAssetViewModel.setBarcodeForAssetResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel setBarcodeForAssetResponse success.data=${it.data}"
                    )



                    if (it.data?.result == 100) {
                        val message = "بارکد به اموال با کد $assetId اضافه شد"
                        showSnackBarMessage(acbRegisterBarcode, message)
                        tvBarcode.text = barcode
                        modifyAssetViewModel.getModifyAssetHistory(
                            accessToken,
                            actorId,
                            getCurrentDateTime(),
                            null,
                            assetId,
                            assetLocationId.toString().toInt(),
                            assetStatusCode!!,
                            "",
                            subCostCenterId!!
                        )


                    } else {
                        val errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage, object : IClickListener {
                                override fun onClick(view: View?, dialog: Dialog) {
                                    dialog.dismiss()
                                }
                            })
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel setBarcodeForAssetResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel setBarcodeForAssetResponse error.${it.message}"
                    )
                    val errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage, object : IClickListener {
                            override fun onClick(view: View?, dialog: Dialog) {
                                dialog.dismiss()
                            }
                        })
                }
            }
        }

        modifyAssetViewModel.getModifyAssetHistoryResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getModifyAssetHistoryResponse success.data=${it.data}"
                    )
                    if (it.data?.result == 100) {
                        val message = "تغییرات به اموال اعمال شد"
                        showCustomDialog(
                            this,
                            DialogType.SUCCESS,
                            message, object : IClickListener {
                                override fun onClick(view: View?, dialog: Dialog) {
                                    dialog.dismiss()
                                    finish()
                                }
                            })
                    } else if (it.data?.result.toString().toInt() >= 0) {
                        val message = "اموال جدید ثبت شد"
                        showCustomDialog(
                            this,
                            DialogType.SUCCESS,
                            message, object : IClickListener {
                                override fun onClick(view: View?, dialog: Dialog) {
                                    dialog.dismiss()
                                    finish()
                                }
                            })
                    } else {
                        val errorMessage = "خطا در افزوده شدن تغییرات به اموال"
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
                        "ActivityModifyAsset observeViewModel getModifyAssetHistoryResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityModifyAsset observeViewModel getModifyAssetHistoryResponse error.${it.message}"
                    )
                    val errorMessage = it.data?.detailError?.get(0).toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }
    }

    private fun clearInputs() {
        costCenterId = 0
        subCostCenterId = 0
        actorId = 0
        tvCostCenter.text = ""
        tvSubCostCenter.text = ""
        tvPerson.text = ""
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    override fun onClick(p0: View?) {

        when (p0) {

            ivBack -> {
                finish()
            }

            acbRegisterBarcode -> {
                //scanBarcode()
                showBarcodeDialog(this@ActivityModifyAsset, object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        barcode = (view as EditText).text.toString()
                        if (barcode.length == 11) {
                            Log.i(
                                "DEBUG",
                                "ActivityModifyBarcode showBarcodeDialog barcode=$barcode"
                            )
                            if (applicationContext?.let { isNetworkAvailable(it) } == true) {
                                Log.i("DEBUG", "raw result detected.try to call setBarcodeForAsset")
                                modifyAssetViewModel.setBarcodeForAsset(
                                    accessToken,
                                    assetId.toString(),
                                    assetTypeCode,
                                    barcode
                                )



                            } else {
                                val message = "اتصال اینترنت برقرار نیست"
                                showCustomDialog(
                                    this@ActivityModifyAsset,
                                    DialogType.ERROR,
                                    message
                                )
                            }
                        } else {
                            val errorMessage = "طول بارکد بایستی 11 رقم باشد"
                            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            }

            acbRegisterAsset -> {

                if (isInputValid()) {
                    if (type != "edit") {
                        assetHistoryId = null
                    }
                    if (!applicationContext.let { isNetworkAvailable(it) }) {
                        val errorMessage = "اتصال اینترنت برقرار نیست"
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                    } else {
                        modifyAssetViewModel.getModifyAssetHistory(
                            accessToken,
                            actorId,
                            getCurrentDateTime(),
                            assetHistoryId,
                            assetId,
                            locationListItem.assetLocationID.toString().toInt(),
                            assetStatusCode,
                            "",
                            subCostCenterId
                        )
                    }
                }
            }

            tvLocation -> {
                if (::locationList.isInitialized) {
                    val customDialog =
                        CustomDialog(this, "محل استقرار را وارد نمایید", locationList,
                            ObjectType.LOCATION_LIST,
                            object : ItemClickListener {
                                override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                                    TODO("Not yet implemented")
                                }

                                override fun goodListItemClicked(goodListItem: GoodList) {
                                    TODO("Not yet implemented")
                                }

                                override fun locationItemClicked(_locationListItem: LocationListItem) {
                                    tvLocation.setText(_locationListItem.assetLocationName)
                                    locationListItem = _locationListItem
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
                } else {
                    val errorMessage = "لیست محل استقرار خالی است"
                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
                        override fun onClick(view: View?, dialog: Dialog) {
                            dialog.dismiss()
                        }
                    })
                }
            }

            tvAssetStatus -> {
                if (::assetStatusList.isInitialized) {
                    val customDialog = CustomDialog(this,
                        "وضعیت اموال را انتخاب نمایید",
                        assetStatusList,
                        ObjectType.ASSET_STATUS,
                        object : ItemClickListener {
                            override fun articlePatternItemClicked(articlePatternListItem: ArticlePatternListItem) {
                                TODO("Not yet implemented")
                            }

                            override fun goodListItemClicked(goodListItem: GoodList) {
                                TODO("Not yet implemented")
                            }

                            override fun locationItemClicked(locationListItem: LocationListItem) {
                                TODO("Not yet implemented")
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

                            override fun assetStatusItemClicked(_assetStatusListItem: AssetStatusListItem) {
                                tvAssetStatus.setText(_assetStatusListItem.assetStatusName)
                                assetStatusListItem = _assetStatusListItem

                                assetStatusCode = assetStatusListItem.assetStatusCode!!
                                Log.i("amirnovin",assetStatusCode.toString())
                            }
                        })
                } else {
                    val errorMessage = "لیست وضعیت های کالا خالی است"
                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
                        override fun onClick(view: View?, dialog: Dialog) {
                            dialog.dismiss()
                        }
                    })
                }
            }
        }
    }


    private fun isInputValid(): Boolean {

        var isValid = true
        var errorMessage = ""

//        if (!::assetStatusListItem.isInitialized) {
//            isValid = false
//            errorMessage = "هیچ وضعیت کالایی انتخاب نشده است"
//        }

        if (actorId == 0) {
            isValid = false
            errorMessage = "هیچ گیرنده ای انتخاب نشده است"
        }

        if (!::locationListItem.isInitialized) {
            isValid = false
            errorMessage = "هیچ محل استقراری انتخاب نشده است"
        }

        if (subCostCenterId == 0) {
            isValid = false
            errorMessage = "مرکز هزینه ی فرعی مقداری ندارد"
        }

        if (costCenterId == 0) {
            isValid = false
            errorMessage = "مرکز هزینه ی اصلی مقداری ندارد"
        }

        if (!applicationContext.let { isNetworkAvailable(it) }) {
            isValid = false
            errorMessage = "اتصال اینترنت برقرار نیست"
        }

        if (!isValid) {
            showSnackBarMessage(acbRegisterAsset, errorMessage)
        }

        return isValid
    }
}