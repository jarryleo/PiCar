package cn.leo.picar.msg

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class BaseMsg<T>(var type: Int = 0,
                      var code: Int = 0,
                      var msg: String = "",
                      var data: T? = null): ParameterizedType {
    constructor() : this(0, 0, "", null)

    override fun getRawType(): Type {
        return BaseMsg::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }

    override fun getActualTypeArguments(): Array<Type> {
        val superclass = this.javaClass.genericSuperclass
        if (superclass is Class<*>) {
            throw RuntimeException("Missing type parameter.")
        }
        return (superclass as ParameterizedType).actualTypeArguments
    }

    fun fromJson(json: String): BaseMsg<T> {
        return Gson().fromJson<BaseMsg<T>>(json, this)
    }
}