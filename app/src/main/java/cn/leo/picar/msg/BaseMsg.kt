package cn.leo.picar.msg

open class BaseMsg<T>(var type: Int = 0,
                      var code: Int = 0,
                      var msg: String = "",
                      var data: T? = null){
    constructor():this(0,0,"",null)
}