package com.qyuan.toolkit

import android.app.Application
import android.content.Context

class AppApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        AppContext.init(base)
    }
}