package com.tehranmunicipality.assetmanagement.ui.asset_allocation

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.LoginFilter.UsernameFilterGMail
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.radiobutton.MaterialRadioButton
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.ui.asset.ActivityModifyAsset
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.CustomDialog
import com.tehranmunicipality.assetmanagement.ui.asset_information.ItemClickListener
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityAssetAllocation : BaseActivity(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener {

    private val assetAllocationViewModel: AssetAllocationViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var acetAssetBarcode: AppCompatEditText
    private lateinit var ivDropDownMainBranch: ImageView
    private lateinit var ivDropDownAssetName: ImageView
    private lateinit var rlChooseMainBranch: RelativeLayout
    private lateinit var rlChooseAssetName: RelativeLayout
    private lateinit var tvChooseMainBranch: TextView
    private lateinit var tvChooseAssetName: TextView
    private lateinit var mrbNew: MaterialRadioButton
    private lateinit var mrbScratch: MaterialRadioButton
    private lateinit var btnAddAsset: Button
    private lateinit var tvAssetName: TextView
    private lateinit var vwLoading: View
    private lateinit var articlePatternList: List<ArticlePatternListItem>
    private var articlePatternListItem: ArticlePatternListItem? = null
    private lateinit var goodList: List<GoodList>
    private var goodItem: GoodList? = null
    private var assetTypeCode: Int = 16
    private var barcode: Long = -1
    private var productId: Int = -1
    private var accessToken = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_allocation)

        bindView()
        setupClicks()
        setupRadioChangesListeners()
        getUser()
        observeViewModel()
    }

    private fun getUser() {
        assetAllocationViewModel.getUser()
    }

    private fun getGoodList(
        accessToken: String,
        goodCode: Int,
        parentArticleParentId: Int,
        productName: String
    ) {
        assetAllocationViewModel.getGoodList(
            accessToken,
            goodCode,
            parentArticleParentId,
            productName
        )
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        acetAssetBarcode = findViewById(R.id.etAssetBarcode)
        ivDropDownMainBranch = findViewById(R.id.ivDropDownMainBranch)
        ivDropDownAssetName = findViewById(R.id.ivDropDownAssetName)
        rlChooseMainBranch = findViewById(R.id.rlChooseMainBranch)
        rlChooseAssetName = findViewById(R.id.rlChooseAssetName)
        tvChooseMainBranch = findViewById(R.id.tvChooseMainBranch)
        tvChooseAssetName = findViewById(R.id.tvChooseAssetName)
        mrbNew = findViewById(R.id.mrbNew)
        mrbScratch = findViewById(R.id.mrbScratch)
        btnAddAsset = findViewById(R.id.btnAddAsset)
        tvAssetName = findViewById(R.id.tvAssetName)
        vwLoading = findViewById(R.id.vwLoading)
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun setupRadioChangesListeners() {
        mrbNew.setOnCheckedChangeListener(this)
        mrbScratch.setOnCheckedChangeListener(this)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        rlChooseMainBranch.setOnClickListener(this)
        rlChooseAssetName.setOnClickListener(this)
        btnAddAsset.setOnClickListener(this)
        acetAssetBarcode.setOnClickListener(this)
    }

    private fun observeViewModel() {

        assetAllocationViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {

                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel appUserResponse success.data=${it.data}"
                    )
                    if (it.data != null) {
                        if (!applicationContext.let { isNetworkAvailable(it) }) {
                            val errorMessage = "اتصال اینترنت برقرار نیست"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        } else {
                            assetAllocationViewModel.getESBToken(it.data.username, it.data.password)
                        }
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityAssetAllocation observeViewModel appUserResponse success.data=null"
                        )
                        Log.i(
                            "DEBUG",
                            "so we have no username and password to call esbToken api!!!"
                        )
                        val errorMessage = "خطا در دریافت اطلاعات کاربر از دیتابیس محلی"
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel appUserResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel appUserResponse error"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }

        assetAllocationViewModel.esbTokenResponse.observe(this) {
            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    if (it.data!!.accessToken!!.isNotEmpty()) {
                        accessToken = it.data.accessToken.toString()
                        getArticlePatternList(it.data.accessToken.toString())

                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivityAssetAllocation observeViewModel esbTokenResponse success.token is empty!!!"
                        )
                        if (!it.data.error?.isEmpty()!!) {
                            val errorMessage = it.data.error_description.toString()
                            showCustomDialog(
                                this, DialogType.ERROR, errorMessage, object : IClickListener {
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
                        "ActivityAssetAllocation observeViewModel esbTokenResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel esbTokenResponse error.${it.message}"
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

        assetAllocationViewModel.getArticlePatternListsResponse.observe(this) {

            when (it.status) {
                Status.SUCCESS -> {
                    hideLoading()
                    if (it.data != null) {
                        Log.i(
                            "DEBUG",
                            "ActivityAssetAllocation observeViewModel getArticlePatternListsResponse success.data=${it.data}"
                        )
                        if (!it.data.articlePatternList.isNullOrEmpty()) {
                            articlePatternList =
                                it.data.articlePatternList as List<ArticlePatternListItem>
                        } else {
                            val errorMessage = "لیست شاخه اصلی مقداری ندارد"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
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
                        "ActivityAssetAllocation observeViewModel getArticlePatternListsResponse loading"
                    )
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation observeViewModel getArticlePatternListsResponse error"
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

        assetAllocationViewModel.getGoodListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i("DEBUG", "ActivityAssetAllocation getGoodList success.data=${it.data}")
                    if (!it.data?.goodList.isNullOrEmpty()) {
                        goodList = it.data?.goodList!!
                    } else {
                        val errorMessage = "لیست کالاها مقداری ندارد"
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
                    Log.i("DEBUG", "ActivityAssetAllocation getGoodList loading")
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i("DEBUG", "ActivityAssetAllocation getGoodList error.->${it.message}")
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

        assetAllocationViewModel.modifyAssetResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation modifyAssetResponse success. data=${it.data}"
                    )
                    var message = ""
                    if (it.data?.result != 0) {
                        message = "عملیات ثبت اموال با موفقیت انجام شد"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ActivityModifyAsset::class.java)
                        intent.putExtra("assetId", it.data?.result.toString())
                        intent.putExtra("assetTypeCode", assetTypeCode.toString())
                        intent.putExtra("barcode", acetAssetBarcode.text.toString())
                        intent.putExtra("productName", goodItem?.productName.toString())
                        startActivity(intent)
                    } else {
                        val errorMessage = it.data.detailError?.get(0)?.errorDesc.toString()
                        showCustomDialog(this, DialogType.ERROR, errorMessage)
                    }
                }

                Status.LOADING -> {
                    showLoading()
                    Log.i("DEBUG", "ActivityAssetAllocation modifyAssetResponse loading")
                }

                Status.ERROR -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityAssetAllocation modifyAssetResponse error.->${it.message}"
                    )
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
        }
    }

    private fun showArticlePatternListPopup(_articlePatternList: List<ArticlePatternListItem>) {

        val customDialog1 = CustomDialog(this, "گروه اصلی را انتخاب کنید", _articlePatternList,
            ObjectType.ARTICLE_PATTERN_LIST,
            object : ItemClickListener {
                override fun articlePatternItemClicked(_articlePatternListItem: ArticlePatternListItem) {
                    goodList = emptyList()
                    goodItem = null
                    articlePatternListItem = _articlePatternListItem
                    tvChooseMainBranch.setText(articlePatternListItem!!.articlePatternCode)
                    tvChooseAssetName.setText("")
                    getGoodList(
                        accessToken,
                        0,
                        articlePatternListItem!!.articlePatternID!!.toInt(),
                        articlePatternListItem!!.articlePatternName.toString()
                    )
                }

                override fun goodListItemClicked(goodListItem: GoodList) {
                    TODO("Not yet implemented")
                }

                override fun locationItemClicked(_locationListItem: LocationListItem) {
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

                override fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun showGoodListPopup(_goodList: List<GoodList>) {

        val customDialog1 = CustomDialog(this, "کالا را انتخاب کنید", _goodList,
            ObjectType.GOOD_LIST,
            object : ItemClickListener {
                override fun articlePatternItemClicked(_articlePatternListItem: ArticlePatternListItem) {
                    TODO("Not yet implemented")
                }

                override fun goodListItemClicked(_goodListItem: GoodList) {
                    goodItem = _goodListItem
                    tvChooseAssetName.setText(_goodListItem.productName)
                }

                override fun locationItemClicked(_locationListItem: LocationListItem) {
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

                override fun assetStatusItemClicked(assetStatusListItem: AssetStatusListItem) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun getArticlePatternList(accessToken: String) {
        assetAllocationViewModel.getArticlePatternList(accessToken)
    }

    override fun onClick(p0: View?) {
        when (p0) {

            ivBack -> {
                finish()
            }

            acetAssetBarcode -> {
                showBarcodeDialog(this@ActivityAssetAllocation, object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        acetAssetBarcode.setText((view as AppCompatEditText).text.toString())
                    }
                })
            }

            rlChooseMainBranch -> {
                if (::articlePatternList.isInitialized) {
                    showArticlePatternListPopup(articlePatternList)
                } else {
                    val errorMessage = "لیست مراکز هزینه خالی است"
                    showSnackBarMessage(ivDropDownMainBranch, errorMessage)
                }
            }

            rlChooseAssetName -> {
                if (::goodList.isInitialized) {
                    if (!goodList.isEmpty()) {
                        showGoodListPopup(goodList)
                    } else {
                        val errorMessage = "لیست کالاها خالی است"
                        showSnackBarMessage(ivDropDownAssetName, errorMessage)
                    }
                } else {
                    val errorMessage = "لیست کالاها خالی است"
                    showSnackBarMessage(ivDropDownAssetName, errorMessage)
                }
            }

            btnAddAsset -> {
                Log.i("DEBUG", "class name = ${javaClass.simpleName}")
                if (isInputValid()) {
                    barcode = acetAssetBarcode.text.toString().toLong()
                    Log.i("DEBUG", "assetTypeCode=${assetTypeCode}")
                    Log.i("DEBUG", "barcode=${barcode}")
                    Log.i("DEBUG", "productId=${productId}")
                    var assetId: Int? = null
                    assetId = null

                    assetAllocationViewModel.modifyAsset(
                        accessToken,
                        assetId,
                        assetTypeCode,
                        barcode,
                        "",
                        goodItem!!.productID
                    )
                }
            }
        }
    }

    private fun isInputValid(): Boolean {

        var isValid = true
        var errorMessage = ""

        if (!applicationContext.let { isNetworkAvailable(it) }) {
            isValid = false
            errorMessage = "اتصال اینترنت برقرار نیست"
        }

        if (goodItem == null) {
            isValid = false
            errorMessage = "نام کالا انتخاب نشده است"
        } else {
            if (goodList.isEmpty()) {
                isValid = false
                errorMessage = "لیست کالاها خالی است"
            }
        }

        if (articlePatternListItem == null) {
            isValid = false
            errorMessage = "گروه اصلی انتخاب نشده است"
        } else {
            if (articlePatternList.isEmpty()) {
                isValid = false
                errorMessage = "گروه اصلی خالی است"
            }
        }

        if (acetAssetBarcode.text!!.isEmpty()) {
            isValid = false
            errorMessage = "بارکد اموال را وارد کنید"
        }

        if (!acetAssetBarcode.text!!.isEmpty() && acetAssetBarcode.text!!.length != BARCODE_LENGTH) {
            isValid = false
            errorMessage = "طول بارکد باید ${englishToPersian(BARCODE_LENGTH.toString())} رقم باشد"
        }

        if (!isValid) {
            showSnackBarMessage(btnAddAsset, errorMessage)
        }
        return isValid
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0) {

            mrbNew -> {
                if (p1) {
                    mrbNew.setButtonDrawable(R.drawable.ic_selected)
                    mrbScratch.setButtonDrawable(R.drawable.ic_unselected)
                    assetTypeCode = 16

                }
            }

            mrbScratch -> {
                if (p1) {
                    mrbNew.setButtonDrawable(R.drawable.ic_unselected)
                    mrbScratch.setButtonDrawable(R.drawable.ic_selected)
                    assetTypeCode = 17
                }
            }
        }
    }
}