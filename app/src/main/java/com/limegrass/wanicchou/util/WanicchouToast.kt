package com.limegrass.wanicchou.util

import android.content.Context
import android.widget.Toast

var WanicchouToast : Toast? = null

fun cancelSetAndShowWanicchouToast(context : Context,
                                   toastText : String,
                                   duration: Int) {
    WanicchouToast?.cancel()
    WanicchouToast = Toast.makeText(context,
            toastText,
            duration)
    WanicchouToast!!.show()

}