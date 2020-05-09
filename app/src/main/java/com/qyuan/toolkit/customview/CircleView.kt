package com.qyuan.toolkit.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

/**
 * Created by qyuan on 2019-12-02.
 */
class CircleView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val rect = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.UNSPECIFIED ->
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            MeasureSpec.EXACTLY ->
                widthSize
            MeasureSpec.AT_MOST ->
                min(max(layoutParams.width, suggestedMinimumWidth), widthSize)
            else ->
                getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        }

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED ->
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
            MeasureSpec.EXACTLY ->
                heightSize
            MeasureSpec.AT_MOST ->
                min(max(layoutParams.height, suggestedMinimumHeight), heightSize)
            else ->
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        }


        val w = resolveSizeAndState(width, widthMeasureSpec, 0)
        val h = resolveSizeAndState(height, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
        Log.d(
            TAG,
            " CircleView " +
                MeasureSpec.toString(widthMeasureSpec) +
                MeasureSpec.toString(heightMeasureSpec) +
                "$measuredWidth $measuredHeight"
        )
    }

    private val paint = Paint().apply {
        color = Color.BLUE
    }

    override fun onDraw(canvas: Canvas) {
        rect.set(paddingStart, paddingTop, width - paddingEnd, height - paddingBottom)
        canvas.drawCircle(
            rect.exactCenterX(),
            rect.exactCenterY(),
            min(rect.width(), rect.height()) / 2F,
            paint
        )
    }

    companion object {
        private const val TAG = "CustomView"
    }
}