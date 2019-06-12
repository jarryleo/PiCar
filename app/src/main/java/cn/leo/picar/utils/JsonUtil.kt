package cn.leo.picar.utils

import com.google.gson.Gson

object JsonUtil {
    fun toJson(any: Any): String {
        return Gson().toJson(any)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return Gson().fromJson(json, classOfT)
    }
}