package cn.leo.picar.view

import android.widget.SeekBar
import kotlin.math.abs
import kotlin.math.tan

object RockerParser {
    const val pi = Math.PI
    fun parseRocker(rockerView: RockerView, sb: SeekBar, parser: (IntArray) -> Unit) {
        val arr = IntArray(8)
        rockerView.setRockerListener { x, y ->
            val speed = sb.progress
            arr[0] = 0
            arr[1] = 0
            arr[2] = 0
            arr[3] = 0
            arr[4] = 0
            arr[5] = 0
            arr[6] = 0
            arr[7] = 0
            if (y != 0) {
                println(" x = $x  y = $y")
                val arc = tan(x.toDouble() / -y.toDouble()) * 180 / pi
                println(arc)
            }
            parser(arr)
        }
    }

}