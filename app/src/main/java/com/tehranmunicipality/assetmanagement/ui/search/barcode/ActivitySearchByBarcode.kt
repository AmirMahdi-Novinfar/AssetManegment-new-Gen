package com.tehranmunicipality.assetmanagement.ui.search.barcode

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.BARCODE_LENGTH
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityCameraBarcodeScanner
import com.tehranmunicipality.assetmanagement.ui.asset_information.ActivityShowAssetInformation
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogDismissListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogListClickListener
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.base.SpinnerSearchTypeAdapter
import com.tehranmunicipality.assetmanagement.ui.search.SearchViewModel
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySearchByBarcode : BaseActivity(), View.OnClickListener {

    private var TAG = "DEBUG"

    private val searchByBarcodeViewModel: SearchViewModel by viewModels()

    private lateinit var ivBack: ImageView
    private lateinit var etBarcode: EditText
    private lateinit var btnInquiry: Button
    private lateinit var btnScanBarcode: Button

    private var isSearchByBarcodeId = true

    private lateinit var ivDropDownLocation2: ImageView
    private lateinit var choose_data_pars_mode: Spinner
    private lateinit var layout_codemeli_main: RelativeLayout

    private val searchModes = arrayOf("برچسب", "بارکد")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_barcode)

        bindView()
        setupClicks()
        spinnerconfig()
    }

    private fun bindView() {
        ivBack = findViewById(R.id.ivBack)
        choose_data_pars_mode = findViewById(R.id.select_queri_db_mode)
        etBarcode = findViewById(R.id.etBarcode)
        layout_codemeli_main = findViewById(R.id.barcode_main_layout)
        btnInquiry = findViewById(R.id.btnInquiry)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)

        layout_codemeli_main.setOnClickListener(this)
    }

    private fun setupClicks() {
        ivBack.setOnClickListener(this)
        btnInquiry.setOnClickListener(this)
        btnScanBarcode.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            ivBack -> {
                finish()
            }

            btnInquiry -> {
                inquiry()
            }

            btnScanBarcode -> {
                openBarcodeScanner()
            }
        }
    }

    private fun inquiry() {
        if (isInputValid()) {
            val intent = Intent(
                this,
                ActivityShowAssetInformation::class.java
            )

            intent.putExtra("fromActivity", javaClass.simpleName)

            if (isSearchByBarcodeId) {
                intent.putExtra("barcodeId", etBarcode.text.toString())
            } else {
                intent.putExtra("tagText", etBarcode.text.toString())
            }

            startActivity(intent)
        }
    }

    private fun spinnerconfig() {

        val spinnerAdapter = SpinnerSearchTypeAdapter(this, searchModes)
        spinnerAdapter.spinnerSearchTypeAdapter(this, searchModes)

        ivDropDownLocation2 = findViewById(R.id.ivDropcodee)

        choose_data_pars_mode.adapter = spinnerAdapter

        choose_data_pars_mode.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                ivDropDownLocation2.setImageResource(R.drawable.ic_up)
                choose_data_pars_mode.performClick()
            }
            false
        }

        choose_data_pars_mode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)

                    when (position) {

                        // حالت بارکد
                        1 -> {
                            isSearchByBarcodeId = true

                            etBarcode.hint = "بارکد را اسکن یا وارد نمایید"
                            etBarcode.filters = arrayOf(
                                InputFilter.LengthFilter(BARCODE_LENGTH)
                            )
                            etBarcode.keyListener =
                                DigitsKeyListener.getInstance("0123456789")

                            etBarcode.setText("")

                            btnInquiry.visibility = View.VISIBLE
                            btnScanBarcode.visibility = View.VISIBLE
                        }

                        // حالت برچسب
                        0 -> {
                            isSearchByBarcodeId = false

                            etBarcode.hint = "برچسب را وارد نمایید"
                            etBarcode.filters = arrayOf(
                                InputFilter.LengthFilter(BARCODE_LENGTH)
                            )
                            etBarcode.keyListener =
                                DigitsKeyListener.getInstance("0123456789")

                            etBarcode.setText("")

                            btnInquiry.visibility = View.VISIBLE
                            btnScanBarcode.visibility = View.GONE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    ivDropDownLocation2.setImageResource(R.drawable.ic_down)
                    Log.i("amir", "amir")
                }
            }

        // انتخاب پیش‌فرض: برچسب
        choose_data_pars_mode.setSelection(0)
    }

    private fun isInputValid(): Boolean {

        var isValid = true
        var errorMessage = ""

        if (!applicationContext.let { isNetworkAvailable(it) }) {
            isValid = false
            errorMessage = "اتصال اینترنت برقرار نیست"
        }

        if (isSearchByBarcodeId) {

            if (etBarcode.text.isEmpty()) {
                isValid = false
                errorMessage = "بارکد کالا را وارد نمایید"

            } else if (etBarcode.length() != 11) {
                isValid = false
                errorMessage = "بارکد باید 11 رقم باشد"
            }

        } else if (etBarcode.text.isEmpty()) {

            isValid = false
            errorMessage = "برچسب را وارد نمایید"
        }

        if (!isValid) {
            showCustomDialog(
                this,
                DialogType.ERROR,
                errorMessage,
                object : IClickListener {
                    override fun onClick(view: View?, dialog: Dialog) {
                        dialog.dismiss()
                    }
                }
            )
        }

        return isValid
    }

    // دریافت بارکد از صفحه دوربین
    private val barcodeScannerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val barcode = result.data?.getStringExtra("barcodeResult")

                if (!barcode.isNullOrBlank()) {
                    etBarcode.setText(barcode)
                    etBarcode.setSelection(etBarcode.text.length)
                }
            }
        }

    // باز کردن صفحه اسکن دوربین
    private fun openBarcodeScanner() {

        val intent = Intent(
            this,
            ActivityCameraBarcodeScanner::class.java
        )

        barcodeScannerLauncher.launch(intent)
    }
}