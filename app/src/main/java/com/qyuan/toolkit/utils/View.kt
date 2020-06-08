package com.qyuan.toolkit.utils

import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import com.qyuan.toolkit.AppContext

fun Float.dp() = TypedValue.applyDimension(
    COMPLEX_UNIT_DIP,
    this,
    AppContext.get().resources.displayMetrics
).toInt()

