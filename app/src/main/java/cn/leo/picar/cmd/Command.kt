package cn.leo.picar.cmd

data class Command(//小车方向控制：0待机，1 前，2后，3左，4右，5左转，6右转，7刹车
        var command: Int,
        //速度（1-100）
        var speed: Int = 0) {
    constructor() : this(0, 0)
}