package com.qyuan.toolkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qyuan.toolkit.utils.dp
import kotlinx.android.synthetic.main.activity_test_code.*

class TestCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_code)
        resource_tv.setOnClickListener { updateTextView() }
    }

    private fun updateTextView(){
        resource_tv.text = "${100F.dp()}"
    }
}