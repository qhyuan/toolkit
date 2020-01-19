package com.qyuan.toolkit.customview.taglayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 * Created by qyuan on 2020-01-09.
 */
class TagLayout : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var adapter: TagLayoutAdapter? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        var usedWidth = paddingStart + paddingEnd
        var usedHeight = 0
        var column = 0
        var rowMaxHeight = 0
        var rowMaxWidth = 0

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var childState = 0
        val childCount = adapter?.getItemCount() ?: 0

        for (index in 0 until childCount) {
            val child = getChildAt(index)
            val nextChild: View? = getChildAt(index + 1)
            val childHeight = getHeightWithMargin(child)
            val childWidth = getWidthWithMargin(child)
            if (usedWidth + childWidth <= maxWidth || column == 0) {
                usedWidth += childWidth
                column++
                rowMaxHeight = max(rowMaxHeight, childHeight)
                if (nextChild == null || usedWidth + nextChild.measuredWidth > maxWidth) {
                    rowMaxWidth = max(rowMaxWidth, usedWidth)
                    usedHeight += rowMaxHeight
                    rowMaxHeight = 0
                }
            } else {
                rowMaxWidth = max(rowMaxWidth, usedWidth)
                usedHeight += rowMaxHeight
                rowMaxHeight = 0
                usedWidth = childWidth + paddingStart + paddingEnd
                column = 0
            }
            childState = View.combineMeasuredStates(childState, child.measuredState)
        }
        setMeasuredDimension(
            View.resolveSizeAndState(rowMaxWidth, widthMeasureSpec, childState),
            View.resolveSizeAndState(usedHeight, heightMeasureSpec, childState)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childStart = paddingStart
        var childTop = paddingTop
        var maxRowHeight = 0
        var indexInRow = 0
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            val childWidthWithMargin = getWidthWithMargin(child)
            val childHeightWithMargin = getHeightWithMargin(child)
            val lp = child.layoutParams as MarginLayoutParams
            if (childStart + childWidthWithMargin + paddingEnd <= measuredWidth || indexInRow == 0) {
                child.layout(
                    childStart + lp.marginStart,
                    childTop + lp.topMargin,
                    childStart + lp.marginStart + child.measuredWidth,
                    childTop + lp.topMargin + child.measuredHeight
                )
                maxRowHeight = max(maxRowHeight, childHeightWithMargin)
                if (childStart + childWidthWithMargin + getWidthWithMargin(getChildAt(index + 1)) + paddingEnd > measuredWidth) {
                    childStart = paddingStart
                    childTop += maxRowHeight
                    maxRowHeight = 0
                    indexInRow = 0
                } else {
                    childStart += childWidthWithMargin
                    indexInRow++
                }
            } else {
                child.layout(
                    childStart + lp.marginStart,
                    childTop + lp.topMargin,
                    childStart + lp.marginStart + child.measuredWidth,
                    childTop + lp.topMargin + child.measuredHeight
                )
                childStart += childWidthWithMargin
            }
        }
    }

    private fun getWidthWithMargin(view: View?): Int {
        view ?: return 0
        val lp = view.layoutParams as MarginLayoutParams
        return view.measuredWidth + lp.marginStart + lp.marginEnd
    }

    private fun getHeightWithMargin(view: View): Int {
        val lp = view.layoutParams as MarginLayoutParams
        return view.measuredHeight + lp.topMargin + lp.bottomMargin
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
}