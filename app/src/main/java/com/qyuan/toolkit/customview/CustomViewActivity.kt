package com.qyuan.toolkit.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.qyuan.toolkit.R

class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
    }

    fun showToast(view: View) {
        Toast.makeText(this, view.toString(), Toast.LENGTH_SHORT).show()
    }
}
