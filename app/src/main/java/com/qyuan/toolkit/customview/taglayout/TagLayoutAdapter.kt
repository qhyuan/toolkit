package com.qyuan.toolkit.customview.taglayout

import android.view.View
import android.view.ViewGroup

/**
 * Created by qyuan on 2020-01-13.
 */
interface TagLayoutAdapter {

    fun getItemCount(): Int

    fun getView(parent: ViewGroup, position: Int): View
}
