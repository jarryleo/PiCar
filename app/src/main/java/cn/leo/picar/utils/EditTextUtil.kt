package cn.leo.picar.utils

import android.text.TextUtils
import android.widget.EditText


fun EditText.getInt(): Int {
    val s = this.text.toString()
    if(TextUtils.isEmpty(s)){
        return 0
    }
    if (TextUtils.isDigitsOnly(s)){
        return s.toInt()
    }
    return 0
}