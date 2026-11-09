
package com.tehranmunicipality.assetmanagement.ui.asset_information

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BarcodeOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val density = resources.displayMetrics.density

    // لایه تاریک اطراف کادر
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x99000000.toInt()
    }

    // شفاف کردن داخل کادر
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    // حاشیه کادر اسکن
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF478886.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 4f * density
    }

    private val frameRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            null
        )

        // تاریک کردن اطراف صفحه
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            overlayPaint
        )

        // ابعاد کادر اسکن
        val frameWidth = 280f * density
        val frameHeight = 120f * density

        // قرارگیری دقیق در مرکز صفحه
        val left = (width - frameWidth) / 2f
        val top = (height - frameHeight) / 2f

        frameRect.set(
            left,
            top,
            left + frameWidth,
            top + frameHeight
        )

        // شفاف کردن فضای داخل کادر
        canvas.drawRoundRect(
            frameRect,
            12f * density,
            12f * density,
            clearPaint
        )

        canvas.restoreToCount(saveCount)

        // رسم حاشیه فیروزه‌ای
        canvas.drawRoundRect(
            frameRect,
            12f * density,
            12f * density,
            framePaint
        )
    }
}
