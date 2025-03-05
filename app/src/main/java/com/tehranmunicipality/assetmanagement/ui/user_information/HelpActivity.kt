package com.tehranmunicipality.assetmanagement.ui.user_information

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.ui.base.BaseActivity

class HelpActivity : BaseActivity(), View.OnClickListener {

    private lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        ivBack = findViewById(R.id.ivBack)

        ivBack.setOnClickListener(this)




    }

    override fun onClick(p0: View?) {
        when (p0) {

            ivBack -> {
                finish()
            }
    }}


    }
