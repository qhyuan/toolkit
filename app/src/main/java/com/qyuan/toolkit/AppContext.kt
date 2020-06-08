package com.qyuan.toolkit

import android.content.Context

object AppContext {
    private var context: Context? = null

    fun init(base: Context) {
        if (context != null) {
            throw  IllegalStateException("App context already set")
        }
        context = base
    }

    fun get() = requireNotNull(context) { "App context not set" }
}
