package cn.leo.picar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Switch
import cn.leo.picar.cmd.Command
import cn.leo.picar.cmd.PwmCommand
import cn.leo.picar.msg.BaseMsg
import cn.leo.picar.msg.MsgType
import cn.leo.picar.udp.UdpFrame
import cn.leo.picar.udp.UdpListener
import cn.leo.picar.udp.UdpSender
import cn.leo.picar.utils.CoroutineUtil
import cn.leo.picar.utils.JsonUtil
import cn.leo.picar.view.RockerParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.findItem(R.id.app_bar_switch)?.actionView?.findViewById<Switch>(R.id.switchSetUltrasonic)
            ?.setOnCheckedChangeListener { _, isChecked -> setUltrasonic(isChecked) }
        return true
    }

    private fun setUltrasonic(start: Boolean) {
        val msg = BaseMsg<Boolean>()
        msg.type = MsgType.TYPE_SET_ULTRASONIC
        msg.data = start
        sendMsg(msg)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.shutdown -> {
                val msg = BaseMsg<Command>()
                msg.type = MsgType.TYPE_SHUTDOWN
                sendMsg(msg)
            }
            R.id.poweroff -> {
                val msg = BaseMsg<String>()
                msg.type = MsgType.TYPE_COMMAND
                msg.msg = "sudo poweroff"
                sendMsg(msg)
            }
            R.id.wheelTest -> startActivity(Intent(this, Main2Activity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        val msg = BaseMsg<PwmCommand>()
        msg.type = MsgType.TYPE_PWM_COMMAND
        RockerParser.parseRocker(rocker, sbSpeed) {
            msg.data = PwmCommand(it)
            sendMsg(msg)
        }
    }

    private fun sendMsg(msg: BaseMsg<*>) {
        val toJson = JsonUtil.toJson(msg)
        //println(toJson)
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

    var downX = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        var x = event?.x ?: 0f
        val w = window.decorView.width
        if (action == MotionEvent.ACTION_DOWN) {
            if (x < w/2) {
                return super.onTouchEvent(event)
            }
            downX = x
        }
        if(action == MotionEvent.ACTION_MOVE){
            if (x < w/2){
                x = w/2f
            }
            val len = downX - x
            val s = 1 - abs(len)/(w/2f)
            if (len > 0){
                RockerParser.turnLeft  = s
            }else{
                RockerParser.turnRight = s
            }
        }
        return true
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
