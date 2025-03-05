package com.tehranmunicipality.assetmanagement.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.airbnb.lottie.LottieAnimationView
import com.andreseko.SweetAlert.SweetAlertDialog
import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.google.android.material.snackbar.Snackbar
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import com.tehranmunicipality.assetmanagement.ui.main.ActivityMain
import com.tehranmunicipality.assetmanagement.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityLogin : AppCompatActivity(), View.OnClickListener {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var acetUsernameForLogin: AppCompatEditText
    private lateinit var acetPasswordForLogin: AppCompatEditText
    private lateinit var ivEye: ImageView
    private lateinit var sweetAlertDialog: SweetAlertDialog
    private lateinit var rlSectionLogin: RelativeLayout
    private lateinit var acbtnLogin: AppCompatButton
    private lateinit var vwLoading: View
    private lateinit var token: String

    private var appUser: AppUser? = null
    private lateinit var activityIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindView()
        setupClicks()
        setupPasswordChangeListener()
        observeViewModel()
        checkLoggedInUser()
        val animationView = findViewById<LottieAnimationView>(R.id.givLoading)

// Start playing the animation
        animationView.playAnimation()

       showLoading()
        activityIntent = Intent(this, ActivityMain::class.java)
    }

    private fun setupPasswordChangeListener() {
        acetPasswordForLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun checkLoggedInUser() {
        loginViewModel.getUserNormal()
    }

    private fun bindView() {
        vwLoading = findViewById(R.id.vwLoading)
        rlSectionLogin = findViewById(R.id.rlSectionLogin)
        acetUsernameForLogin = findViewById(R.id.acetUsernameForLogin)
        acetPasswordForLogin = findViewById(R.id.acetPasswordForLogin)
        ivEye = findViewById(R.id.ivEye)
        sweetAlertDialog = SweetAlertDialog(applicationContext)
        acbtnLogin = findViewById(R.id.acbtnLogin)
    }

    private fun showLoading() {
        vwLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        vwLoading.visibility = View.GONE
    }

    private fun setupClicks() {
        acbtnLogin.setOnClickListener(this)
        ivEye.setOnClickListener(this)
    }

    private fun observeViewModel() {

        loginViewModel.esbTokenResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    loading(false)
                    Log.i(
                        "DEBUG",
                        "ActivityLogin observeViewModel esbTokenResponse success.data=${it.data}"
                    )
                    Log.i("DEBUG", "ActivityLogin token=${it.data?.accessToken.toString()}")
                    if (!it.data?.accessToken.isNullOrEmpty()) {
                        token = it.data?.accessToken.toString()

                        Log.i("DEBUG", "amirtoken=$token")
                        val jwt = JWT(token)
                        val jwtClaim: Claim = jwt.getClaim("DisplayName")
                        val displayName = jwtClaim.asString()
                        Log.i("DEBUG", "displayName=$displayName")
                        val username = acetUsernameForLogin.text.toString().plus("@tehran.ir").lowercase()
                        Log.i("amir",username)
                        val password = acetPasswordForLogin.text.toString()

                        loginViewModel.insertUser(username, password, displayName.toString(), token)
                        appUser = AppUser(username, password, displayName.toString(), token)
                    } else {
                        if (!it.data?.error?.isEmpty()!!) {
                            val errorMessage = it.data.error_description.toString()
                            showCustomDialog(this, DialogType.ERROR, errorMessage)
                        }
                    }
                }

                Status.LOADING -> {
                    loading(true)
                }

                Status.ERROR -> {
                    loading(false)
                    Snackbar.make(acbtnLogin, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        loginViewModel.insertUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivityLogin observeViewModel insertUserResponse success.data=${it.data}"
                    )
                    hideLoading()
                    if (it.data!! > 0) {
                        Log.i("DEBUG", "ActivityLogin observeViewModel insertUser success")
                        Log.i(
                            "DEBUG",
                            "ActivityLogin observeViewModel insertUserResponse before checkLoggedInUser"
                        )
                        checkLoggedInUser()
                    }
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.ERROR -> {
                    hideLoading()
                    val errorMessage = it.message.toString()
                    showSnackBarMessage(acbtnLogin, errorMessage)
                }
            }
        }

        loginViewModel.appUserResponse.observe(this) {

            when (it.status) {

                Status.SUCCESS -> {
                    Log.i(
                        "DEBUG",
                        "ActivityLogin observeViewModel appUserResponse success.->${it.data}"
                    )
                    hideLoading()
                    if (it.data != null) {
                        activityIntent.putExtra("displayName", it.data.displayName)
                        activityIntent.putExtra("username", it.data.username)
                        activityIntent.putExtra("password", it.data.password)
                        activityIntent.putExtra("token", it.data.token)
                        startActivity(activityIntent)
                    } else {
                        val errorMessage =
                            "کاربری در دیتابیس محلی وجود ندارد."
                        showSnackBarMessage(acbtnLogin, errorMessage)
                    }
                }

                Status.LOADING -> {
                    showLoading()
                }

                Status.ERROR -> {
                    hideLoading()
                    val errorMessage = it.message.toString()
                    showSnackBarMessage(acbtnLogin, errorMessage)
                }
            }
        }
    }

    private fun getUser() {
        loginViewModel.getUser()
    }

    override fun onClick(p0: View?) {

        when (p0) {

            ivEye -> {

                if (acetPasswordForLogin.transformationMethod != null) {
                    if (acetPasswordForLogin.transformationMethod.equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        ivEye.setImageResource(R.drawable.ic_eye_close)
                        acetPasswordForLogin.transformationMethod = null
                    }
                } else {
                    ivEye.setImageResource(R.drawable.ic_eye_open)
                    acetPasswordForLogin.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
            }

            acbtnLogin -> {
                if (isLoginInputValid()) {
                    val username = acetUsernameForLogin.text.toString().plus("@tehran.ir")
                    val password = acetPasswordForLogin.text.toString()
                    Log.i("DEBUG", "ActivityLogin acbLogin clicked getESBToken")
                    loginViewModel.getESBToken(username, password)
                }
            }
        }
    }

    private fun isLoginInputValid(): Boolean {
        var isValid = true
        var errorMessage = ""

        if (!applicationContext.let { isNetworkAvailable(it) }) {
            isValid = false
            errorMessage = "اتصال اینترنت برقرار نیست"
        }

        if (acetPasswordForLogin.text!!.isEmpty()) {
            isValid = false
            errorMessage = "رمز عبور را وارد نمایید"
        }

        if (acetUsernameForLogin.text!!.isEmpty()) {
            isValid = false
            errorMessage = "نام کاربری را وارد نمایید"
        }

        if (!isValid) {
            showSnackBarMessage(acbtnLogin, errorMessage)
        }

        return isValid
    }

    private fun loading(show: Boolean) {
        if (show) {
            vwLoading.visibility = View.VISIBLE
        } else {
            vwLoading.visibility = View.GONE
        }
    }
}