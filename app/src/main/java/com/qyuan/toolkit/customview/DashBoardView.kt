package com.qyuan.toolkit.customview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
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

    private val arcRectF = RectF()
    private val effectRectF = RectF(0F, 0F, 5F, 20F)

    private val paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        style = Paint.Style.STROKE
        strokeWidth = 6F
        strokeCap = Paint.Cap.BUTT
    }

    private val arcPath = Path()
    private val arcPathMeasure = PathMeasure()
    private val effectPath = Path().apply {
        addRect(effectRectF, Path.Direction.CW)
    }
    private var pathEffect: PathDashPathEffect? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val offset = paint.strokeWidth / 2
        arcRectF.set(
            0F + offset,
            0F + offset,
            width.toFloat() - offset,
            height.toFloat() - offset
        )
        arcPath.reset()
        arcPath.addArc(arcRectF, 180F, 180F)
        arcPathMeasure.setPath(arcPath, false)
        pathEffect = PathDashPathEffect(
            effectPath,
            (arcPathMeasure.length - 5F) / 11,
            0F,
            PathDashPathEffect.Style.ROTATE
        )
        ValueAnimator.ofFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(arcRectF, 180F, 180F, false, paint)
        paint.pathEffect = pathEffect
        canvas.drawPath(arcPath, paint)
        paint.pathEffect = null
    }
}
