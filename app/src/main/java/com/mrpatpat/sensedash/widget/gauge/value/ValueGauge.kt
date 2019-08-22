package com.mrpatpat.sensedash.widget.gauge.value

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.mrpatpat.sensedash.R
import androidx.core.content.res.ResourcesCompat
import android.graphics.Rect
import kotlin.math.min

class ValueGauge : View {

    private var textPaint: TextPaint = TextPaint()
    private val preAllocatedRect = Rect()

    private var _value: Int? = 0
    private var _unit = context.getString(R.string.unit_degree_celsius)

    var value: Int?
        get() = _value
        set(value) {
            _value = value
            invalidate()
        }

    var unit: String?
        get() = _unit
        set(unit) {
            _unit = unit
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingHorizontal = 10 //width.toFloat() * 0.1f
        val paddingVertical = 10 //height.toFloat() * 0.1f

        val labelSizePercent = 0.25f
        val valueSizePercent = 0.75f

        // align top left with padding to top and left, fills 25% of the whole view minus the padding
        "OIL".let {
            textPaint.textAlign = Paint.Align.CENTER
            val desiredTextHeight = height.toFloat() * labelSizePercent - paddingVertical
            val desiredTextWidth = width.toFloat() - paddingHorizontal
            textPaint.color = Color.WHITE
            setTextSizeToFitDesiredRect(textPaint, desiredTextWidth , desiredTextHeight, it)
            textPaint.getTextBounds(it, 0, it.length, preAllocatedRect)
            val currentTextHeight = preAllocatedRect.height().toFloat()
            val currentTextWidth = preAllocatedRect.width().toFloat()
            canvas.drawText(
                it,
                currentTextWidth / 2 + paddingHorizontal,
                currentTextHeight + paddingVertical,
                textPaint
            )
        }

        // align bottom right with padding to bottom and left, fills 75% of the whole view minus the padding
        getDisplayedText().let {
            textPaint.textAlign = Paint.Align.CENTER
            val desiredTextHeight = height.toFloat() * valueSizePercent -  paddingVertical
            val desiredTextWidth = width.toFloat() - paddingHorizontal
            setTextColorForLimits()
            setTextSizeToFitDesiredRect(textPaint, desiredTextWidth, desiredTextHeight, it)
            textPaint.getTextBounds("2", 0, 1, preAllocatedRect)
            canvas.drawText(
                it,
                width - desiredTextWidth / 2 - paddingHorizontal, //TODO is a bit off
                height.toFloat() - paddingVertical,
                textPaint
            )
        }
    }

    private fun setTextColorForLimits() {
        textPaint.color = getTextColor()
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        loadAttributes(attrs, defStyle)
        initPaint()
        invalidate()
    }

    private fun initPaint() {
        textPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            typeface = ResourcesCompat.getFont(context, R.font.digital_seven_mono)
        }
    }

    private fun loadAttributes(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ValueGauge, defStyle, 0
        )

        _value = a.getInteger(R.styleable.ValueGauge_value, 0)
        _unit = a.getString(R.styleable.ValueGauge_unit)

        a.recycle()
    }

    private fun getTextColor(): Int {
        return when {
            value!! > 150 -> Color.RED
            value!! > 120 -> Color.YELLOW
            else -> Color.WHITE
        }
    }

    private fun getDisplayedText(): String {
        return value.toString() + " " + unit
    }

    private fun setTextSizeToFitDesiredRect(
        paint: Paint,
        desiredWidth: Float,
        desiredHeight: Float,
        text: String
    ) {
        val broadestText = "2".repeat(text.length)
        val testTextSize = 48f
        paint.textSize = testTextSize
        paint.getTextBounds(broadestText, 0, broadestText.length, preAllocatedRect)
        val desiredTextSize = testTextSize * min(desiredWidth / preAllocatedRect.width(), desiredHeight / preAllocatedRect.height())
        paint.textSize = desiredTextSize
    }

}
