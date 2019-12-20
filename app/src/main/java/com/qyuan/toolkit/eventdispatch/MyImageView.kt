package com.qyuan.toolkit.eventdispatch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView

/**
 * Created by qyuan on 2019-12-05.
 */
class MyImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private var dispatchmoveTimes = 0
    private var touchMove = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            dispatchmoveTimes = 0
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            dispatchmoveTimes++
        }
        if (dispatchmoveTimes <= 1 || ev.action != MotionEvent.ACTION_MOVE) {
            Log.d(
                TAG,
                "dispatchTouchEvent ${MotionEvent.actionToString(ev.action)}"
            )
        }

        val dispatch = super.dispatchTouchEvent(ev)
        return dispatch
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val touchEvent = super.onTouchEvent(ev)
        if (ev.action == MotionEvent.ACTION_DOWN) {
            touchMove = 0
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            touchMove++
        }
        if (touchMove <= 1 || ev.action != MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "onTouchEvent $touchEvent ${MotionEvent.actionToString(ev.action)}")
        }
        return true
    }

    companion object {
        private const val TAG = EventDispatchActivity.TAG + " MyImage"
    }
}
