package com.smproject.cityradio

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

public class CustomTypefaceSpan(
    family: String?,
    private val typeface: Typeface
) :
    MetricAffectingSpan() {

    override fun updateMeasureState(p: TextPaint) {
        applyCustomTypeface(p, typeface)
    }

    override fun updateDrawState(tp: TextPaint) {
        applyCustomTypeface(tp, typeface)
    }

    private fun applyCustomTypeface(paint: Paint, typeface: Typeface) {
        paint.typeface = typeface
    }
}