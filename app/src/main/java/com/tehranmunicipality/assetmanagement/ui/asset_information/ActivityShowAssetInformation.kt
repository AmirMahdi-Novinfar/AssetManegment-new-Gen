package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.data.model.AssetListItem
import com.tehranmunicipality.assetmanagement.data.model.GetAssetListResponse
import com.tehranmunicipality.assetmanagement.ui.asset.ActivityModifyAsset
import com.tehranmunicipality.assetmanagement.ui.asset.ModifyAssetViewModel
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.SpinnerFilterAdapter
import com.tehranmunicipality.assetmanagement.util.DialogType
import com.tehranmunicipality.assetmanagement.util.IClickListener
import com.tehranmunicipality.assetmanagement.util.IClickListenerWithEditText
import com.tehranmunicipality.assetmanagement.util.IClickListenergone
import com.tehranmunicipality.assetmanagement.util.Resource
import com.tehranmunicipality.assetmanagement.util.SearchType
import com.tehranmunicipality.assetmanagement.util.Status
import com.tehranmunicipality.assetmanagement.util.englishToPersian
import com.tehranmunicipality.assetmanagement.util.getCurrentDateTime
import com.tehranmunicipality.assetmanagement.util.isNetworkAvailable
import com.tehranmunicipality.assetmanagement.util.setFormattedText
import com.tehranmunicipality.assetmanagement.util.showAssetmoreDialog
import com.tehranmunicipality.assetmanagement.util.showBarcodeDialog
import com.tehranmunicipality.assetmanagement.util.showCustomDialog
import com.tehranmunicipality.assetmanagement.util.showSnackBarMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class ActivityShowAssetInformation : BaseActivity(), View.OnClickListener {

    private var scanTargetEditText: EditText? = null

    // دریافت نتیجه اسکن دوربین
    private val barcodeScannerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val scannedBarcode =
                    result.data?.getStringExtra("barcodeResult")

                if (!scannedBarcode.isNullOrBlank()) {

                    scanTargetEditText?.let { editText ->
                        editText.setText(scannedBarcode)
                        editText.setSelection(editText.text?.length ?: 0)
                    }
                }
            }

            // بعد از دریافت نتیجه، مرجع EditText پاک می‌شود
            scanTargetEditText = null
        }

    private val showAssetInformationViewModel: ShowAssetInformationViewModel by viewModels()
    private val modifyAssetViewModel: ModifyAssetViewModel by viewModels()

    private lateinit var ivBack: ImageView
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
    private var nationalCode = ""
    private var actorID = 0
    private var assetLocationID = 0
    private var assetstatenum = 0
    private var assetStatusCode = 0
    private var subCostCenterID = 0
    private var barcode = ""
    private var note = ""

    private lateinit var searchTypeFromAPI: SearchType
    private var searchTypeLocally = SearchType.AssetName

    private lateinit var ivDropDownLocation2: ImageView

    private val searchArray = arrayOf(
        "دارای بارکد",
        "تاریخ ویرایش",
        "حروف الفبا"
    )

    var responsetype = ""

    private lateinit var choose_data_pars_mode: Spinner

    var myit: Resource<GetAssetListResponse> =
        Resource.success(GetAssetListResponse())

    lateinit var moreAssetsContainer11: LinearLayout
    lateinit var moreAssetsContainer22: LinearLayout
    lateinit var moreAssetsContainer33: LinearLayout
    lateinit var moreAssetsContainer44: LinearLayout

    // جلوگیری از اجرای چندباره درخواست اولیه
    private var initialRequestStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_asset_information)

        bindView()

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(
                window.decorView.windowToken,
                0
            )
        }

        setupClicks()

        if (intent.hasExtra("fromActivity")) {

            when (intent.getStringExtra("fromActivity")) {

                "ActivitySearchByBarcode" -> {

                    if (intent.hasExtra("barcodeId")) {

                        searchText =
                            intent.getStringExtra("barcodeId").toString()

                        searchTypeFromAPI = SearchType.Barcode
                        searchTypeLocally = SearchType.Barcode

                        Log.i("DEBUG", "searchType is barcode")

                        acetSearchText.filters =
                            arrayOf(InputFilter.LengthFilter(BARCODE_LENGTH))

                        acetSearchText.keyListener =
                            DigitsKeyListener.getInstance("0123456789")

                    } else if (intent.hasExtra("tagText")) {

                        searchText =
                            intent.getStringExtra("tagText").toString()

                        searchTypeFromAPI = SearchType.TagText
                        searchTypeLocally = SearchType.TagText

                        Log.i("DEBUG", "searchType is tagtext")

                        acetSearchText.filters =
                            arrayOf(InputFilter.LengthFilter(11))

                        acetSearchText.keyListener =
                            DigitsKeyListener.getInstance("0123456789")
                    }
                }

                "ActivitySearchByAssetLocation" -> {

                    if (intent.hasExtra("assetLocationId")) {

                        searchText =
                            intent.getStringExtra("assetLocationId").toString()

                        searchTypeFromAPI = SearchType.Location
                        searchTypeLocally = SearchType.Location

                        Log.i("DEBUG", "searchType is location")
                        Log.i("DEBUG22", searchText)

                        acetSearchText.filters =
                            arrayOf(InputFilter.LengthFilter(20))

                        acetSearchText.keyListener =
                            DigitsKeyListener.getInstance("0123456789")

                        acetSearchText.inputType =
                            InputType.TYPE_CLASS_TEXT
                    }
                }

                "ActivitySearchByUsernameOrNationalCode" -> {

                    if (intent.hasExtra("nationalCode")) {

                        searchText =
                            intent.getStringExtra("nationalCode").toString()

                        searchTypeFromAPI = SearchType.NationalCode
                    }
                }

                "ActivityPersonList" -> {

                    if (intent.hasExtra("username")) {

                        searchText =
                            intent.getStringExtra("username").toString()

                        searchTypeFromAPI = SearchType.NationalCode
                    }
                }
            }

            // ابتدا Observerها ثبت می‌شوند
            observeViewModel()

            // سپس درخواست اولیه فقط یک بار اجرا می‌شود
            if (!initialRequestStarted) {
                initialRequestStarted = true
                getUser()
            }
        }
    }

    private fun setupTextChangeListener() {

        acetSearchText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun afterTextChanged(p0: Editable?) {

                p0?.let {

                    val fixedQuery =
                        it.toString().replace('ی', 'ي')

                    expandableListAdapter2.search(
                        searchTypeLocally,
                        fixedQuery
                    )

                    tvRecordCount.text =
                        "تعداد: ${englishToPersian(expandableListAdapter2.itemCount.toString())}"
                }
            }
        })
    }

    private fun getUser() {
        showAssetInformationViewModel.getUser()
    }

    private fun bindView() {

        choose_data_pars_mode = findViewById(R.id.selectfilterasset)
        ivBack = findViewById(R.id.ivBack)

        tvItem1 = findViewById(R.id.tvItem1)
        tvItem2 = findViewById(R.id.tvItem2)

        tvRecordCount = findViewById(R.id.tvRecordCount)

        tvProductName = findViewById(R.id.tvProductName)
        tvLocation = findViewById(R.id.tvLocation)
        tvAssetTypeName = findViewById(R.id.tvAssetTypeName)
        tvUsername = findViewById(R.id.tvUsername)

        acetSearchText = findViewById(R.id.acetSearchText)
        acetSearchText.clearFocus()

        ivSearch = findViewById(R.id.ivSearch)

        rvAssetInformation = findViewById(R.id.rvAssetInformation)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun searchAsset(
        searchType: SearchType,
        accessToken: String = "",
        searchText: String = ""
    ) {

        Log.i(
            "DEBUG",
            "ActivityShowUserInformation searchType=$searchType"
        )

        showAssetInformationViewModel.getAssetList(
            searchType,
            accessToken,
            searchText
        )
    }

    private fun setupClicks() {

        ivBack.setOnClickListener(this)
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
                        "ActivityShowUserInformation appUserResponse success. data=${it.data}"
                    )

                    if (!it.data?.token.isNullOrEmpty()) {

                        if (!applicationContext.let { isNetworkAvailable(it) }) {

                            val errorMessage = "اتصال اینترنت برقرار نیست"

                            showCustomDialog(
                                this,
                                DialogType.ERROR,
                                errorMessage
                            )

                        } else {

                            showAssetInformationViewModel.getESBToken(
                                it.data?.username.toString(),
                                it.data?.password.toString()
                            )
                        }

                    } else {

                        Log.i(
                            "DEBUG",
                            "User token is null or empty"
                        )

                        val errorMessage =
                            "خطا در دریافت توکن کاربر از دیتابیس محلی"

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()
                                    finish()
                                }
                            }
                        )
                    }
                }

                Status.LOADING -> {

                    showLoading()

                    Log.i(
                        "DEBUG",
                        "appUserResponse loading"
                    )
                }

                Status.ERROR -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "appUserResponse error: ${it.message}"
                    )

                    val errorMessage = it.message.toString()

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage,
                        object : IClickListener {

                            override fun onClick(
                                view: View?,
                                dialog: Dialog
                            ) {
                                dialog.dismiss()
                                finish()
                            }
                        }
                    )
                }
            }
        }

        showAssetInformationViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "esbTokenResponse success. data=${it.data}"
                    )

                    if (!it.data?.accessToken.isNullOrEmpty()) {

                        accessToken =
                            it.data?.accessToken.toString()

                        searchAsset(
                            searchTypeFromAPI,
                            accessToken,
                            searchText
                        )

                    } else {

                        Log.i(
                            "DEBUG",
                            "ESB access token is empty or null"
                        )

                        val errorMessage = it.message.toString()

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()
                                    finish()
                                }
                            }
                        )
                    }
                }

                Status.LOADING -> {

                    showLoading()

                    Log.i(
                        "DEBUG",
                        "esbTokenResponse loading"
                    )
                }

                Status.ERROR -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "esbTokenResponse error: ${it.message}"
                    )

                    var errorMessage =
                        it.data?.error_description.toString()

                    if (errorMessage.isEmpty()) {
                        errorMessage = it.message.toString()
                    }

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage,
                        object : IClickListener {

                            override fun onClick(
                                view: View?,
                                dialog: Dialog
                            ) {
                                dialog.dismiss()
                                finish()
                            }
                        }
                    )
                }
            }
        }

        showAssetInformationViewModel.getAssetListResponse.observe(this) {

            myit = it

            when (it.status) {

                Status.SUCCESS -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "getAssetListResponse success. data=${it.data}"
                    )

                    if (it.data?.assetList != null) {

                        if (responsetype == "setAssetMoreClicked") {

                            if (it.data.assetList.isNotEmpty()) {

                                moreAssetsContainer22.visibility =
                                    View.VISIBLE

                                disableViewAndChildren(
                                    moreAssetsContainer11
                                )

                                note +=
                                    "برچسب های اضافه : " +
                                            it.data.assetList[0]!!.assetTag +
                                            " , "

                            } else {

                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    "اموالی یافت نشد",
                                    object : IClickListener {

                                        override fun onClick(
                                            view: View?,
                                            dialog: Dialog
                                        ) {
                                            dialog.dismiss()
                                        }
                                    }
                                )
                            }

                        } else if (responsetype == "setAssetMoreClicked2") {

                            if (it.data.assetList.isNotEmpty()) {

                                note +=
                                    it.data.assetList[0]!!.assetTag + " , "

                                moreAssetsContainer33.visibility =
                                    View.VISIBLE

                                disableViewAndChildren(
                                    moreAssetsContainer22
                                )

                            } else {

                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    "اموالی یافت نشد",
                                    object : IClickListener {

                                        override fun onClick(
                                            view: View?,
                                            dialog: Dialog
                                        ) {
                                            dialog.dismiss()
                                        }
                                    }
                                )
                            }

                        } else if (responsetype == "setAssetMoreClicked3") {

                            if (it.data.assetList.isNotEmpty()) {

                                note +=
                                    it.data.assetList[0]!!.assetTag + " , "

                                moreAssetsContainer44.visibility =
                                    View.VISIBLE

                                disableViewAndChildren(
                                    moreAssetsContainer33
                                )

                            } else {

                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    "اموالی یافت نشد",
                                    object : IClickListener {

                                        override fun onClick(
                                            view: View?,
                                            dialog: Dialog
                                        ) {
                                            dialog.dismiss()
                                        }
                                    }
                                )
                            }

                        } else if (responsetype == "setAssetMoreClicked4") {

                            if (it.data.assetList.isNotEmpty()) {

                                note +=
                                    it.data.assetList[0]!!.assetTag + " , "

                                disableViewAndChildren(
                                    moreAssetsContainer44
                                )

                            } else {

                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    "اموالی یافت نشد",
                                    object : IClickListener {

                                        override fun onClick(
                                            view: View?,
                                            dialog: Dialog
                                        ) {
                                            dialog.dismiss()
                                        }
                                    }
                                )
                            }

                        } else {

                            Log.i(
                                "DEdug",
                                "vaghtiassetmoremizanimiad inja1"
                            )

                            if (it.data.assetList.isNotEmpty()) {

                                when (searchTypeFromAPI) {

                                    SearchType.Username,
                                    SearchType.NationalCode -> {

                                        setFormattedText(
                                            tvItem1,
                                            Color.WHITE,
                                            "کد ملی : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.identityNo.toString()
                                            )
                                        )

                                        nationalCode =
                                            it.data.assetList[0]
                                                ?.identityNo.toString()

                                        setFormattedText(
                                            tvItem2,
                                            Color.WHITE,
                                            "تحویل گیرنده : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.actorName.toString()
                                            )
                                        )
                                    }

                                    SearchType.Barcode -> {

                                        setFormattedText(
                                            tvItem1,
                                            Color.WHITE,
                                            "بارکد : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.barCode.toString()
                                            )
                                        )
                                    }

                                    SearchType.TagText -> {

                                        setFormattedText(
                                            tvItem1,
                                            Color.WHITE,
                                            "برچسب : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.assetTag.toString()
                                            )
                                        )
                                    }

                                    SearchType.Location -> {

                                        setFormattedText(
                                            tvItem1,
                                            Color.WHITE,
                                            "محل استقرار : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.assetLocationName.toString()
                                            )
                                        )
                                    }

                                    SearchType.AssetName -> {

                                        setFormattedText(
                                            tvItem1,
                                            Color.WHITE,
                                            "نام اموال : ",
                                            englishToPersian(
                                                it.data.assetList[0]
                                                    ?.assetLocationName.toString()
                                            )
                                        )
                                    }
                                }

                                initializeExpandableListView(
                                    it.data.assetList
                                        .sortedByDescending { item -> item?.barCode }
                                            as List<AssetListItem>
                                )

                                Log.i(
                                    "DEdug",
                                    "vaghtiassetmoremizanimiad inja2"
                                )

                                spinnerconfig()

                                tvRecordCount.text =
                                    "تعداد : ${englishToPersian(it.data.assetList.size.toString())}"

                            } else {

                                Log.i(
                                    "DEBUG",
                                    "Asset list is empty"
                                )

                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    "اموالی یافت نشد",
                                    object : IClickListener {

                                        override fun onClick(
                                            view: View?,
                                            dialog: Dialog
                                        ) {
                                            dialog.dismiss()
                                            finish()
                                        }
                                    }
                                )
                            }
                        }

                    } else {

                        var errorMessage =
                            it.data?.detailError?.get(0)?.errorDesc.toString()

                        if (errorMessage.isEmpty()) {
                            errorMessage = it.message.toString()
                        }

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()
                                    finish()
                                }
                            }
                        )
                    }
                }

                Status.LOADING -> {

                    showLoading()

                    Log.i(
                        "DEBUG",
                        "getAssetListResponse loading"
                    )
                }

                Status.ERROR -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "getAssetListResponse error: ${it.message}"
                    )

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        it.message.toString(),
                        object : IClickListener {

                            override fun onClick(
                                view: View?,
                                dialog: Dialog
                            ) {
                                dialog.dismiss()
                                finish()
                            }
                        }
                    )
                }
            }
        }

        // Observer ثبت بارکد
        showAssetInformationViewModel.setBarcodeForAssetResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "setBarcodeForAssetResponse success. data=${it.data}"
                    )

                    if (it.data?.result == 100) {

                        val message =
                            "بارکد به اموال با کد $assetId اضافه شد"

                        showSnackBarMessage(
                            rvAssetInformation,
                            message
                        )

                        // رفرش اطلاعات فقط بعد از ثبت موفق بارکد
                        getUser()

                        modifyAssetViewModel.getModifyAssetHistory(
                            accessToken,
                            actorID,
                            getCurrentDateTime(),
                            null,
                            assetId,
                            assetLocationID.toString().toInt(),
                            assetStatusCode,
                            "",
                            subCostCenterID
                        )

                    } else {

                        val errorMessage =
                            it.data?.detailError?.get(0)?.errorDesc.toString()

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()
                                }
                            }
                        )
                    }
                }

                Status.LOADING -> {

                    showLoading()

                    Log.i(
                        "DEBUG",
                        "setBarcodeForAssetResponse loading"
                    )
                }

                Status.ERROR -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "setBarcodeForAssetResponse error: ${it.message}"
                    )

                    var errorMessage =
                        it.data?.detailError?.get(0)?.errorDesc.toString()

                    if (errorMessage.isEmpty()) {
                        errorMessage = it.message.toString()
                    }

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage,
                        object : IClickListener {

                            override fun onClick(
                                view: View?,
                                dialog: Dialog
                            ) {
                                dialog.dismiss()
                            }
                        }
                    )
                }
            }
        }

        showAssetInformationViewModel.getModifyAssetHistoryResponse.observe(this) {

            Log.i(
                "DEBUG",
                "ActivityShowAssetInformation getModifyAssetHistoryResponse"
            )

            when (it.status) {

                Status.SUCCESS -> {

                    hideLoading()

                    Log.i(
                        "DEBUG",
                        "getModifyAssetHistoryResponse success"
                    )

                    if (it.data?.detailError?.isEmpty() == true) {

                        val message =
                            "برچسب های اضافه به اموال اعمال شد"

                        showCustomDialog(
                            this,
                            DialogType.SUCCESS,
                            message,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()

                                    showAssetInformationViewModel.getAssetList(
                                        SearchType.NationalCode,
                                        accessToken,
                                        nationalCode
                                    )
                                }
                            }
                        )

                    } else {

                        Log.i(
                            "DEBUG",
                            "getModifyAssetHistoryResponse error"
                        )

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            "خطا در افزوده شدن تغییرات به اموال",
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {
                                    dialog.dismiss()
                                }
                            }
                        )
                    }
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.ERROR -> {

                    hideLoading()

                    val errorMessage =
                        it.data?.detailError?.get(0).toString()

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage
                    )
                }
            }
        }
    }

    /*
     * عمداً onResume را override نکرده‌ایم.
     *
     * چون برگشت از دوربین باعث اجرای مجدد onResume می‌شود
     * و نباید در این مرحله getUser فراخوانی شود.
     */

    override fun onClick(p0: View?) {

        when (p0) {

            ivBack -> {
                finish()
            }

            ivSearch -> {

                if (acetSearchText.text?.isEmpty() == false) {

                    expandableListAdapter2.search(
                        searchTypeLocally,
                        acetSearchText.text.toString()
                    )

                } else {

                    Toast.makeText(
                        applicationContext,
                        "متن جستجو را وارد نمایید",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    public fun initializeExpandableListView(
        assetList: List<AssetListItem>
    ) {

        val intent = Intent(
            this,
            ActivityModifyAsset::class.java
        )

        expandableListAdapter2 =
            AssetExpandableListAdapter2(
                this,
                object : AssetItemClickListener {

                    override fun editItemClicked(
                        assetListItem: AssetListItem
                    ) {

                        responsetype = "edititem"

                        Log.i(
                            "DEBUG",
                            "assetID=${assetListItem.assetID}"
                        )

                        Log.i(
                            "DEBUG",
                            "assetTypeCode=${assetListItem.assetTypeCode}"
                        )

                        Log.i(
                            "DEBUG",
                            "barCode=${assetListItem.barCode}"
                        )

                        intent.putExtra("type", "edit")

                        assetListItem.assetID?.let {

                            assetId = it.toString().toInt()

                            intent.putExtra(
                                "assetId",
                                it.toString()
                            )
                        }

                        assetListItem.assetCode?.let {
                            intent.putExtra("assetCode", it.toString())
                        }

                        assetListItem.assetStateCode?.let {
                            intent.putExtra(
                                "assetStateCode",
                                it.toString()
                            )
                        }

                        assetListItem.assetTypeCode?.let {
                            intent.putExtra(
                                "assetTypeCode",
                                it.toString()
                            )
                        }

                        assetListItem.barCode?.let {
                            intent.putExtra("barcode", it.toString())
                        }

                        assetListItem.assetTag?.let {
                            intent.putExtra("assetTag", it.toString())
                        }

                        assetListItem.productName?.let {
                            intent.putExtra(
                                "productName",
                                it.toString()
                            )

                            Log.i("ghandman", it.toString())
                        }

                        assetListItem.assetHistoryId?.let {
                            intent.putExtra(
                                "assetHistoryId",
                                it.toString()
                            )
                        }

                        assetListItem.costCenterID?.let {
                            intent.putExtra(
                                "costCenterID",
                                it.toString()
                            )
                        }

                        assetListItem.costCenterName?.let {
                            intent.putExtra(
                                "costCenterName",
                                it.toString()
                            )
                        }

                        assetListItem.costCenterCode?.let {
                            intent.putExtra(
                                "costCenterCode",
                                it.toString()
                            )
                        }

                        assetListItem.subCostCenterID?.let {

                            intent.putExtra(
                                "subCostCenterID",
                                it.toString()
                            )

                            subCostCenterID = it.toString().toInt()
                        }

                        assetListItem.subCostCenterName?.let {
                            intent.putExtra(
                                "subCostCenterName",
                                it.toString()
                            )
                        }

                        assetListItem.subCostCenterCode?.let {
                            intent.putExtra(
                                "subCostCenterCode",
                                it.toString()
                            )
                        }

                        assetListItem.assetLocationID?.let {

                            intent.putExtra(
                                "assetLocationID",
                                it.toString()
                            )

                            assetLocationID = it.toString().toInt()
                        }

                        assetListItem.assetStateCode?.let {

                            assetstatenum = it.toString().toInt()
                        }

                        assetListItem.assetLocationName?.let {
                            intent.putExtra(
                                "assetLocationName",
                                it.toString()
                            )
                        }

                        assetListItem.actorID?.let {

                            intent.putExtra(
                                "actorID",
                                it.toString()
                            )

                            actorID = it.toString().toInt()
                        }

                        assetListItem.actorName?.let {
                            intent.putExtra(
                                "actorName",
                                it.toString()
                            )
                        }

                        assetListItem.assetStatusCode?.let {

                            intent.putExtra(
                                "assetStatusCode",
                                it.toString()
                            )

                            assetStatusCode = it
                        }

                        assetListItem.assetTypeName?.let {
                            intent.putExtra(
                                "assetTypeName",
                                it.toString()
                            )
                        }

                        if (assetListItem.barCode.isNullOrEmpty()) {

                            showCustomDialog(
                                this@ActivityShowAssetInformation,
                                DialogType.WARNING,
                                "ابتدا بارکد تخصیص داده شود",
                                object : IClickListener {

                                    override fun onClick(
                                        view: View?,
                                        dialog: Dialog
                                    ) {
                                        dialog.dismiss()
                                    }
                                }
                            )

                            return
                        }

                        startActivity(intent)

                        Log.d(
                            "DEBUG",
                            assetListItem.assetStateCode.toString()
                        )
                    }

                    override fun setBarcodeClicked(
                        assetListItem: AssetListItem
                    ) {

                        showBarcodeDialog(
                            this@ActivityShowAssetInformation,
                            object : IClickListener {

                                override fun onClick(
                                    view: View?,
                                    dialog: Dialog
                                ) {

                                    responsetype = "setBarcodeClicked"

                                    barcode =
                                        (view as EditText).text.toString()

                                    if (barcode.length == 11) {

                                        Log.i(
                                            "DEBUG",
                                            "barcode=$barcode"
                                        )

                                        if (isNetworkAvailable(applicationContext)) {

                                            Log.i(
                                                "DEBUG",
                                                "Calling setBarcodeForAsset"
                                            )

                                            assetListItem.assetID?.let {

                                                assetId = it.toString().toInt()
                                            }

                                            assetListItem.subCostCenterID?.let {

                                                subCostCenterID =
                                                    it.toString().toInt()
                                            }

                                            assetListItem.assetLocationID?.let {

                                                assetLocationID =
                                                    it.toString().toInt()
                                            }

                                            assetListItem.actorID?.let {

                                                actorID =
                                                    it.toString().toInt()
                                            }

                                            assetListItem.assetStatusCode?.let {

                                                assetStatusCode = it
                                            }

                                            // API ثبت بارکد فقط با زدن دکمه ثبت اجرا می‌شود
                                            showAssetInformationViewModel.setBarcodeForAsset(
                                                accessToken,
                                                assetListItem.assetID.toString(),
                                                assetListItem.assetTypeCode!!,
                                                barcode
                                            )

                                        } else {

                                            showCustomDialog(
                                                this@ActivityShowAssetInformation,
                                                DialogType.ERROR,
                                                "اتصال شبکه برقرار نیست"
                                            )
                                        }

                                    } else {

                                        Toast.makeText(
                                            applicationContext,
                                            "طول بارکد باید 11 رقم باشد",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },

                            // باز کردن دوربین برای اسکن بارکد
                            onScanClick = { editText ->

                                scanTargetEditText = editText

                                val scannerIntent = Intent(
                                    this@ActivityShowAssetInformation,
                                    ActivityCameraBarcodeScanner::class.java
                                )

                                barcodeScannerLauncher.launch(scannerIntent)
                            }
                        )
                    }

                    override fun setAssetMoreClicked(
                        assetListItem: AssetListItem
                    ) {

                        if (assetListItem.barCode.isNullOrEmpty()) {

                            showCustomDialog(
                                this@ActivityShowAssetInformation,
                                DialogType.WARNING,
                                "برای ثبت برچسب اضافه باید اول بارکد ثبت گردد"
                            )

                        } else {

                            showAssetmoreDialog(
                                this@ActivityShowAssetInformation,

                                object : IClickListenerWithEditText {

                                    override fun onClick(
                                        view: View?,
                                        editText: EditText?,
                                        dialog: Dialog
                                    ) {

                                        when (view?.id) {

                                            R.id.sabtmoreasset -> {

                                                responsetype =
                                                    "setAssetMoreClicked"

                                                val enteredNote =
                                                    editText?.text.toString()

                                                if (enteredNote.isNotEmpty()) {

                                                    Log.i(
                                                        "DEBUGnote",
                                                        enteredNote
                                                    )

                                                    showAssetInformationViewModel.getAssetList(
                                                        SearchType.TagText,
                                                        accessToken,
                                                        enteredNote
                                                    )

                                                } else {

                                                    showCustomDialog(
                                                        this@ActivityShowAssetInformation,
                                                        DialogType.ERROR,
                                                        "لطفا مقدار برچسب را وارد کنید."
                                                    )
                                                }
                                            }

                                            R.id.sabtmoreasset2 -> {

                                                responsetype =
                                                    "setAssetMoreClicked2"

                                                val enteredNote =
                                                    editText?.text.toString()

                                                if (enteredNote.isNotEmpty()) {

                                                    Log.i(
                                                        "DEBUGnote",
                                                        enteredNote
                                                    )

                                                    showAssetInformationViewModel.getAssetList(
                                                        SearchType.TagText,
                                                        accessToken,
                                                        enteredNote
                                                    )

                                                } else {

                                                    showCustomDialog(
                                                        this@ActivityShowAssetInformation,
                                                        DialogType.ERROR,
                                                        "لطفا مقدار برچسب را وارد کنید."
                                                    )
                                                }
                                            }

                                            R.id.sabtmoreasset3 -> {

                                                responsetype =
                                                    "setAssetMoreClicked3"

                                                val enteredNote =
                                                    editText?.text.toString()

                                                if (enteredNote.isNotEmpty()) {

                                                    Log.i(
                                                        "DEBUGnote",
                                                        enteredNote
                                                    )

                                                    showAssetInformationViewModel.getAssetList(
                                                        SearchType.TagText,
                                                        accessToken,
                                                        enteredNote
                                                    )

                                                } else {

                                                    showCustomDialog(
                                                        this@ActivityShowAssetInformation,
                                                        DialogType.ERROR,
                                                        "لطفا مقدار برچسب را وارد کنید."
                                                    )
                                                }
                                            }

                                            R.id.sabtmoreasset4 -> {

                                                responsetype =
                                                    "setAssetMoreClicked4"

                                                val enteredNote =
                                                    editText?.text.toString()

                                                if (enteredNote.isNotEmpty()) {

                                                    Log.i(
                                                        "DEBUGnote",
                                                        enteredNote
                                                    )

                                                    showAssetInformationViewModel.getAssetList(
                                                        SearchType.TagText,
                                                        accessToken,
                                                        enteredNote
                                                    )

                                                } else {

                                                    showCustomDialog(
                                                        this@ActivityShowAssetInformation,
                                                        DialogType.ERROR,
                                                        "لطفا مقدار برچسب را وارد کنید."
                                                    )
                                                }
                                            }

                                            R.id.acbConfirm -> {

                                                if (note.isNotEmpty()) {

                                                    if (isNetworkAvailable(applicationContext)) {

                                                        assetListItem.assetID?.let {

                                                            assetId = it.toInt()
                                                        }

                                                        assetListItem.subCostCenterID?.let {

                                                            subCostCenterID = it.toInt()
                                                        }

                                                        assetListItem.assetLocationID?.let {

                                                            assetLocationID = it.toInt()
                                                        }

                                                        assetListItem.actorID?.let {

                                                            actorID = it.toInt()
                                                        }

                                                        assetListItem.assetStatusCode?.let {

                                                            assetStatusCode = it
                                                        }

                                                        showAssetInformationViewModel.getModifyAssetHistory(
                                                            accessToken,
                                                            actorID,
                                                            getCurrentDateTime(),
                                                            null,
                                                            assetId,
                                                            assetLocationID,
                                                            assetStatusCode,
                                                            note,
                                                            subCostCenterID
                                                        )

                                                    } else {

                                                        showCustomDialog(
                                                            this@ActivityShowAssetInformation,
                                                            DialogType.ERROR,
                                                            "اتصال شبکه برقرار نیست"
                                                        )
                                                    }

                                                } else {

                                                    showCustomDialog(
                                                        this@ActivityShowAssetInformation,
                                                        DialogType.ERROR,
                                                        "برچسب اضافه وارد نشده است"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },

                                object : IClickListenergone {

                                    override fun onViewCreated(
                                        asset_title_assetmore: TextView,
                                        asset_tag_assetmore: TextView,
                                        moreAssetsContainer: LinearLayout,
                                        moreAssetsContainer2: LinearLayout,
                                        moreAssetsContainer3: LinearLayout,
                                        moreAssetsContainer4: LinearLayout
                                    ) {

                                        moreAssetsContainer11 =
                                            moreAssetsContainer

                                        moreAssetsContainer22 =
                                            moreAssetsContainer2

                                        moreAssetsContainer33 =
                                            moreAssetsContainer3

                                        moreAssetsContainer44 =
                                            moreAssetsContainer4

                                        asset_title_assetmore.text =
                                            assetListItem.productName

                                        if (assetListItem.assetTag?.isNotEmpty() == true) {

                                            asset_tag_assetmore.text =
                                                "شماره برچسب: " +
                                                        assetListItem.assetTag

                                        } else {

                                            asset_tag_assetmore.text =
                                                "بدون شماره برچسب"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            )

        rvAssetInformation.layoutManager =
            LinearLayoutManager(this)

        rvAssetInformation.itemAnimator =
            DefaultItemAnimator()

        expandableListAdapter2.addData(assetList)

        rvAssetInformation.adapter =
            expandableListAdapter2

        setupTextChangeListener()
    }

    private fun spinnerconfig() {

        val spineradapter =
            SpinnerFilterAdapter(this, searchArray)

        spineradapter.spinnerfilteradapter(
            this,
            searchArray
        )

        ivDropDownLocation2 =
            findViewById(R.id.ivDropcodee)

        choose_data_pars_mode.adapter =
            spineradapter

        choose_data_pars_mode.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                ivDropDownLocation2.setImageResource(
                    R.drawable.ic_up
                )

                choose_data_pars_mode.performClick()
            }

            false
        }

        choose_data_pars_mode.onItemSelectedListener =
            object : AdapterView.OnItemClickListener,
                AdapterView.OnItemSelectedListener {

                override fun onItemClick(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {

                    ivDropDownLocation2.setImageResource(
                        R.drawable.ic_down
                    )
                }

                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    p2: Int,
                    p3: Long
                ) {

                    ivDropDownLocation2.setImageResource(
                        R.drawable.ic_down
                    )

                    val sortedList = when (p2) {

                        0 -> {
                            myit.data?.assetList
                                ?.sortedByDescending { it?.barCode }
                        }

                        1 -> {
                            myit.data?.assetList
                                ?.sortedByDescending { it?.assetHistoryDate }
                        }

                        2 -> {
                            myit.data?.assetList
                                ?.sortedBy { it?.productName }
                        }

                        else -> null
                    }

                    sortedList?.let {

                        initializeExpandableListView(
                            it as List<AssetListItem>
                        )

                        expandableListAdapter2.notifyDataSetChanged()

                        rvAssetInformation.adapter =
                            expandableListAdapter2
                    }
                }

                override fun onNothingSelected(
                    p0: AdapterView<*>?
                ) {

                    ivDropDownLocation2.setImageResource(
                        R.drawable.ic_down
                    )
                }
            }
    }

    fun disableViewAndChildren(view: View) {

        view.isEnabled = false

        if (view is ViewGroup) {

            for (i in 0 until view.childCount) {

                disableViewAndChildren(
                    view.getChildAt(i)
                )
            }
        }
    }
}