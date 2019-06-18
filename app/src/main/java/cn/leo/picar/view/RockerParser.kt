package cn.leo.picar.view

import android.widget.SeekBar
import kotlin.math.acos
import kotlin.math.sqrt

object RockerParser {
    fun parseRocker(rockerView: RockerView, sb: SeekBar, parser: (IntArray) -> Unit) {
        val arr = IntArray(8)
        rockerView.setRockerListener { x, y ->
            val speed = sb.progress
            when (angle(x, -y) - 22) {
                in 0 until 45 -> setArr(arr, speed, 0, 2, 4, 8)
                in 45 until 90 -> setArr(arr, speed, 0, 6)
                in 90 until 135 -> setArr(arr, speed, 0, 3, 5, 6)
                in 135 until 180 -> setArr(arr, speed, 3, 5)
                in 180 until 225 -> setArr(arr, speed, 1, 3, 5, 7)
                in 225 until 270 -> setArr(arr, speed, 1, 7)
                in 270 until 315 -> setArr(arr, speed, 1, 2, 4, 7)
                in 315 until 360 -> setArr(arr, speed, 2, 4)
            }
            parser(arr)
        }
    }

    fun setArr(arr: IntArray, speed: Int, vararg indexs: Int) {
        arr.forEachIndexed { index, _ ->
            if (indexs.contains(index)) {
                arr[index] = speed
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