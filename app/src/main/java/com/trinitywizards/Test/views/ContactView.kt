package com.trinitywizards.Test.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.trinitywizards.Test.R
import kotlin.math.roundToInt


class ContactView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mPaint = Paint()
    private val mPaint1 = Paint()
    private var mSize = 0f
    private var mText = ""

    init {
        background = null
        mPaint.color = ContextCompat.getColor(context, R.color.blue)
        mPaint1.color = Color.WHITE
        mPaint1.textAlign = Paint.Align.LEFT
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w < h) {
            mSize = w.toFloat()
        } else {
            mSize = h.toFloat()
        }
        mPaint1.textSize = mSize * 0.5f
    }

    override fun draw(canvas: Canvas) {
        var centerX = width * 0.5f
        var centerY = height * 0.5f
        canvas.drawCircle(
            centerX,
            centerY,
            mSize * 0.5f,
            mPaint
        )
        if (mText.isEmpty()) {
            var mIcon = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.ic_person_white
            )
            mIcon = scaleBitmap(mIcon, (mSize * 0.6).roundToInt(), (mSize * 0.6).roundToInt())
            val x = mIcon.width * 0.5f
            val y = mIcon.height * 0.5f
            canvas.drawBitmap(mIcon, centerX - x, centerY - y, null)
        } else {
            val rect = Rect()
            mPaint1.getTextBounds(mText, 0, mText.length, rect)
            val x = width / 2f - rect.width() / 2f - rect.left
            val y = height / 2f + rect.height() / 2f - rect.bottom
            canvas.drawText(mText, x, y, mPaint1)
        }

        super.draw(canvas)
    }

    fun scaleBitmap(bitmap: Bitmap, wantedWidth: Int, wantedHeight: Int): Bitmap {
        val originalWidth = bitmap.width.toFloat()
        val originalHeight = bitmap.height.toFloat()
        val output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val m = Matrix()

        val scalex = wantedWidth / originalWidth
        val scaley = wantedHeight / originalHeight
        val xTranslation = 0.0f
        val yTranslation = (wantedHeight - originalHeight * scaley) / 2.0f

        m.postTranslate(xTranslation, yTranslation)
        m.preScale(scalex, scaley)
        // m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        val paint = Paint()
        paint.isFilterBitmap = true
        canvas.drawBitmap(bitmap, m, paint)

        return output
    }

    fun setText(text: String) {
        mText = text
        invalidate()
    }

}