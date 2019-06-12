package cn.leo.picar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Button
import cn.leo.pi.utils.CoroutineUtil
import cn.leo.picar.cmd.Command
import cn.leo.picar.cmd.CommandType
import cn.leo.picar.msg.BaseMsg
import cn.leo.picar.msg.MsgType
import cn.leo.picar.udp.UdpFrame
import cn.leo.picar.udp.UdpListener
import cn.leo.picar.udp.UdpSender
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : AppCompatActivity() {
    private val receiver: UdpListener = UdpFrame.getListener()
    private var sender: UdpSender? = null
    private var timeOut = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initEvent()
        checkConnect()
    }

    private fun initView() {
        val btns = arrayListOf<Button>(
            btnForward,
            btnBackward,
            btnLeft,
            btnRight,
            btnTurnLeft,
            btnTurnRight,
            btnBrake
        )
        btns.forEachIndexed { index, btn ->
            btn.setOnTouchListener { v, event ->
                val msg = BaseMsg<Command>()
                msg.type = MsgType.TYPE_CAR
                if (event.action == MotionEvent.ACTION_DOWN){
                    msg.data = Command(index+1,sbSpeed.progress)
                }else if (event.action == MotionEvent.ACTION_UP){
                    msg.data = Command(CommandType.IDLE,0)
                }
                sender?.send(Gson().toJson(msg).toByteArray(Charsets.UTF_8))
                true
            }
        }

    }

    private fun initEvent() {
        receiver.subscribe(25535) { _ , host ->
            if (sender == null) {
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
