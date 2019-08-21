package com.mrpatpat.sensedash.widget.gauge.value

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.mrpatpat.sensedash.R
import kotlinx.android.synthetic.main.activity_value_gauge_debug.*

class ValueGaugeDebugActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val defaultValue = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_gauge_debug)
        initSeekBar()
        initValueGauge()
    }

    private fun initValueGauge() {
        valueGauge.value = defaultValue
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(this)
        seekBar.min = 100
        seekBar.max = 200
        seekBar.progress = defaultValue
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        valueGauge.value = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}
