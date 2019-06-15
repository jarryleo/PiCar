package cn.leo.picar

import cn.leo.picar.cmd.Command
import cn.leo.picar.cmd.CommandType
import cn.leo.picar.msg.BaseMsg
import cn.leo.picar.msg.MsgType
import cn.leo.picar.utils.JsonUtil
import org.junit.Test

import org.junit.Assert.*

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
    fun test1(){
        val msg = BaseMsg<Command>()
        msg.type = MsgType.TYPE_CAR
        msg.data = Command(CommandType.IDLE, 0)
        sendMsg(msg)
    }

    private fun sendMsg(msg: BaseMsg<*>) {
        val toJson = JsonUtil.toJson(msg)
        println(toJson)
    }
}
