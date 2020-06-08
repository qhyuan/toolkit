package com.qyuan.toolkit.utils

import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.qyuan.toolkit.AppContext

object DisplayHelper {

    private val resources = AppContext.get().resources
    private val displayMetrics = resources.displayMetrics

    fun density() = displayMetrics.density

    fun widthPixels() = displayMetrics.widthPixels

    fun heightPixels() = displayMetrics.heightPixels

    fun realHeightPixels(): Int {
        val wm = ContextCompat.getSystemService(AppContext.get(), WindowManager::class.java)
            ?: throw ClassNotFoundException()
        return wm.run {
            val metrics = DisplayMetrics()
            defaultDisplay.getRealMetrics(metrics)
            metrics.heightPixels
        }
    }
}
