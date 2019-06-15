package cn.leo.picar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import cn.leo.picar.cmd.Command
import cn.leo.picar.cmd.CommandType
import cn.leo.picar.cmd.PwmCommand
import cn.leo.picar.msg.BaseMsg
import cn.leo.picar.msg.MsgType
import cn.leo.picar.udp.UdpFrame
import cn.leo.picar.udp.UdpListener
import cn.leo.picar.udp.UdpSender
import cn.leo.picar.utils.CoroutineUtil
import cn.leo.picar.utils.JsonUtil
import cn.leo.picar.utils.getInt
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class Main2Activity : AppCompatActivity() {
    private val receiver: UdpListener = UdpFrame.getListener()
    private var sender: UdpSender? = null
    private var timeOut = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initEvent()
        initView()
        checkConnect()
    }

    private fun initView() {
        btnCommit.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_UP ||
                event.action == MotionEvent.ACTION_CANCEL
            ) {

                if (event.action == MotionEvent.ACTION_DOWN) {
                    val msg = BaseMsg<PwmCommand>()
                    msg.data = PwmCommand(intArrayOf(
                        etLF1.getInt(),
                        etLF2.getInt(),
                        etRF1.getInt(),
                        etRF2.getInt(),
                        etLB1.getInt(),
                        etLB2.getInt(),
                        etRB1.getInt(),
                        etRB2.getInt()
                    ))
                    sendMsg(msg)
                } else {
                    val msg = BaseMsg<Command>()
                    msg.type = MsgType.TYPE_CAR
                    msg.data = Command(CommandType.IDLE, 0)
                    sendMsg(msg)
                }
            }
            true
        }
    }

    private fun sendMsg(msg: BaseMsg<*>) {
        val toJson = JsonUtil.toJson(msg)
        println(toJson)
        CoroutineUtil.io {
            sender?.send(toJson.toByteArray(Charsets.UTF_8))
        }
    }

    private fun initEvent() {
        receiver.subscribe(25535) { _, host ->
            if (sender == null || timeOut > 10) {
                sender = UdpFrame.getSender(host, 25535)
            }
            timeOut = 0
        }
    }
    private fun checkConnect() {
        //超时检测协程
        CoroutineUtil.io {
            while (isActive) {
                if (sender != null) {
                    delay(1000)
                    timeOut++
                    if (timeOut > 10) {
                        status.setBackgroundResource(R.drawable.shape_status_red)
                    } else {
                        status.setBackgroundResource(R.drawable.shape_status_green)
                    }
                }
            }
        }
    }
}
