package com.qyuan.toolkit.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop

class CustomViewGroup : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedWidth = paddingStart + paddingEnd
        var usedHeight = paddingTop + paddingBottom

        var childState = 0

        for (index in 0 until childCount) {
            val child: View? = get(index)
            if (child == null || child.isGone) {
                continue
            }
            val childParam = requireNotNull(child.layoutParams as? MarginLayoutParams)

            child.measure(
                getChildMeasureSpec(
                    widthMeasureSpec,
                    usedWidth,
                    childParam.width
                ),
                getChildMeasureSpec(
                    heightMeasureSpec,
                    usedHeight,
                    childParam.height
                )
            )
            if (child.measuredWidthAndState and MEASURED_STATE_MASK == MEASURED_STATE_TOO_SMALL) {
                // TODO child need more space
                Log.d(TAG, "MEASURED_STATE_TOO_SMALL")
            }
            // 如果你不处理这种情况，把 MEASURED_STATE_TOO_SMALL 作为参数传递给setMeasuredDimension以告知父控件
            // 通常情况下系统只会在协商测量的过程中处理MEASURED_STATE_TOO_SMALL
            // see ViewRootImpl.measureHierarchy
            childState = View.combineMeasuredStates(childState, child.measuredState)
            usedWidth += child.measuredWidth + childParam.marginStart + childParam.marginEnd
            usedHeight += child.measuredHeight + childParam.topMargin + childParam.bottomMargin
        }
        val w = resolveSizeAndState(
            paddingStart + paddingEnd + usedWidth,
            widthMeasureSpec,
            childState
        )
        val h = resolveSizeAndState(
            paddingTop + paddingBottom + usedHeight,
            heightMeasureSpec,
            childState
        )
        setMeasuredDimension(w, h)
        Log.d(
            TAG,
            "CustomViewGroup $measuredWidth $measuredHeight"
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var childStart = paddingStart
        var childTop = paddingTop

        for (index in 0 until childCount) {
            val child: View = getChildAt(index) ?: continue
            if (child.isGone) {
                continue
            }
            childStart += child.marginStart
            childTop += child.marginTop
            child.layout(
                childStart,
                childTop,
                childStart + child.measuredWidth,
                childTop + child.measuredHeight
            )
            childStart += child.measuredWidth + child.marginEnd
            childTop += child.measuredHeight + child.marginEnd
        }
    }

    // support Margin for children view
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    companion object {
        private const val TAG = "CustomView"
    }
}
