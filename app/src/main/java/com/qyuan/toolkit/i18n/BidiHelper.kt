package com.qyuan.toolkit.i18n

import android.icu.lang.UCharacter
import android.icu.lang.UProperty
import android.os.Build
import android.text.TextUtils

/**
 * Helper class for Bi-directional text.
 * More information about characters in "[\u202A\u202B\u202C\u202E\u202F]":
 * @see android.text.BidiFormatter
 * Created by qyuan on 2018/9/28.
 */
object BidiHelper {
    private val regex = "[\u202A\u202B\u202C\u202E\u202F]".toRegex()

    fun equalsIgnoreBidiControls(a: CharSequence?, b: CharSequence?): Boolean {
        return TextUtils.equals(a?.removeBidiControls(), b?.removeBidiControls())
    }

    private fun CharSequence.removeBidiControls(): CharSequence =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.filterNot {
                UCharacter.hasBinaryProperty(it.toInt(), UProperty.BIDI_CONTROL)
            }
        } else {
            this.replace(regex, "")
        }
}
