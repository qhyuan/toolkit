package com.qyuan.toolkit.eventdispatch

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import java.util.stream.Stream
import kotlin.math.abs

/**
 * Created by qyuan on 2019-12-05.
 */
class MyFrameLayout : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var moveTimes = 0

    private var touchmove = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private var mIsScrolling = false

    override fun scrollTo(x: Int, y: Int) {
        mIsScrolling = scrollY != x || scrollY != y
        super.scrollTo(x, y)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            moveTimes = 0
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            moveTimes++
        }
        if (moveTimes <= 1 || ev.action != MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "onInterceptTouchEvent ${MotionEvent.actionToString(ev.action)}")
        }
        /*
            * This method JUST determines whether we want to intercept the motion.
            * If we return true, onTouchEvent will be called and we do the actual
            * scrolling there.
            */
        val intercept = when (ev.actionMasked) {
            // Always handle the case of the touch gesture being complete.
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Release the scroll.
                mIsScrolling = false
                false // Do not intercept touch event, let the child handle it
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsScrolling) {
                    // We're currently scrolling, so yes, intercept the
                    // touch event!
                    true
                } else {

                    // If the user has dragged her finger horizontally more than
                    // the touch slop, start the scroll

                    // left as an exercise for the reader
                    val xDiff: Int = abs(startPointF.x - ev.x).toInt()

                    // Touch slop should be calculated using ViewConfiguration
                    // constants.
                    if (xDiff > ViewConfiguration.get(context).scaledTouchSlop) {
                        // Start scrolling!
                        mIsScrolling = true
                        true
                    } else {
                        false
                    }
                }
            }
            else -> {
                // In general, we don't want to intercept touch events. They should be
                // handled by the child view.
                false
            }
        }
        return false
    }

    private var startPointF = PointF()

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent ${MotionEvent.actionToString(ev.action)}")
        return true
    }

    companion object {
        private const val TAG = EventDispatchActivity.TAG + " MyFrame"
    }
}
