package com.tehranmunicipality.assetmanagement.ui.search.barcode

import android.app.Dialog
import android.content.DialogInterface
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
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.search.SearchViewModel
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogDismissListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogListClickListener
import com.tehranmunicipality.assetmanagement.ui.base.SpinnerSearchTypeAdapter
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySearchByBarcode : BaseActivity(), View.OnClickListener {

    private var TAG = "DEBUG"
    private val searchByBarcodeViewModel: SearchViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var etBarcode: EditText
    private lateinit var btnInquiry: Button
    private var isSearchByBarcodeId = true
    private lateinit var popupMenu: PopupMenu
    private lateinit var ivDropDownLocation2:ImageView
    private lateinit var choose_data_pars_mode:Spinner
    private lateinit var layout_codemeli_main: RelativeLayout



    val searchModes = arrayOf("برچسب", "بارکد")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_barcode)

        bindView()
        setupClicks()
        spinnerconfig()
    }


    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        choose_data_pars_mode=findViewById(R.id.select_queri_db_mode)
        etBarcode = findViewById(R.id.etBarcode)
        layout_codemeli_main = findViewById(R.id.barcode_main_layout)
        btnInquiry = findViewById(R.id.btnInquiry)
        layout_codemeli_main.setOnClickListener(this)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        btnInquiry.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0) {

            ivBack -> {
                finish()
            }
            btnInquiry -> {
                inquiry()
            }
        }
    }

    private fun inquiry() {
        if (isInputValid()) {
            val intent = Intent(this, ActivityShowAssetInformation::class.java)
            intent.putExtra("fromActivity", javaClass.simpleName)
            when (isSearchByBarcodeId) {

                true -> {
                    intent.putExtra("barcodeId", etBarcode.text.toString())
                }

                false -> {
                    intent.putExtra("tagText", etBarcode.text.toString())
                }
            }

            startActivity(intent)
        }
    }


    private fun spinnerconfig() {

        var spineradapter = SpinnerSearchTypeAdapter(this,searchModes)
        spineradapter.spinnerSearchTypeAdapter(this,searchModes)
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
                    etBarcode.hint = "بارکد را اسکن یا وارد نمایید"
                    etBarcode.filters = arrayOf(InputFilter.LengthFilter(BARCODE_LENGTH))
                    etBarcode.keyListener = DigitsKeyListener.getInstance("0123456789")
                    isSearchByBarcodeId = true
                    etBarcode.setText("")
                    btnInquiry.visibility = View.VISIBLE

                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)



                }else if (p2==0){
                    etBarcode.hint = "برچسب را وارد نمایید"
                    etBarcode.filters = arrayOf(InputFilter.LengthFilter(BARCODE_LENGTH))
                    etBarcode.keyListener = DigitsKeyListener.getInstance("0123456789")
                    isSearchByBarcodeId = false
                    etBarcode.setText("")
                    btnInquiry.visibility = View.VISIBLE
                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)




                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_down)
                Log.i("amir","amir")

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

        if (isSearchByBarcodeId) {
            if (etBarcode.text.isEmpty() ) {
                isValid = false
                errorMessage = "بارکد کالا را وارد نمایید"
            } else if (etBarcode.length()!=11) {
                    isValid = false
                    errorMessage = "بارکد باید 11 رقم باشد "
                }
        }else if (etBarcode.text.isEmpty()) {
                isValid = false
                errorMessage = "برچسب را وارد نمایید"
            }

        if (!isValid) {

            showCustomDialog(
                this,
                DialogType.ERROR,
                errorMessage, object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        dialog.dismiss()
                    }
                })
        }
        return isValid
    }
}