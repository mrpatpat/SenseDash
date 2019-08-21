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


class ValueGauge : View {

    private lateinit var textPaint: TextPaint
    private var _value: Int? = 0
    private var _unit = context.getString(R.string.unit_degree_celsius)

    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    var value: Int?
        get() = _value
        set(value) {
            _value = value
            invalidatePaint()
            invalidate()
        }

    var unit: String?
        get() = _unit
        set(unit) {
            _unit = unit
            invalidatePaint()
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

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ValueGauge, defStyle, 0
        )

        _value = a.getInteger(R.styleable.ValueGauge_value, 0)
        _unit = a.getString(R.styleable.ValueGauge_unit)

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.digital_seven_mono)
        }

        // Update TextPaint and text measurements from attributes
        invalidatePaint()
    }

    private fun invalidatePaint() {
        textPaint?.textSize = 40 * resources.displayMetrics.scaledDensity
        textPaint?.let {
            textWidth = it.measureText(getDisplayedText())
            textHeight = it.fontMetrics.bottom
        }
        if(value!! > 150) {
            textPaint.color = Color.RED
        } else if(value!! > 120) {
            textPaint.color = Color.YELLOW
        } else {
            textPaint.color = Color.WHITE
        }
    }

    private fun getDisplayedText() = value.toString() + " " + unit

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        getDisplayedText().let {
            canvas.drawText(
                it,
                paddingLeft + (contentWidth - textWidth) / 2,
                paddingTop + (contentHeight + textHeight) / 2,
                textPaint
            )
        }
    }
}
