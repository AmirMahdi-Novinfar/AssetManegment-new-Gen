package com.tehranmunicipality.assetmanagement.ui.search.username_or_national_code

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.SpinnerSearchTypeAdapter
import com.tehranmunicipality.assetmanagement.ui.person_list.ActivityPersonList
import com.tehranmunicipality.assetmanagement.ui.search.SearchViewModel
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySearchByUsernameOrNationalCode : BaseActivity(), View.OnClickListener {

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var choose_data_pars_mode:Spinner
    private lateinit var etUsernameOrNationalCode: EditText
    private lateinit var btnInquiry: Button
    private lateinit var layout_codemeli_main: RelativeLayout
    private var isSearchByUsername: Boolean = false
    private lateinit var ivDropDownLocation2:ImageView
    val searchArray = arrayOf("کدملی", "نام تحویل گیرنده")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_username_or_national_code)

        bindView()
        setupClicks()
        observeViewModel()
        spinnerconfig()

    }


    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        choose_data_pars_mode=findViewById(R.id.select_queri_db_mode)
        etUsernameOrNationalCode = findViewById(R.id.etUsernameOrNationalCode)
        layout_codemeli_main = findViewById(R.id.layout_codemeli_main)
        btnInquiry = findViewById(R.id.btnInquiry)
        layout_codemeli_main.setOnClickListener(this)
    }

    private fun setupClicks() {

        ivBack.setOnClickListener(this)
        btnInquiry.setOnClickListener(this)

    }

    private fun spinnerconfig() {

        var spineradapter = SpinnerSearchTypeAdapter(this,searchArray)
        spineradapter.spinnerSearchTypeAdapter(this,searchArray)
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

                if (p2==1){
                    etUsernameOrNationalCode.visibility=View.VISIBLE
                    etUsernameOrNationalCode.hint = "${searchArray[1]} را وارد نمایید"
                    etUsernameOrNationalCode.filters = arrayOf(InputFilter.LengthFilter(20))
                    etUsernameOrNationalCode.inputType = InputType.TYPE_CLASS_TEXT
                    isSearchByUsername = true
                    etUsernameOrNationalCode.setText("")
                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)


                }else if (p2==0){
                    etUsernameOrNationalCode.visibility=View.VISIBLE
                    etUsernameOrNationalCode.hint = "${searchArray[0]} را وارد نمایید"
                    etUsernameOrNationalCode.filters = arrayOf(InputFilter.LengthFilter(10))
                    etUsernameOrNationalCode.keyListener =
                        DigitsKeyListener.getInstance("0123456789")
                    isSearchByUsername = false
                    etUsernameOrNationalCode.setText("")

                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)




                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)
                Log.i("amir","amir")

            }

        }


    }

    private fun observeViewModel() {

        searchViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel appUserResponse success.data=${it.data}"
                    )
                    if (it.data?.token?.isEmpty() == false) {
                        if (!applicationContext.let { isNetworkAvailable(it) }) {
                            val errorMessage = "اتصال اینترنت برقرار نیست"
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        } else {
                            searchViewModel.getESBToken(it.data.username, it.data.password)
                        }
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivitySearchByUsernameOrNationalCode observeViewModel appUserResponse token is empty!!!"
                        )
                    }
                }

                Status.LOADING -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel appUserResponse loading"
                    )
                }
                Status.ERROR -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel appUserResponse error.${it.message}"
                    )
                }
            }
        }

        searchViewModel.esbTokenResponse.observe(this) {
            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    if (!it.data?.accessToken.isNullOrEmpty()) {

                        val searchText = etUsernameOrNationalCode.text.toString()
                        val searchType: SearchType
                        when (isSearchByUsername) {

                            true -> {
                                searchType = SearchType.Username
                            }

                            false -> {
                                searchType = SearchType.NationalCode
                            }

                        }
                        searchViewModel.getAssetList(
                            searchType,
                            it.data?.accessToken.toString(),
                            searchText
                        )
                    } else {
                        Log.i(
                            "DEBUG",
                            "ActivitySearchByUsernameOrNationalCode observeViewModel esbTokenResponse token is null"
                        )
                    }
                }

                Status.LOADING -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel esbTokenResponse loading"
                    )
                }

                Status.ERROR -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel esbTokenResponse error.${it.message}"
                    )
                    val errorMessage = it.message.toString()
                    showCustomDialog(this, DialogType.ERROR, errorMessage)
                }
            }
        }

        searchViewModel.getAssetListResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel getAssetListResponse success.data=${it.data}"
                    )
                }

                Status.LOADING -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel getAssetListResponse loading"
                    )
                }

                Status.ERROR -> {
                    Log.i(
                        "DEBUG",
                        "ActivitySearchByUsernameOrNationalCode observeViewModel getAssetListResponse error.${it.message}"
                    )
                }
            }
        }
    }

    override fun onClick(p0: View?) {

        when (p0) {

            ivBack -> {
                finish()
            }

//            rlChooseSearchMode -> {
//                Log.i("DEBUG", "dropdown clicked")
//                showPopup()
//            }


            layout_codemeli_main->{
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)
            }

            btnInquiry -> {

                if (isInputValid()) {
                    val intent = Intent(this, ActivityShowAssetInformation::class.java)
                    intent.putExtra("fromActivity", javaClass.simpleName)
                    Log.i("DEBUG", "activityName=${javaClass.simpleName}")

                    if (isSearchByUsername) {

                        val intent1 = Intent(this, ActivityPersonList::class.java)
                        intent1.putExtra(
                            "username",
                            etUsernameOrNationalCode.text.toString()
                        )
                        startActivity(intent1)
                    } else {
                        intent.putExtra(
                            "nationalCode",
                            etUsernameOrNationalCode.text.toString()
                        )
                        startActivity(intent)
                    }
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

        if (isSearchByUsername) {

            if (etUsernameOrNationalCode.text.isEmpty()) {
                isValid = false
                errorMessage = "${searchArray[1]} را وارد نمایید"
            }
        } else {
            if (etUsernameOrNationalCode.text.isEmpty()) {
                isValid = false
                errorMessage = "${searchArray[0]} را وارد نمایید"
            } else {
                if (etUsernameOrNationalCode.text.length != 10) {
                    isValid = false
                    errorMessage = "${searchArray[0]} باید ۱۰ رقم باشد"

                }
            }
        }

        if (!isValid) {
            showSnackBarMessage(btnInquiry, errorMessage)
        }
        return isValid
    }
}

