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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.tehranmunicipality.assetmanagement.util.showDialog2
import com.tehranmunicipality.assetmanagement.util.showSnackBarMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ActivityShowAssetInformation : BaseActivity(), View.OnClickListener {

    private val showAssetInformationViewModel: ShowAssetInformationViewModel by viewModels()
    private val modifyAssetViewModel: ModifyAssetViewModel by viewModels()

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
    private var actorID = 0
    private var assetLocationID = 0
    private var assetStatusCode = 0
    private var subCostCenterID = 0
    private var barcode = ""
    private var note = "برچسب های اضافه : "
    private lateinit var searchTypeFromAPI: SearchType
    private var searchTypeLocally = SearchType.AssetName
    private lateinit var ivDropDownLocation2:ImageView
    val searchArray = arrayOf("دارای بارکد", "تاریخ ویرایش","حروف الفبا")
    var responsetype=""
    private lateinit var choose_data_pars_mode:Spinner
     var myit:Resource<GetAssetListResponse> = Resource.success(GetAssetListResponse())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_asset_information)


        bindView()
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }


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
                        acetSearchText.filters = arrayOf(InputFilter.LengthFilter(11))
                        acetSearchText.keyListener = DigitsKeyListener.getInstance("0123456789")
                    }
                }

                "ActivitySearchByAssetLocation" -> {
                    if (intent.hasExtra("assetLocationId")) {
                        //assetLocationId = intent.getStringExtra("assetLocationId").toString()
                        searchText = intent.getStringExtra("assetLocationId").toString()
                        searchTypeFromAPI = SearchType.Location
                        searchTypeLocally = SearchType.Location
                        Log.i("DEBUG", "searchType is location")
                        Log.i("DEBUG22", searchText)
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
                        searchTypeFromAPI = SearchType.NationalCode
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

//            override fun afterTextChanged(p0: Editable?) {
//                expandableListAdapter2.search(searchTypeLocally, p0.toString())
//                tvRecordCount.text =
//                    "تعداد:${englishToPersian(expandableListAdapter2.itemCount.toString())}"
//            }



            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    val fixedQuery = it.toString().replace('ی', 'ي') // تبدیل "ی" فارسی به "ي" عربی

                    expandableListAdapter2.search(searchTypeLocally, fixedQuery) // جستجو با متن اصلاح‌شده

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
        choose_data_pars_mode=findViewById(R.id.selectfilterasset)
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
        acetSearchText.clearFocus()
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
            myit=it
            when (it.status) {
                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel getAssetListResponse success.data=${it.data} "
                    )
                    if (it.data?.assetList != null) {
                        if (responsetype=="setAssetMoreClicked"){
                            if (it.data.assetList.size > 0) {
//                                showCustomDialog(
//                                    this,
//                                    DialogType.SUCCESS,
//                                    " شماره برچسب اموال "+   it.data.assetList[0]!!.assetTag+ "  با نام "+ it.data.assetList[0]!!.productName+" اضافه شد" , object : IClickListener {
//                                        override fun onClick(view: View?, dialog: Dialog) {
//                                            dialog.dismiss()
//                                        }
//                                    })

                                //note+=it.data.assetList[0]!!.assetTag+ " "

                                Log.d("Amir","ok")


                                //اینجا فیلد های جدید ایجاد میشه
                                val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_barcode_with_tag_more, null)
                                val moreAssetsContainer: LinearLayout = dialogView.findViewById(R.id.moreAssetsContainer)
                                 val addedEditTexts = mutableListOf<EditText>()

                                val newEditText = EditText(this).apply {
                                    hint = "شماره برچسب جدید"
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    inputType = InputType.TYPE_CLASS_NUMBER
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    ).apply {
                                        setMargins(16, 8, 16, 8)
                                    }
                                    background = getDrawable(R.drawable.background_gray_light_corners_round_5)
                                    setPadding(16, 16, 16, 16)
                                    textSize = 15f
                                    typeface = resources.getFont(R.font.iransansweb)
                                    gravity = Gravity.CENTER
                                }

                                moreAssetsContainer.addView(newEditText)
                                addedEditTexts.add(newEditText)


                            }else{
                                val message = "اموالی یافت نشد"
                                showCustomDialog(
                                    this,
                                    DialogType.WARNING,
                                    message, object : IClickListener {
                                        override fun onClick(view: View?, dialog: Dialog) {
                                            dialog.dismiss()
                                        }
                                    })

                            }
                        }else{
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
                                it.data.assetList.sortedByDescending { it?.barCode } as List<AssetListItem>
                            )
                            spinnerconfig()
                            tvRecordCount.text =
                                "تعداد : ${englishToPersian(it.data.assetList.size.toString())}"
                                } else {
                            Log.i(
                                "DEBUG",
                                "ActivityShowUserInformation observeViewModel getAssetListResponse success. list is empty. so we return back"
                            )

                            val message = "اموالی یافت نشد"
                            showCustomDialog(
                                this,
                                DialogType.WARNING,
                                message, object : IClickListener {
                                    override fun onClick(view: View?, dialog: Dialog) {
                                        dialog.dismiss()
                                        finish()
                                    }
                                })
                        }
                    } }else {
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
//        showAssetInformationViewModel.getAssetListResponseformoreasset.observe(this) {
//            myit=it
//            when (it.status) {
//
//                Status.SUCCESS -> {
//                    hideLoading()
//                    Log.i(
//                        "DEBUG",
//                        "ActivityShowUserInformation observeViewModel getAssetListResponse success.data=${it.data} "
//                    )
//                    if (it.data?.assetList != null) {
//                        if (it.data.assetList.size > 0) {
//
//                            Log.i("DEBUG","AMVAL YAFT SHOD")
//
//                        } else {
//                            Log.i(
//                                "DEBUG",
//                                "ActivityShowUserInformation observeViewModel getAssetListResponse success. list is empty. so we return back"
//                            )
//
//                            val message = "اموالی یافت نشد"
//                            showCustomDialog(
//                                this,
//                                DialogType.WARNING,
//                                message, object : IClickListener {
//                                    override fun onClick(view: View?, dialog: Dialog) {
//                                        dialog.dismiss()
//                                    }
//                                })
//                        }
//                    } else {
//                        var errorMessage = it.data?.detailError?.get(0)?.errorDesc.toString()
//                        if (errorMessage.isEmpty()) {
//                            errorMessage = it.message.toString()
//                        }
//                        showCustomDialog(
//                            this,
//                            DialogType.ERROR,
//                            errorMessage, object : IClickListener {
//                                override fun onClick(view: View?, dialog: Dialog) {
//                                    dialog.dismiss()
//                                }
//                            })
//                    }
//                }
//
//                Status.LOADING -> {
//                    showLoading()
//                    Log.i(
//                        "DEBUG",
//                        "ActivityShowUserInformation observeViewModel getAssetListResponse loading"
//                    )
//                }
//
//                Status.ERROR -> {
//                    hideLoading()
//                    Log.i(
//                        "DEBUG",
//                        "ActivityShowUserInformation observeViewModel getAssetListResponse error.error=${it.message}"
//                    )
//                    val errorMessage = it.message.toString()
//                    showCustomDialog(this, DialogType.ERROR, errorMessage, object : IClickListener {
//                        override fun onClick(view: View?, dialog: Dialog) {
//                            dialog.dismiss()
//                            finish()
//                        }
//                    })
//                }
//            }
//        }

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


                        modifyAssetViewModel.getModifyAssetHistory(
                            accessToken,
                            actorID,
                            getCurrentDateTime(),
                            null,
                            assetId,
                            assetLocationID.toString().toInt(),
                            assetStatusCode!!,
                            "",
                            subCostCenterID!!
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
                            }
                        })
                }
            }
        }
        showAssetInformationViewModel.setAssetMoreClickedResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    hideLoading()
                    Log.i(
                        "DEBUG",
                        "ActivityShowUserInformation observeViewModel setBarcodeForAssetResponse success.data=${it.data}"
                    )

                    if (it.data?.result == 100) {

                        val message = "برچسب مورد تایید است"
                        //showSnackBarMessage(elvAssetInformation, message)
                        showSnackBarMessage(rvAssetInformation, message)
                        getUser()


                        modifyAssetViewModel.getModifyAssetHistory(
                            accessToken,
                            actorID,
                            getCurrentDateTime(),
                            null,
                            assetId,
                            assetLocationID.toString().toInt(),
                            assetStatusCode!!,
                            note,
                            subCostCenterID!!

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
    public fun initializeExpandableListView(assetList: List<AssetListItem>) {

        val intent = Intent(this, ActivityModifyAsset::class.java)
        expandableListAdapter2 = AssetExpandableListAdapter2(this, object : AssetItemClickListener {
            override fun editItemClicked(assetListItem: AssetListItem) {
                responsetype ="edititem"


                Log.i("DEBUG", "ActivityShowUserInformation assetID=${assetListItem.assetID}")
                Log.i(
                    "DEBUG",
                    "ActivityShowUserInformation assetTypeCode=${assetListItem.assetTypeCode}"
                )
                Log.i("DEBUG", "ActivityShowUserInformation barCode=${assetListItem.barCode}")

                intent.putExtra("type", "edit")

                if (assetListItem.assetID != null) {
                    assetId = assetListItem.assetID.toString().toInt()
                    intent.putExtra("assetId", assetListItem.assetID.toString())
                    Log.d("DEBUG", "assetIdeee: $assetId")

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
                    subCostCenterID=assetListItem.subCostCenterID.toString().toInt()

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
                    assetLocationID=assetListItem.assetLocationID.toString().toInt()

                }

                if (assetListItem.assetLocationName != null) {
                    intent.putExtra(
                        "assetLocationName",
                        assetListItem.assetLocationName.toString()
                    )
                }

                if (assetListItem.actorID != null) {
                    intent.putExtra("actorID", assetListItem.actorID.toString())
                    actorID=assetListItem.actorID.toString().toInt()
                }

                if (assetListItem.actorName != null) {
                    intent.putExtra("actorName", assetListItem.actorName.toString())
                }

                if (assetListItem.assetStatusCode != null) {
                    intent.putExtra("assetStatusCode", assetListItem.assetStatusCode.toString())
                    assetStatusCode=assetListItem.assetStatusCode
                }

                if (assetListItem.assetTypeName != null) {
                    intent.putExtra("assetTypeName", assetListItem.assetTypeName.toString())
                }

                if (assetListItem.barCode == null || assetListItem.barCode == "") {
                    Log.i("DEBUG", "ActivityShowUserInformation ${assetListItem.barCode}")
                    val message = "ابتدا بارکد تخصیص داده شود"
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
                        responsetype="setBarcodeClicked"
                        barcode = (view as EditText).text.toString()

                        if (barcode.length == 11) {
                            Log.i(
                                "DEBUG",
                                "ActivityShowUserInformation showBarcodeDialog barcode=$barcode"
                            )
                            if (applicationContext?.let { isNetworkAvailable(it) } == true) {
                                Log.i(
                                    "DEBUG",
                                    "raw result detected.try to call setBarcodeForAsset"
                                )


                                if (assetListItem.assetID != null) {
                                    assetId = assetListItem.assetID.toString().toInt()
                                    intent.putExtra("assetId", assetListItem.assetID.toString())
                                    Log.d("DEBUG", "assetIdeee: $assetId")

                                }
                                if (assetListItem.subCostCenterID != null) {
                                    intent.putExtra("subCostCenterID", assetListItem.subCostCenterID.toString())
                                    subCostCenterID=assetListItem.subCostCenterID.toString().toInt()

                                }
                                if (assetListItem.assetLocationID != null) {
                                    intent.putExtra("assetLocationID", assetListItem.assetLocationID.toString())
                                    assetLocationID=assetListItem.assetLocationID.toString().toInt()

                                }
                                if (assetListItem.actorID != null) {
                                    intent.putExtra("actorID", assetListItem.actorID.toString())
                                    actorID=assetListItem.actorID.toString().toInt()
                                }

                                if (assetListItem.assetStatusCode != null) {
                                    intent.putExtra("assetStatusCode", assetListItem.assetStatusCode.toString())
                                    assetStatusCode=assetListItem.assetStatusCode
                                }


                                showAssetInformationViewModel.setBarcodeForAsset(
                                    accessToken,
                                    assetListItem.assetID.toString(),
                                    assetListItem.assetTypeCode!!,
                                    barcode
                                )



//                                Toast.makeText(applicationContext, barcode, Toast.LENGTH_SHORT)
//                                    .show()


                            } else {
                                val message = "اتصال شبکه برقرار نیست"
                                showCustomDialog(
                                    this@ActivityShowAssetInformation,
                                    DialogType.ERROR,
                                    message
                                )
                            }
                        } else {
                            val errorMessage = "طول بارکد باید 11  رقم باشد"
                            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
            }



            override fun setAssetMoreClicked(assetListItem: AssetListItem) {
                showAssetmoreDialog(this@ActivityShowAssetInformation, object : IClickListenerWithEditText {
                    override fun onClick(view: View?, editText: EditText?, dialog: Dialog) {
                        responsetype="setAssetMoreClicked"
                        when (view?.id) {
                            R.id.sabtmoreasset -> {
                                Log.d("DEBUG", "دکمه ثبت دارایی بیشتر کلیک شد")
                                val note = editText?.text.toString()
                                if (note.isNotEmpty()) {
                                    Log.i("DEBUGnote", note)

                                    val showAssetInformationViewModel =
                                        ViewModelProvider(this@ActivityShowAssetInformation)
                                            .get(ShowAssetInformationViewModel::class.java)

                                    showAssetInformationViewModel.getAssetList(SearchType.TagText, accessToken, note)

                                } else {
                                    showCustomDialog(
                                        this@ActivityShowAssetInformation,
                                        DialogType.ERROR,
                                        "لطفا مقدار برچسب را وارد کنید."
                                    )

                                    Log.e("DEBUG", "EditText مربوط به دارایی بیشتر خالی است!")
                                }
                            }

                            R.id.acbConfirm -> {
                                val barcodeEditText = dialog.findViewById<EditText>(R.id.etBarcode)
                                 barcode = barcodeEditText?.text.toString()

                                if (barcode.length == 11) {
                                    if (applicationContext?.let { isNetworkAvailable(it) } == true) {
                                        Log.i("DEBUG", "بارکد معتبر است و عملیات آغاز می‌شود.")

                                        val intent = Intent()

                                        assetListItem.assetID?.let {
                                            intent.putExtra("assetId", it.toString())
                                            assetId = it.toInt()
                                            Log.d("DEBUG", "assetId: $assetId")
                                        }
                                        assetListItem.subCostCenterID?.let {
                                            intent.putExtra("subCostCenterID", it.toString())
                                            subCostCenterID = it.toInt()
                                        }
                                        assetListItem.assetLocationID?.let {
                                            intent.putExtra("assetLocationID", it.toString())
                                            assetLocationID = it.toInt()
                                        }
                                        assetListItem.actorID?.let {
                                            intent.putExtra("actorID", it.toString())
                                            actorID = it.toInt()
                                        }
                                        assetListItem.assetStatusCode?.let {
                                            intent.putExtra("assetStatusCode", it.toString())
                                            assetStatusCode = it
                                        }

                                        val showAssetInformationViewModel =
                                            ViewModelProvider(this@ActivityShowAssetInformation)
                                                .get(ShowAssetInformationViewModel::class.java)

                                        showAssetInformationViewModel.setAssetMoreClicked(
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
                                    Toast.makeText(applicationContext, "طول بارکد باید 11 رقم باشد", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                })
            }

        })

        rvAssetInformation.layoutManager = LinearLayoutManager(this)
        rvAssetInformation.itemAnimator = DefaultItemAnimator()

        expandableListAdapter2.addData(assetList)
        expandableListAdapter2
        rvAssetInformation.adapter = expandableListAdapter2
        setupTextChangeListener()
    }



    private fun spinnerconfig() {

        var spineradapter = SpinnerFilterAdapter(this,searchArray)
        spineradapter.spinnerfilteradapter(this,searchArray)
        ivDropDownLocation2=findViewById(R.id.ivDropcodee)
        choose_data_pars_mode.adapter=spineradapter

        choose_data_pars_mode.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_up)
                choose_data_pars_mode.performClick() // برای باز کردن منو
            }
            false // به سایر رویدادها اجازه می‌دهد ادامه یابند
        }


        choose_data_pars_mode.onItemSelectedListener=object :AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)


            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)

                if (p2==0){
                    initializeExpandableListView(
                        myit.data?.assetList?.sortedByDescending { it?.barCode } as List<AssetListItem>)

                    // ارسال داده‌های سورت شده به آداپتر

// اطلاع‌رسانی به آداپتر که داده‌ها تغییر کرده است
                    expandableListAdapter2.notifyDataSetChanged()

// اختصاص آداپتر به RecyclerView
                    rvAssetInformation.adapter = expandableListAdapter2

                }else if (p2==1){
                    Log.i("aa","bb")
                    initializeExpandableListView(
                        myit.data?.assetList?.sortedByDescending { it?.assetHistoryDate } as List<AssetListItem>)

                    // ارسال داده‌های سورت شده به آداپتر

// اطلاع‌رسانی به آداپتر که داده‌ها تغییر کرده است
                    expandableListAdapter2.notifyDataSetChanged()

// اختصاص آداپتر به RecyclerView
                    rvAssetInformation.adapter = expandableListAdapter2


                }else if (p2==2){
                    initializeExpandableListView(
                        myit.data?.assetList?.sortedBy { it?.productName } as List<AssetListItem>)

                    // ارسال داده‌های سورت شده به آداپتر

// اطلاع‌رسانی به آداپتر که داده‌ها تغییر کرده است
                    expandableListAdapter2.notifyDataSetChanged()

// اختصاص آداپتر به RecyclerView
                    rvAssetInformation.adapter = expandableListAdapter2

                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)

            }

        }


    }



}