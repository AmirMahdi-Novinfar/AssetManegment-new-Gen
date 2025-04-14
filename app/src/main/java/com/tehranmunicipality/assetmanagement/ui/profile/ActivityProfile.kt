package com.tehranmunicipality.assetmanagement.ui.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity
import com.tehranmunicipality.assetmanagement.ui.login.ActivityLogin
import com.tehranmunicipality.assetmanagement.ui.main.MainViewModel
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AmirMahdi Novinfar - amirnovin80@gmail.com on 29,October,2023
 */

@AndroidEntryPoint
class ActivityProfile : BaseActivity(), View.OnClickListener {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var ivBack: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvPersonnelNo: TextView
    private lateinit var tvNationalCode: TextView
    private lateinit var tvRegionCode: TextView
    private var appUser: AppUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        bindView()
        setupClicks()
        observeViewModel()
        getUser()
    }

    private fun getUser() {
        profileViewModel.getUser()
    }

    private fun observeViewModel() {

        profileViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i("DEBUG", "ActivityProfile appUserResponse success.data=${it.data}")
                    if (it.data != null) {
                        appUser = it.data
                        setFormattedText(
                            tvUsername,
                            getColor(R.color.white),
                            "نام و نام خانوادگی : ",
                            appUser!!.displayName.toString()
                        )
                        setFormattedText(
                            tvPersonnelNo, getColor(R.color.white),
                            "شماره پرسنلی : ",
                            englishToPersian("ثابته")

                        )
                        setFormattedText(
                            tvNationalCode, getColor(R.color.white),
                            "شماره ملی : ",
                            englishToPersian("ثابته")
                        )
                        setFormattedText(
                            tvRegionCode, getColor(R.color.white),
                            "واحد اجرایی : ",
                            "سازمان فناوری اطلاعات و ارتباطات شهرداری تهران"
                        )
                    }
                }

                Status.LOADING -> {
                    Log.i("DEBUG", "ActivityProfile appUserResponse loading")
                }

                Status.ERROR -> {
                    Log.i("DEBUG", "ActivityProfile appUserResponse error.error=${it.message}")
                }
            }
        }
    }

    private fun bindView() {

        ivBack = findViewById(R.id.ivBack)
        tvUsername = findViewById(R.id.tvUsername)
        tvPersonnelNo = findViewById(R.id.tvPersonnelNo)
        tvNationalCode = findViewById(R.id.tvNationalCode)
        tvRegionCode = findViewById(R.id.tvRegionCode)
    }

    private fun setupClicks() {

        ivBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {

            ivBack -> {
                finish()
            }
        }
    }
}