package com.qyuan.toolkit.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PathEffect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.qyuan.toolkit.R

/**
 * Created by qyuan on 2020/5/7.
 */
class DashBoardView : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 6F
        pathEffect = PathEffect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(0F, 0F, width.toFloat(), height.toFloat(), 180F, 180F, false, paint)
    }
}
