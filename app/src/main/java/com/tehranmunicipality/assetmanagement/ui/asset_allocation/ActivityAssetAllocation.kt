package com.tehranmunicipality.assetmanagement.ui.asset_allocation

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.ui.asset.ActivityModifyAsset
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityCameraBarcodeScanner
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.CustomDialog
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityAssetAllocation : BaseActivity(),
    View.OnClickListener,
    android.widget.CompoundButton.OnCheckedChangeListener {

    private val assetAllocationViewModel: AssetAllocationViewModel by viewModels()

    private lateinit var barcodeScannerLauncher: ActivityResultLauncher<Intent>

    private lateinit var ivBack: ImageView
    private lateinit var acetAssetBarcode: AppCompatEditText
    private lateinit var btnScanBarcode: Button

    private lateinit var ivDropDownMainBranch: ImageView
    private lateinit var ivDropDownAssetName: ImageView

    private lateinit var rlChooseMainBranch: RelativeLayout
    private lateinit var rlChooseAssetName: RelativeLayout

    private lateinit var tvChooseMainBranch: TextView
    private lateinit var tvChooseAssetName: TextView

    private lateinit var btnAddAsset: Button
    private lateinit var tvAssetName: TextView
    private lateinit var vwLoading: View

    private var articlePatternList: List<ArticlePatternListItem> = emptyList()
    private var articlePatternListItem: ArticlePatternListItem? = null

    private var goodList: List<GoodList> = emptyList()
    private var goodItem: GoodList? = null

    private var assetTypeCode: Int = 16
    private var barcode: String = "-1"
    private var productId: Int = -1
    private var accessToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // لانچر باید قبل از ورود Activity به وضعیت STARTED ثبت شود.
        barcodeScannerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            Log.d(
                "BarcodeScanner",
                "resultCode=${result.resultCode}, data=${result.data}"
            )

            if (result.resultCode == RESULT_OK) {
                val scannedBarcode =
                    result.data?.getStringExtra(ActivityCameraBarcodeScanner.EXTRA_BARCODE)

                if (!scannedBarcode.isNullOrBlank()) {
                    acetAssetBarcode.setText(scannedBarcode)
                    acetAssetBarcode.setSelection(
                        acetAssetBarcode.text?.length ?: 0
                    )

                    acetAssetBarcode.requestFocus()

                    Toast.makeText(
                        this,
                        "بارکد با موفقیت اسکن شد",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d("BarcodeScanner", "Barcode received: $scannedBarcode")
                } else {
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        "بارکدی از صفحه اسکن دریافت نشد"
                    )
                }
            } else {
                // لغو اسکن یا Back؛ صفحه تخصیص اموال باز می‌ماند.
                Log.d("BarcodeScanner", "Scanner canceled")
            }
        }

        setContentView(R.layout.activity_asset_allocation)

        bindView()
        setupClicks()
        observeViewModel()
        getUser()
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)

        acetAssetBarcode = findViewById(R.id.etAssetBarcode)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)

        ivDropDownMainBranch = findViewById(R.id.ivDropDownMainBranch)
        ivDropDownAssetName = findViewById(R.id.ivDropDownAssetName)

        rlChooseMainBranch = findViewById(R.id.rlChooseMainBranch)
        rlChooseAssetName = findViewById(R.id.rlChooseAssetName)

        tvChooseMainBranch = findViewById(R.id.tvChooseMainBranch)
        tvChooseAssetName = findViewById(R.id.tvChooseAssetName)

        btnAddAsset = findViewById(R.id.btnAddAsset)
        tvAssetName = findViewById(R.id.tvAssetName)
        vwLoading = findViewById(R.id.vwLoading)

        assetTypeCode = when (
            intent.getStringExtra("fromactivitynewormakhdoosh")
        ) {
            "rlDenominateAssetamir" -> 16
            "makhdoosh_activity" -> 17
            else -> 16
        }

        Log.i("AssetAllocation", "assetTypeCode=$assetTypeCode")
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        rlChooseMainBranch.setOnClickListener(this)
        rlChooseAssetName.setOnClickListener(this)
        btnScanBarcode.setOnClickListener(this)
        btnAddAsset.setOnClickListener(this)
    }

    private fun getUser() {
        assetAllocationViewModel.getUser()
    }

    private fun getGoodList(
        token: String,
        goodCode: Int,
        parentArticleParentId: Int,
        productName: String
    ) {
        assetAllocationViewModel.getGoodList(
            token,
            goodCode,
            parentArticleParentId,
            productName
        )
    }

    private fun getArticlePatternList(token: String) {
        assetAllocationViewModel.getArticlePatternList(token)
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun openBarcodeScanner() {
        if (!::barcodeScannerLauncher.isInitialized) {
            showCustomDialog(
                this,
                DialogType.ERROR,
                "امکان باز کردن اسکنر وجود ندارد"
            )
            return
        }

        try {
            val scannerIntent = Intent(
                this,
                ActivityCameraBarcodeScanner::class.java
            )

            barcodeScannerLauncher.launch(scannerIntent)
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Cannot open scanner", e)

            showCustomDialog(
                this,
                DialogType.ERROR,
                "خطا در باز کردن دوربین: ${e.localizedMessage ?: "خطای نامشخص"}"
            )
        }
    }

    private fun observeViewModel() {

        assetAllocationViewModel.appUserResponse.observe(this) { response ->
            when (response.status) {

                Status.SUCCESS -> {
                    hideLoading()

                    val user = response.data

                    if (user == null) {
                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            "خطا در دریافت اطلاعات کاربر از دیتابیس محلی"
                        )
                        return@observe
                    }

                    if (!isNetworkAvailable(applicationContext)) {
                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            "اتصال اینترنت برقرار نیست"
                        )
                        return@observe
                    }

                    assetAllocationViewModel.getESBToken(
                        user.username,
                        user.password
                    )
                }

                Status.LOADING -> showLoading()

                Status.ERROR -> {
                    hideLoading()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        response.message ?: "خطا در دریافت اطلاعات کاربر"
                    )
                }
            }
        }

        assetAllocationViewModel.esbTokenResponse.observe(this) { response ->
            when (response.status) {

                Status.SUCCESS -> {
                    hideLoading()

                    val token = response.data?.accessToken

                    if (!token.isNullOrBlank()) {
                        accessToken = token
                        getArticlePatternList(token)
                    } else {
                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            response.data?.error_description
                                ?: "دریافت توکن با خطا مواجه شد"
                        )
                    }
                }

                Status.LOADING -> showLoading()

                Status.ERROR -> {
                    hideLoading()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        response.message ?: "خطا در دریافت توکن"
                    )
                }
            }
        }

        assetAllocationViewModel.getArticlePatternListsResponse.observe(this) { response ->
            when (response.status) {

                Status.SUCCESS -> {
                    hideLoading()

                    val receivedList = response.data?.articlePatternList

                    if (!receivedList.isNullOrEmpty()) {
                        articlePatternList =
                            receivedList.filterIsInstance<ArticlePatternListItem>()
                    } else {
                        articlePatternList = emptyList()

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            "لیست گروه‌های اصلی مقداری ندارد"
                        )
                    }
                }

                Status.LOADING -> showLoading()

                Status.ERROR -> {
                    hideLoading()
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        response.message ?: "خطا در دریافت لیست گروه‌های اصلی"
                    )
                }
            }
        }

        assetAllocationViewModel.getGoodListResponse.observe(this) { response ->
            when (response.status) {

                Status.SUCCESS -> {
                    hideLoading()

                    val receivedList = response.data?.goodList

                    if (!receivedList.isNullOrEmpty()) {
                        goodList = receivedList
                    } else {
                        goodList = emptyList()
                        goodItem = null
                        tvChooseAssetName.text = ""

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            "لیست کالاها مقداری ندارد"
                        )
                    }
                }

                Status.LOADING -> showLoading()

                Status.ERROR -> {
                    hideLoading()

                    val errorMessage =
                        response.data?.detailError?.getOrNull(0)?.errorDesc
                            ?: response.message
                            ?: "خطا در دریافت لیست کالاها"

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage
                    )
                }
            }
        }

        assetAllocationViewModel.modifyAssetResponse.observe(this) { response ->
            when (response.status) {

                Status.SUCCESS -> {
                    hideLoading()

                    val result = response.data?.result

                    if (result != null && result != 0) {

                        Toast.makeText(
                            applicationContext,
                            "عملیات ثبت اموال با موفقیت انجام شد",
                            Toast.LENGTH_LONG
                        ).show()

                        val nextIntent = Intent(
                            this,
                            ActivityModifyAsset::class.java
                        ).apply {
                            putExtra("assetId", result.toString())
                            putExtra("assetTypeCode", assetTypeCode.toString())
                            putExtra(
                                "barcode",
                                acetAssetBarcode.text?.toString().orEmpty()
                            )
                            putExtra(
                                "productName",
                                goodItem?.productName.orEmpty()
                            )
                        }

                        startActivity(nextIntent)

                    } else {
                        val errorMessage =
                            response.data?.detailError?.getOrNull(0)?.errorDesc
                                ?: "ثبت اموال با خطا مواجه شد"

                        showCustomDialog(
                            this,
                            DialogType.ERROR,
                            errorMessage
                        )
                    }
                }

                Status.LOADING -> showLoading()

                Status.ERROR -> {
                    hideLoading()

                    val errorMessage =
                        response.data?.detailError?.getOrNull(0)?.errorDesc
                            ?: response.message
                            ?: "خطا در ثبت اموال"

                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        errorMessage
                    )
                }
            }
        }
    }

    private fun showArticlePatternListPopup(
        list: List<ArticlePatternListItem>
    ) {
        if (list.isEmpty()) {
            showCustomDialog(
                this,
                DialogType.ERROR,
                "لیست گروه‌های اصلی خالی است"
            )
            return
        }

        CustomDialog(
            this,
            "گروه اصلی را انتخاب کنید",
            list,
            ObjectType.ARTICLE_PATTERN_LIST,
            object : ItemClickListener {

                override fun articlePatternItemClicked(
                    item: ArticlePatternListItem
                ) {
                    articlePatternListItem = item

                    goodList = emptyList()
                    goodItem = null

                    tvChooseMainBranch.text =
                        item.articlePatternCode.orEmpty()

                    tvChooseAssetName.text = ""

                    val parentId = item.articlePatternID

                    if (parentId == null) {
                        showCustomDialog(
                            this@ActivityAssetAllocation,
                            DialogType.ERROR,
                            "شناسه گروه اصلی معتبر نیست"
                        )
                        return
                    }

                    getGoodList(
                        accessToken,
                        0,
                        parentId,
                        item.articlePatternName.orEmpty()
                    )
                }

                override fun goodListItemClicked(item: GoodList) {}

                override fun locationItemClicked(item: LocationListItem) {}

                override fun costCenterItemClicked(item: CostCenterListItem) {}

                override fun subCostCenterItemClicked(item: SubCostCenterListItem) {}

                override fun personListItemClicked(item: PersonListItem) {}

                override fun assetStatusItemClicked(item: AssetStatusListItem) {}
            }
        )
    }

    private fun showGoodListPopup(list: List<GoodList>) {
        if (list.isEmpty()) {
            showSnackBarMessage(
                ivDropDownAssetName,
                "لیست کالاها خالی است"
            )
            return
        }

        CustomDialog(
            this,
            "کالا را انتخاب کنید",
            list,
            ObjectType.GOOD_LIST,
            object : ItemClickListener {

                override fun articlePatternItemClicked(
                    item: ArticlePatternListItem
                ) {}

                override fun goodListItemClicked(item: GoodList) {
                    goodItem = item
                    tvChooseAssetName.text = item.productName.orEmpty()
                }

                override fun locationItemClicked(item: LocationListItem) {}

                override fun costCenterItemClicked(item: CostCenterListItem) {}

                override fun subCostCenterItemClicked(item: SubCostCenterListItem) {}

                override fun personListItemClicked(item: PersonListItem) {}

                override fun assetStatusItemClicked(item: AssetStatusListItem) {}
            }
        )
    }

    override fun onClick(view: View?) {
        when (view) {

            ivBack -> {
                finish()
            }

            rlChooseMainBranch -> {
                showArticlePatternListPopup(articlePatternList)
            }

            rlChooseAssetName -> {
                if (articlePatternListItem == null) {
                    showSnackBarMessage(
                        ivDropDownAssetName,
                        "ابتدا گروه اصلی را انتخاب کنید"
                    )
                } else {
                    showGoodListPopup(goodList)
                }
            }

            btnScanBarcode -> {
                openBarcodeScanner()
            }

            btnAddAsset -> {
                if (!isInputValid()) return

                barcode = acetAssetBarcode.text?.toString()?.trim().orEmpty()
                productId = goodItem?.productID ?: -1

                if (productId == -1) {
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        "شناسه کالای انتخاب‌شده معتبر نیست"
                    )
                    return
                }

                if (accessToken.isBlank()) {
                    showCustomDialog(
                        this,
                        DialogType.ERROR,
                        "توکن دسترسی دریافت نشده است"
                    )
                    return
                }

                Log.d("AssetAllocation", "assetTypeCode=$assetTypeCode")
                Log.d("AssetAllocation", "barcode=$barcode")
                Log.d("AssetAllocation", "productId=$productId")

                assetAllocationViewModel.modifyAsset(
                    accessToken,
                    null,
                    assetTypeCode,
                    barcode,
                    "",
                    productId
                )
            }
        }
    }

    private fun isInputValid(): Boolean {

        val currentBarcode = acetAssetBarcode.text?.toString()?.trim().orEmpty()

        val errorMessage = when {
            !isNetworkAvailable(applicationContext) ->
                "اتصال اینترنت برقرار نیست"

            accessToken.isBlank() ->
                "توکن دسترسی دریافت نشده است"

            articlePatternListItem == null ->
                "گروه اصلی انتخاب نشده است"

            goodItem == null ->
                "نام کالا انتخاب نشده است"

            currentBarcode.isBlank() ->
                "بارکد اموال را وارد کنید"

            currentBarcode.length != BARCODE_LENGTH ->
                "طول بارکد باید ${englishToPersian(BARCODE_LENGTH.toString())} رقم باشد"

            else -> null
        }

        if (errorMessage != null) {
            showCustomDialog(
                this,
                DialogType.ERROR,
                errorMessage
            )
            return false
        }

        return true
    }

    override fun onCheckedChanged(
        buttonView: android.widget.CompoundButton,
        isChecked: Boolean
    ) {
        // انتخاب نوع اموال از طریق Intent انجام می‌شود.
    }
}