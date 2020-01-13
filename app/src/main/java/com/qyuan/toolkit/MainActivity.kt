package com.qyuan.toolkit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.qyuan.toolkit.customview.CustomViewActivity
import com.qyuan.toolkit.eventdispatch.EventDispatchActivity
import java.util.PriorityQueue

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openCustomView(view: View) {
        openActivity(CustomViewActivity::class.java)
    }

    fun openEventDispatch(view: View) {
        openActivity(EventDispatchActivity::class.java)
    }

    private fun openActivity(activityClass: Class<out Activity>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
