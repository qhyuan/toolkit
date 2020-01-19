package com.qyuan.toolkit.customview.taglayout

/**
 * Created by qyuan on 2020-01-13.
 */
abstract class TagLayoutAdapter {

    abstract fun getItemCount(): Int

    abstract fun getView(position: Int)

}
