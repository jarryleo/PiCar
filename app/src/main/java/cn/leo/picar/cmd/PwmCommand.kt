package cn.leo.picar.cmd

data class PwmCommand(var pwmArray:IntArray){
    constructor():this(intArrayOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PwmCommand

        if (!pwmArray.contentEquals(other.pwmArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return pwmArray.contentHashCode()
    }
}