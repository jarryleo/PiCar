package cn.leo.picar

import cn.leo.picar.cmd.Command
import cn.leo.picar.cmd.CommandType
import cn.leo.picar.msg.BaseMsg
import cn.leo.picar.msg.MsgType
import cn.leo.picar.utils.JsonUtil
import cn.leo.picar.view.RockerParser
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test1() {
        val msg = BaseMsg<Command>()
        msg.type = MsgType.TYPE_CAR
        msg.data = Command(CommandType.IDLE, 0)
        sendMsg(msg)
    }

    private fun sendMsg(msg: BaseMsg<*>) {
        val toJson = JsonUtil.toJson(msg)
        println(toJson)
    }

    @Test
    fun test2(){
        println(angle(0,100))
        println(angle(70,70))
        println(angle(100,0))
        println(angle(70,-70))
        println(angle(0,-100))
        println(angle(-70,-70))
        println(angle(-100,0))
        println(angle(-70,70))

        val arr = IntArray(8)
        RockerParser.setArr(arr,80,0,2,4,6)
        println(Arrays.toString(arr))
    }


    private fun angle(x: Int, y: Int) :Int{
        val len = sqrt((x * x + y * y).toDouble())
        var angle = acos(y / len)
        if (x < 0){
            angle = 2*Math.PI - angle
        }
        return (angle * 180 / Math.PI).toInt()
    }
}
