package com.qyuan.toolkit.customview.taglayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.qyuan.toolkit.R
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Created by qyuan on 2020-01-09.
 */
class TagLayout : ViewGroup {

    var maxLines: Int = -1
        set(value) {
            field = value
            requestLayout()
        }
    var itemSpace = 0
        set(value) {
            field = value
            requestLayout()
        }
    var adapter: TagLayoutAdapter? = null
        set(value) {
            field = value
            reset()
        }

    private var availableChildren = arrayOfNulls<ItemViewInfo>(INIT_COUNT)
    private var availableChildCount = 0

    constructor(context: Context) : super(context) {
        initAttr(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttr(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initAttr(context, attrs)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TagLayout)
        itemSpace = attributes.getDimension(R.styleable.TagLayout_itemSpace, 0F).roundToInt()
        maxLines = attributes.getInteger(R.styleable.TagLayout_maxTagLines, -1)
        attributes.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val innerAdapter = adapter
        val itemCount = innerAdapter?.getItemCount()
        if (innerAdapter == null || itemCount == null || itemCount == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val widthSpace = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        val heightSpace = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

        var usedWidth = 0
        var usedHeight = 0

        var maxWidth = 0
        var rowMaxHeight = 0

        var childState = 0

        var currentLine = 1

        for (index in 0 until itemCount) {
            val childViewInfo = obtainViewInfo(innerAdapter, index)
            val childView = childViewInfo.view

            measureChildWithMargins(
                childView,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                0
            )

            val childHeight = childViewInfo.getHeightWithMargin()
            val childWidth = childViewInfo.getWidthWithMargin()

            if (usedHeight + childHeight > heightSpace) {
                break
            }
            val shouldBreakLine = usedWidth + childWidth > widthSpace
            if (shouldBreakLine && currentLine == maxLines) {
                break
            }
            val lp = childView.layoutParams as MarginLayoutParams
            if (!shouldBreakLine) {
                childViewInfo.setPoint(
                    paddingStart + usedWidth + lp.marginStart,
                    paddingTop + usedHeight + lp.topMargin
                )
                rowMaxHeight = max(rowMaxHeight, childHeight)
            } else {
                currentLine++
                maxWidth = max(maxWidth, usedWidth)
                usedWidth = 0
                usedHeight += rowMaxHeight
                rowMaxHeight = childHeight
                childViewInfo.setPoint(
                    paddingStart + usedWidth + lp.marginStart,
                    paddingTop + usedHeight + lp.topMargin
                )
            }
            usedWidth += childWidth + itemSpace
            childState = View.combineMeasuredStates(childState, childView.measuredState)
            availableChildCount = index + 1
        }
        usedHeight += rowMaxHeight

        setMeasuredDimension(
            View.resolveSizeAndState(
                max(maxWidth, usedWidth) + paddingStart + paddingEnd,
                widthMeasureSpec,
                childState
            ),
            View.resolveSizeAndState(
                usedHeight + paddingTop + paddingBottom,
                heightMeasureSpec,
                childState
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val innerAdapter = adapter
        val itemCount = adapter?.getItemCount()
        if (innerAdapter == null || itemCount == null || itemCount == 0) {
            reset()
            return
        }
        removeAllViewsInLayout()
        for (position in 0 until availableChildCount) {
            val childInfo = obtainViewInfo(innerAdapter, position)
            val child = childInfo.view
            if (child.visibility == View.GONE) {
                continue
            }
            val lp = child.layoutParams as MarginLayoutParams
            addViewInLayout(child, position, lp, true)
            child.layout(
                childInfo.start,
                childInfo.top,
                childInfo.start + child.measuredWidth,
                childInfo.top + child.measuredHeight
            )
        }
    }

    private fun obtainViewInfo(innerAdapter: TagLayoutAdapter, position: Int): ItemViewInfo {
        val availableItem = availableChildren.getOrNull(position)
        if (availableItem != null) {
            return availableItem
        }
        if (availableChildren.size - availableChildCount <= 1) {
            grow()
        }
        return ItemViewInfo(view = innerAdapter.getView(this, position))
            .also {
                availableChildren[position] = it
            }
    }

    private fun reset() {
        availableChildren = arrayOfNulls(INIT_COUNT)
        availableChildCount = 0
        removeAllViewsInLayout()
    }

    private fun grow() {
        val newSize = availableChildCount * 2
        availableChildren = availableChildren.copyOf(newSize)
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

    private data class ItemViewInfo(
        var start: Int = 0,
        var top: Int = 0,
        val view: View
    ) {
        fun getWidthWithMargin(): Int {
            val lp = view.layoutParams as MarginLayoutParams
            return view.measuredWidth + lp.marginStart + lp.marginEnd
        }

        fun getHeightWithMargin(): Int {
            val lp = view.layoutParams as MarginLayoutParams
            return view.measuredHeight + lp.topMargin + lp.bottomMargin
        }

        fun setPoint(start: Int, top: Int) {
            this.start = start
            this.top = top
        }
    }

    companion object {
        private const val INIT_COUNT = 2
    }
}
