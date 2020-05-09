package com.qyuan.toolkit.customview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qyuan.toolkit.R
import com.qyuan.toolkit.customview.taglayout.TagLayoutAdapter
import kotlinx.android.synthetic.main.activity_custom_view.*
import kotlinx.android.synthetic.main.layout_simple_text_view.view.*
import kotlin.random.Random

@SuppressLint("SetTextI18n")
class CustomViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)

        tag_layout.adapter = object : TagLayoutAdapter {

            override fun getItemCount(): Int {
                return 6
            }

            override fun getView(parent: ViewGroup, position: Int): View {
                val res = if (position % 4 == 0) {
                    R.layout.layout_simple_text_view_margin_10
                } else {
                    R.layout.layout_simple_text_view
                }
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(res, parent, false)
                return view.text_view.apply {
                    val color =
                        Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
                    setBackgroundColor(color)
                    text = "#${Color.red(color)}#${Color.green(color)}#${Color.blue(color)}"
                }
            }
        }
    }

    fun showToast(view: View) {
        Toast.makeText(this, view.toString(), Toast.LENGTH_SHORT).show()
    }
}
