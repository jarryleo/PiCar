package cn.leo.picar

import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
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
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), View.OnTouchListener, IVLCVout.OnNewVideoLayoutListener {


    private val receiver: UdpListener = UdpFrame.getListener()
    private var sender: UdpSender? = null
    private var timeOut = 100
    private var ip = ""
    private var lastJson = ""

    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMediaPlayer()
        initView()
        initEvent()
        checkConnect()
    }

    private fun initMediaPlayer() {
        mLibVLC = LibVLC(this, arrayListOf<String>("-vvv"))
        mMediaPlayer = MediaPlayer(mLibVLC)
        video.holder.setFormat(PixelFormat.TRANSLUCENT)
    }

    private fun playVideo() {
        if (ip.isEmpty()) {
            return
        }
        stopVideo()
        val url = "http://$ip:8085/?action=stream"
        val vlcVout = mMediaPlayer?.vlcVout
        vlcVout?.setVideoView(video)
        vlcVout?.setSubtitlesView(video)
        vlcVout?.attachViews(this)
        val media = Media(mLibVLC, Uri.parse(url))
        mMediaPlayer?.media = media
        media.release()
        mMediaPlayer?.play()

        mMediaPlayer?.aspectRatio = "16:9"
        mMediaPlayer?.scale = 0f
        mMediaPlayer?.vlcVout?.setWindowSize(window.decorView.width, window.decorView.height)
    }


    override fun onStart() {
        super.onStart()
        playVideo()
    }

    override fun onStop() {
        super.onStop()
        stopVideo()
    }

    private fun stopVideo() {
        mMediaPlayer?.stop()
        mMediaPlayer?.vlcVout?.detachViews()
    }

    override fun onNewVideoLayout(
        vlcVout: IVLCVout?,
        width: Int,
        height: Int,
        visibleWidth: Int,
        visibleHeight: Int,
        sarNum: Int,
        sarDen: Int
    ) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
        mLibVLC?.release()
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
            R.id.reboot -> {
                val msg = BaseMsg<String>()
                msg.type = MsgType.TYPE_COMMAND
                msg.msg = "sudo reboot"
                sendMsg(msg)
            }
            R.id.camera -> {
                val msg = BaseMsg<String>()
                msg.type = MsgType.TYPE_COMMAND
                msg.msg = "./camera.sh"
                sendMsg(msg)
                playVideo()
                CoroutineUtil.io {
                    delay(5000)
                    if (mMediaPlayer?.isPlaying != true) {
                        playVideo()
                    }
                }
            }

            R.id.wheelTest -> startActivity(Intent(this, Main2Activity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        toolBar.title = "遥控器"
        toolBar.background.alpha = 0x55
        setSupportActionBar(toolBar)
        //实现透明状态栏效果  并且toolbar 需要设置  android:fitsSystemWindows="true"
        window.attributes.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or window.attributes.flags

        val msg = BaseMsg<PwmCommand>()
        msg.type = MsgType.TYPE_PWM_COMMAND
        RockerParser.parseRocker(rocker, sbSpeed) {
            msg.data = PwmCommand(it)
            sendMsg(msg)
        }
        val btn = arrayOf(btnUp, btnDown, btnLeft, btnRight)
        btn.forEach {
            it.setOnTouchListener(this)
        }
    }

    private var gear:Int by Delegates.vetoable(20){
        _, _, newValue ->
        if (newValue > 24 || newValue < 10){
            return@vetoable false
        }
        true
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action

        when (v) {
            btnUp -> {
                if (action == MotionEvent.ACTION_DOWN) {
                    gear += 1
                    val msg = BaseMsg<Int>()
                    msg.type = MsgType.TYPE_SETTING_GEAR
                    msg.data = gear
                    sendMsg(msg)
                }
            }
            btnDown -> {
                if (action == MotionEvent.ACTION_DOWN) {
                    gear -= 1
                    val msg = BaseMsg<Int>()
                    msg.type = MsgType.TYPE_SETTING_GEAR
                    msg.data = gear
                    sendMsg(msg)
                }
            }
            btnLeft -> {
                if (action == MotionEvent.ACTION_DOWN) {
                    RockerParser.turnLeft = 0.5f
                } else if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL
                ) {
                    RockerParser.turnLeft = 1f
                }
            }
            btnRight -> {
                if (action == MotionEvent.ACTION_DOWN) {
                    RockerParser.turnRight = 0.5f
                } else if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL
                ) {
                    RockerParser.turnRight = 1f
                }
            }
        }
        return false
    }

    private fun sendMsg(msg: BaseMsg<*>) {
        val toJson = JsonUtil.toJson(msg)
        if (lastJson == toJson) {
            return
        }
        println(toJson)
        lastJson = toJson
        CoroutineUtil.io {
            sender?.send(toJson.toByteArray(Charsets.UTF_8))
        }
    }


    private fun initEvent() {
        receiver.subscribe(25535) { _, host ->
            if (sender == null || timeOut > 10) {
                ip = host
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
