package com.tehranmunicipality.assetmanagement.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.andreseko.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.tehranmunicipality.assetmanagement.R
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogDismissListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.IDialogListClickListener
import com.tehranmunicipality.assetmanagement.ui.asset_information.ShowAssetInformationViewModel
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*

interface IClickListener {
    fun onClick(view: View?,  dialog: Dialog)
}

interface IClickListenerWithEditText {
    fun onClick(view: View?, editText: EditText?, dialog: Dialog)
}


fun getCurrentDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    Log.i("DEBUG", "currentDateTime=${sdf.format(Date())}")
    return persianToEnglish(sdf.format(Date()))
}

fun showSnackBarMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    //Internet connectivity check in Android Q
    val networks = connectivityManager.allNetworks
    var hasInternet = false
    if (networks.isNotEmpty()) {
        for (network in networks) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) hasInternet =
                true
        }
    }
    return hasInternet
}

fun getPersianDate(): String {

    val pdate = PersianDate()
    val pdformater1 = PersianDateFormat("Y/m/d")
    return pdformater1.format(pdate)

}

fun setFormattedText(textView: TextView, firstSection: String, secondSection: String) {
    val formattedText = SpannableStringBuilder("$firstSection $secondSection")

    // Format the first section in bold and black color
    formattedText.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        firstSection.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    formattedText.setSpan(
        ForegroundColorSpan(Color.BLACK),
        0,
        firstSection.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Format the second section in normal text and gray color
    formattedText.setSpan(
        ForegroundColorSpan(Color.BLACK),
        firstSection.length + 1,
        formattedText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView.text = formattedText
}

fun setFormattedText(textView: TextView, color: Int, firstSection: String, secondSection: String) {
    val formattedText = SpannableStringBuilder("$firstSection $secondSection")

    // Format the first section in bold and black color
    formattedText.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        firstSection.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    formattedText.setSpan(
        ForegroundColorSpan(color),
        0,
        firstSection.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    // Format the second section in normal text and gray color
    formattedText.setSpan(
        ForegroundColorSpan(color),
        firstSection.length + 1,
        formattedText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView.text = formattedText
}

fun openConfirmVisionDialog(context: Context, listener: IClickListener) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_confirm_vision)
    dialog.setCancelable(false)
    val acbNo = dialog.findViewById<AppCompatButton>(R.id.btnNo)
    val acbYes = dialog.findViewById<AppCompatButton>(R.id.btnYes)
    acbNo.setOnClickListener {
        dialog.dismiss()
    }
    acbYes.setOnClickListener {
        listener.onClick(acbYes, dialog)
        dialog.dismiss()
    }
    dialog.show()
}

fun openConfirmExitDialog(context: Context, listener: IClickListener) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_confirm_exit)
    dialog.setCancelable(false)
    val acbNo = dialog.findViewById<AppCompatButton>(R.id.btnNo)
    val acbYes = dialog.findViewById<AppCompatButton>(R.id.btnYes)
    val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
    acbNo.setOnClickListener {
        dialog.dismiss()
    }
    acbYes.setOnClickListener {
        listener.onClick(acbYes, dialog)
        dialog.dismiss()
    }

    ivClose.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}

fun openHelpDialog(context: Context, message: String) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_help)
    dialog.setCancelable(false)
    val tvHelp = dialog.findViewById<TextView>(R.id.tvHelp)
    val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)

    tvHelp.text = message

    ivClose.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun String.normalizeText(): String {
    // Remove diacritics and normalize the text
    val normalizedText = Normalizer.normalize(this, Normalizer.Form.NFD)

    // Remove non-spacing marks
    return normalizedText.replace(Regex("\\p{Mn}"), "")
}

fun String.replaceLast(toReplace: CharArray, newChar: Char): String {
    if (last() in toReplace) {
        return dropLast(1) + 'A'
    }
    return this
}

fun englishToPersian(englishStr: String): String {
    var result = ""
    var fa = '0'
    for (ch in englishStr) {
        fa = ch
        when (ch) {
            '0' -> fa = '۰'
            '1' -> fa = '۱'
            '2' -> fa = '۲'
            '3' -> fa = '۳'
            '4' -> fa = '۴'
            '5' -> fa = '۵'
            '6' -> fa = '۶'
            '7' -> fa = '۷'
            '8' -> fa = '۸'
            '9' -> fa = '۹'
        }
        result = "${result}$fa"
    }
    return result
}

fun persianToEnglish(persianStr: String): String {
    var result = ""
    var en = '0'
    for (ch in persianStr) {
        en = ch
        when (ch) {

            '۰' -> en = '0'
            '۱' -> en = '1'
            '۲' -> en = '2'
            '۳' -> en = '3'
            '۴' -> en = '4'
            '۵' -> en = '5'
            '۶' -> en = '6'
            '۷' -> en = '7'
            '۸' -> en = '8'
            '۹' -> en = '9'

        }
        result = "${result}$en"
    }
    return result
}

fun showListSearchTypesDialog(
    activity: Activity,
    view: View,
    array: Array<String>,
    dialogListItemClickListener: IDialogListClickListener,
    dialogDismissListener: IDialogDismissListener
) {

    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.dialog_list_search_types)
    val lvSearchTypes = dialog.findViewById<ListView>(R.id.lvSearchTypes)
    //dialog.setCancelable(false)

    val layoutParams = dialog.window?.attributes
    layoutParams?.width = view.width
    layoutParams?.height = RelativeLayout.LayoutParams.WRAP_CONTENT

    layoutParams?.gravity = Gravity.TOP
    layoutParams?.y = (view.y).toInt() + view.height + 10

    dialog.window?.attributes = layoutParams


    val spinnerAdapter = com.tehranmunicipality.assetmanagement.ui.base.SpinnerAdapter(activity,
        array, object : IDialogListClickListener {
            override fun onClick(view: View?, text: String) {
                dialogListItemClickListener.onClick(view, text)
                dialog.dismiss()
            }
        })

    lvSearchTypes.adapter = spinnerAdapter

    dialog.setOnDismissListener(object : OnDismissListener {
        override fun onDismiss(p0: DialogInterface?) {
            dialogDismissListener.onDismiss(p0)
        }
    })

    dialog.show()

}

fun showCustomDialog(
    activity: Activity,
    dialogType: DialogType,
    message: String,
    clickListener: IClickListener
) {

    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_info)
    dialog.setCancelable(false)
    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnClose = dialog.findViewById<Button>(R.id.btnClose)

    val animFadeIn: Animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)

    ivIcon.animation = animFadeIn

    ivIcon.animate()
        .scaleY(1.0F)
        .setDuration(600)
        .setInterpolator(DecelerateInterpolator())
        .start()

    when (dialogType) {

        DialogType.NORMAL -> {

        }

        DialogType.SUCCESS -> {
            tvTitle.text = "تایید"
            ivIcon.setImageResource(R.drawable.ic_success)
            btnClose.setBackgroundResource(R.drawable.background_success)
        }

        DialogType.WARNING -> {
            tvTitle.text = "هشدار"
            ivIcon.setImageResource(R.drawable.ic_warning)
            btnClose.setBackgroundResource(R.drawable.background_warning)
        }

        DialogType.ERROR -> {
            tvTitle.text = "خطا"
            ivIcon.setImageResource(R.drawable.ic_error)
            btnClose.setBackgroundResource(R.drawable.background_error)
        }
    }
    tvMessage.text = message

    btnClose.setOnClickListener(object : View.OnClickListener {
        override fun onClick(p0: View?) {
            clickListener.onClick(p0, dialog)
        }
    })

    dialog.show()

}

fun showCustomDialogwithtwobutton(
    activity: Activity,
    dialogType: DialogType,
    message: String,
    clickListener: IClickListener,
    clickListenersubmit: IClickListener
) {

    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_info_with_two_buttons)
    dialog.setCancelable(false)
    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnClose = dialog.findViewById<Button>(R.id.btnClose)
    val btnsubmit = dialog.findViewById<Button>(R.id.btnsubmit)

    val animFadeIn: Animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)

    ivIcon.animation = animFadeIn

    ivIcon.animate()
        .scaleY(1.0F)
        .setDuration(600)
        .setInterpolator(DecelerateInterpolator())
        .start()

    when (dialogType) {

        DialogType.NORMAL -> {

        }

        DialogType.SUCCESS -> {
            tvTitle.text = "تایید"
            ivIcon.setImageResource(R.drawable.ic_success)
            btnClose.setBackgroundResource(R.drawable.background_success)
        }

        DialogType.WARNING -> {
            tvTitle.text = "هشدار"
            ivIcon.setImageResource(R.drawable.ic_warning)
            btnClose.setBackgroundResource(R.drawable.backscanbarcodetoosi)
        }

        DialogType.ERROR -> {
            tvTitle.text = "خطا"
            ivIcon.setImageResource(R.drawable.ic_error)
            btnClose.setBackgroundResource(R.drawable.background_error)
        }
    }
    tvMessage.text = message

    btnClose.setOnClickListener(object : View.OnClickListener {
        override fun onClick(p0: View?) {
            clickListener.onClick(p0, dialog)
        }
    })
    btnsubmit.setOnClickListener(object : View.OnClickListener {
        override fun onClick(p0: View?) {
            clickListenersubmit.onClick(p0, dialog)
        }
    })

    dialog.show()

}

fun showCustomDialogamir(
    context: Context,
    dialogType: DialogType,
    message: String,
    clickListener: IClickListener
) {

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_info)
    dialog.setCancelable(false)
    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnClose = dialog.findViewById<Button>(R.id.btnClose)

    val animFadeIn: Animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)

    ivIcon.animation = animFadeIn

    ivIcon.animate()
        .scaleY(1.0F)
        .setDuration(600)
        .setInterpolator(DecelerateInterpolator())
        .start()

    when (dialogType) {

        DialogType.NORMAL -> {

        }

        DialogType.SUCCESS -> {
            tvTitle.text = "تایید"
            ivIcon.setImageResource(R.drawable.ic_success)
            btnClose.setBackgroundResource(R.drawable.background_success)
        }

        DialogType.WARNING -> {
            tvTitle.text = "هشدار"
            ivIcon.setImageResource(R.drawable.ic_warning)
            btnClose.setBackgroundResource(R.drawable.background_warning)
        }

        DialogType.ERROR -> {
            tvTitle.text = "خطا"
            ivIcon.setImageResource(R.drawable.ic_error)
            btnClose.setBackgroundResource(R.drawable.background_error)
        }
    }
    tvMessage.text = message

    btnClose.setOnClickListener(object : View.OnClickListener {
        override fun onClick(p0: View?) {
            clickListener.onClick(p0, dialog)
        }
    })

    dialog.show()

}

fun showCustomDialog(
    activity: Activity,
    dialogType: DialogType,
    message: String
) {

    val dialog = Dialog(activity)
    dialog.setCancelable(false)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_info)
    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnClose = dialog.findViewById<Button>(R.id.btnClose)

    val animFadeIn: Animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)

    ivIcon.animation = animFadeIn

    ivIcon.animate()
        .scaleY(1.0F)
        .setDuration(600)
        .setInterpolator(DecelerateInterpolator())
        .start()

    when (dialogType) {

        DialogType.NORMAL -> {

        }

        DialogType.SUCCESS -> {
            tvTitle.text = "تایید"
            ivIcon.setImageResource(R.drawable.ic_success)
            btnClose.setBackgroundResource(R.drawable.background_success)
        }

        DialogType.WARNING -> {
            tvTitle.text = "هشدار"
            ivIcon.setImageResource(R.drawable.ic_warning)
            btnClose.setBackgroundResource(R.drawable.background_warning)
        }

        DialogType.ERROR -> {
            tvTitle.text = "خطا"
            ivIcon.setImageResource(R.drawable.ic_error)
            btnClose.setBackgroundResource(R.drawable.background_error)
        }
    }
    tvMessage.text = message

    btnClose.setOnClickListener(object : View.OnClickListener {
        override fun onClick(p0: View?) {
            dialog.dismiss()
        }
    })

    dialog.show()
}


fun showDialog2(
    activity: Activity,
    dialogType: DialogType,
    message: String,
    clickListener: SweetAlertDialog.OnSweetClickListener
): SweetAlertDialog {
    val sweetAlertDialog: SweetAlertDialog
    var _message = message
    when (dialogType) {

        DialogType.NORMAL -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = "پیام"
        }

        DialogType.SUCCESS -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = "پیام"
        }

        DialogType.ERROR -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.titleText = "خطا!!"
        }

        DialogType.WARNING -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = "هشدار!!"
        }

    }

    if (_message.length > 25) {
        _message = _message.substring(0, 25) + "\n" + _message.substring(25, _message.length)
    }

    sweetAlertDialog.contentText = _message
    sweetAlertDialog.setCancelable(false)
    sweetAlertDialog.setConfirmClickListener(clickListener)
    sweetAlertDialog.show()
    return sweetAlertDialog
}

fun showDialog3(
    activity: Activity,
    dialogType: DialogType,
    message: String
): SweetAlertDialog {
    val sweetAlertDialog: SweetAlertDialog
    var _message = message
    when (dialogType) {

        DialogType.NORMAL -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = "پیام"
        }

        DialogType.SUCCESS -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = "پیام"
        }

        DialogType.ERROR -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.titleText = "خطا!!"
        }

        DialogType.WARNING -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = "هشدار!!"
        }

    }

    if (_message.length > 25) {
        _message = _message.substring(0, 25) + "\n" + _message.substring(25, _message.length)
    }

    sweetAlertDialog.contentText = _message
    sweetAlertDialog.setCancelable(false)
    sweetAlertDialog.setNeutralClickListener { activity.finish() }
    sweetAlertDialog.show()
    return sweetAlertDialog
}

fun showBarcodeDialog(context: Context, listener: IClickListener) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_barcode)
    dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(true)
    val acbConfirm = dialog.findViewById<AppCompatButton>(R.id.acbConfirm)
    val etBarcode = dialog.findViewById<EditText>(R.id.etBarcode)
    val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)


    etBarcode.requestFocus()
    acbConfirm.setOnClickListener {
        if (!etBarcode.text.toString().isEmpty() && etBarcode.text.toString().length == 11) {
            listener.onClick(etBarcode, dialog)

            dialog.dismiss()
        } else if (etBarcode.text.toString().length != 11) {
            val errorMessage = "طول بارکد باید 11 رقم باشد"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        } else {
            val errorMessage = "لطفا بارکد را وارد نمایید"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    etBarcode.addTextChangedListener {
        Log.i("DEBUG", "text changed. count=${it?.length}")
        if (it?.length == 11) {
            acbConfirm.setBackgroundResource(R.drawable.backscanbarcode)
        }else{
            acbConfirm.setBackgroundResource(R.drawable.backscanbarcodetoosi)

        }
    }

    ivClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}


fun showAssetmoreDialog(context: Context, listener: IClickListenerWithEditText,listner2gone:IClickListenergone) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_barcode_with_tag_more)
    dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(true)
    dialog.window?.setDimAmount(0.9f) // این قسمت مهمه!
    val acbConfirm = dialog.findViewById<AppCompatButton>(R.id.acbConfirm)
    val sabtmoreasset = dialog.findViewById<AppCompatButton>(R.id.sabtmoreasset)
    val sabtmoreasset2 = dialog.findViewById<AppCompatButton>(R.id.sabtmoreasset2)
    val sabtmoreasset3 = dialog.findViewById<AppCompatButton>(R.id.sabtmoreasset3)
    val sabtmoreasset4 = dialog.findViewById<AppCompatButton>(R.id.sabtmoreasset4)
    val etBarcode = dialog.findViewById<EditText>(R.id.etBarcode)
    val moreassetedt = dialog.findViewById<EditText>(R.id.moreassetedt)
    val moreassetedt2 = dialog.findViewById<EditText>(R.id.moreassetedt2)
    val moreassetedt3 = dialog.findViewById<EditText>(R.id.moreassetedt3)
    val moreassetedt4 = dialog.findViewById<EditText>(R.id.moreassetedt4)
    val asset_title_assetmore = dialog.findViewById<TextView>(R.id.asset_title_assetmore)
    val asset_tag_assetmore = dialog.findViewById<TextView>(R.id.asset_tag_assetmore)
    val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)


    val moreAssetsContainer = dialog.findViewById<LinearLayout>(R.id.moreAssetsContainer)
    val moreAssetsContainer2 = dialog.findViewById<LinearLayout>(R.id.moreAssetsContainer2)
    val moreAssetsContainer3 = dialog.findViewById<LinearLayout>(R.id.moreAssetsContainer3)
    val moreAssetsContainer4 = dialog.findViewById<LinearLayout>(R.id.moreAssetsContainer4)


    moreAssetsContainer2.visibility = View.GONE
    moreAssetsContainer3.visibility = View.GONE
    moreAssetsContainer4.visibility = View.GONE

    listner2gone.onViewCreated(asset_title_assetmore,asset_tag_assetmore,moreAssetsContainer,moreAssetsContainer2, moreAssetsContainer3, moreAssetsContainer4)

//    etBarcode.requestFocus()
//    acbConfirm.setOnClickListener {
//        if (!etBarcode.text.toString().isEmpty() && etBarcode.text.toString().length == 11) {
//            listener.onClick(acbConfirm,etBarcode,dialog)
//            dialog.dismiss()
//        } else if (etBarcode.text.toString().length != 11) {
//            val errorMessage = "طول بارکد باید 11 رقم باشد"
//            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
//        } else {
//            val errorMessage = "لطفا بارکد را وارد نمایید"
//            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
//        }
//
    acbConfirm.setOnClickListener {

        listener.onClick(acbConfirm, etBarcode, dialog)
        dialog.dismiss()


    }


//        etBarcode.addTextChangedListener {
//            Log.i("DEBUG", "text changed. count=${it?.length}")
//            if (it?.length == 11) {
//                acbConfirm.setBackgroundResource(R.drawable.backscanbarcode)
//            }else{
//                acbConfirm.setBackgroundResource(R.drawable.backscanbarcodetoosi)
//
//        }
//        }
//    }


    sabtmoreasset.setOnClickListener{
        moreassetedt.clearFocus() // مقدار رو ذخیره کن
            if (!moreassetedt.text.toString().isEmpty() && moreassetedt.text.toString().length in 6..11) {
                listener.onClick(sabtmoreasset, moreassetedt,dialog)
            } else {
                val errorMessage = "طول برچسب باید بین 6 تا 11 رقم باشد"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }


    }
    sabtmoreasset2.setOnClickListener{
        moreassetedt.clearFocus() // مقدار رو ذخیره کن
            if (!moreassetedt2.text.toString().isEmpty() && moreassetedt2.text.toString().length in 6..11) {
                listener.onClick(sabtmoreasset2, moreassetedt2,dialog)
            } else {
                val errorMessage = "طول برچسب باید بین 6 تا 11 رقم باشد"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }


    }
    sabtmoreasset3.setOnClickListener{
        moreassetedt.clearFocus() // مقدار رو ذخیره کن
            if (!moreassetedt3.text.toString().isEmpty() && moreassetedt3.text.toString().length in 6..11) {
                listener.onClick(sabtmoreasset3, moreassetedt3,dialog)
            } else {
                val errorMessage = "طول برچسب باید بین 6 تا 11 رقم باشد"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }


    }
    sabtmoreasset4.setOnClickListener{
        moreassetedt.clearFocus() // مقدار رو ذخیره کن
            if (!moreassetedt4.text.toString().isEmpty() && moreassetedt4.text.toString().length in 6..11) {
                listener.onClick(sabtmoreasset4, moreassetedt4,dialog)
            } else {
                val errorMessage = "طول برچسب باید بین 6 تا 11 رقم باشد"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }


    }

    ivClose.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

interface IClickListenergone {
    fun onViewCreated(asset_title_assetmore:TextView,asset_tag_assetmore:TextView, moreAssetsContainer: LinearLayout,moreAssetsContainer2: LinearLayout, moreAssetsContainer3: LinearLayout, moreAssetsContainer4: LinearLayout)
    // ... سایر متدهای مورد نیاز ...
}




fun showDialog2(
    activity: Activity,
    dialogType: DialogType,
    message: String
): SweetAlertDialog {
    val sweetAlertDialog: SweetAlertDialog
    when (dialogType) {

        DialogType.NORMAL -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = "پیام"
        }

        DialogType.SUCCESS -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = "موفقیت آمیز"
        }

        DialogType.ERROR -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.titleText = "خطا"
        }

        DialogType.WARNING -> {
            sweetAlertDialog = SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = "هشدار!!"
        }

    }
    sweetAlertDialog.contentText = message
    sweetAlertDialog.setCancelable(false)
    sweetAlertDialog.setNeutralClickListener { activity.finish() }
    //sweetAlertDialog.show()
    return sweetAlertDialog
}