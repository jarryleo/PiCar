package cn.leo.picar.view

import android.widget.SeekBar
import kotlin.math.acos
import kotlin.math.sqrt

object RockerParser {
    var turnLeft = 1f
    var turnRight = 1f
    fun parseRocker(rockerView: RockerView, sb: SeekBar, parser: (IntArray) -> Unit) {
        val arr = IntArray(8)
        rockerView.setRockerListener { x, y ->
            val speed = sb.progress
            //println("left = $turnLeft   right = $turnRight")
            if (x == 0 && y == 0) {
                val list = mutableListOf<Int>()
                if (turnLeft < 1f) {
                    list.addAll(listOf(1, 2, 5, 6))
                }
                if (turnRight < 1f) {
                    list.addAll(listOf(0, 3, 4, 7))
                }
                setArr(arr, speed, list.toIntArray(), intArrayOf(), intArrayOf())
            } else {
                var a = angle(x, -y) + 22
                if (a >= 360) {
                    a -= 360
                }
                when (a) {
                    in 0 until 45 -> setArr(arr, speed, intArrayOf(0, 2, 4, 6), intArrayOf(0, 4), intArrayOf(2, 6))
                    in 45 until 90 -> setArr(arr, speed, intArrayOf(0, 6), intArrayOf(0), intArrayOf(6))
                    in 90 until 135 -> setArr(arr, speed, intArrayOf(0, 3, 5, 6), intArrayOf(0, 3), intArrayOf(5, 6))
                    in 135 until 180 -> setArr(arr, speed, intArrayOf(3, 5), intArrayOf(5), intArrayOf(3))
                    in 180 until 225 -> setArr(arr, speed, intArrayOf(1, 3, 5, 7), intArrayOf(1, 5), intArrayOf(3, 7))
                    in 225 until 270 -> setArr(arr, speed, intArrayOf(1, 7), intArrayOf(1), intArrayOf(7))
                    in 270 until 315 -> setArr(arr, speed, intArrayOf(1, 2, 4, 7), intArrayOf(4, 7), intArrayOf(1, 2))
                    in 315 until 360 -> setArr(arr, speed, intArrayOf(2, 4), intArrayOf(4), intArrayOf(2))
                }
            }
            parser(arr)
        }
    }

    private fun setArr(arr: IntArray, speed: Int, powerWheels: IntArray, leftWheels: IntArray, rightWheels: IntArray) {
        arr.forEachIndexed { index, _ ->
            if (powerWheels.contains(index)) {
                when {
                    leftWheels.contains(index) -> arr[index] = (speed * turnLeft).toInt()
                    rightWheels.contains(index) -> arr[index] = (speed * turnRight).toInt()
                    else -> arr[index] = speed
                }
            } else {
                arr[index] = 0
            }
        }
    }


    private fun angle(x: Int, y: Int): Int {
        val len = sqrt((x * x + y * y).toDouble())
        var angle = acos(y / len)
        if (x < 0) {
            angle = 2 * Math.PI - angle
        }
        return (angle * 180 / Math.PI).toInt()
    }

}
