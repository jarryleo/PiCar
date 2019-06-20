package cn.leo.picar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import cn.leo.picar.R
import cn.leo.picar.utils.CoroutineUtil
import kotlinx.coroutines.*
import kotlin.math.min
import kotlin.math.sqrt

class RockerView : View {
    private val backPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rockerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = 0f
    private var cx = 0f
    private var cy = 0f
    private var rockerListener: (x: Int, y: Int) -> Unit = { _, _ -> }
    private var finger = -1

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        backPaint.color = Color.argb(0x55, 0xff, 0xff, 0xff)
        rockerPaint.color = Color.argb(0x88, 0xff, 0xff, 0xff)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wm = widthMeasureSpec
        var hm = heightMeasureSpec
        val tw = MeasureSpec.getMode(wm)
        val th = MeasureSpec.getMode(hm)
        if (tw != MeasureSpec.EXACTLY) {
            wm = MeasureSpec.makeMeasureSpec(dp2px(100), MeasureSpec.EXACTLY)
        }
        if (th != MeasureSpec.EXACTLY) {
            hm = MeasureSpec.makeMeasureSpec(dp2px(100), MeasureSpec.EXACTLY)
        }
        val w = MeasureSpec.getSize(wm)
        val h = MeasureSpec.getSize(hm)
        if (w < h) {
            hm = wm
        } else {
            wm = hm
        }
        radius = (min(w, h) / 2 + 0.5).toFloat()
        cx = radius
        cy = radius
        super.onMeasure(wm, hm)
    }

    override fun onDraw(canvas: Canvas?) {
        //绘制底圆
        canvas?.drawCircle(radius, radius, radius, backPaint)
        //绘制摇杆
        canvas?.drawCircle(cx, cy, radius / 2, rockerPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (val action = event?.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (finger == -1 && action != MotionEvent.ACTION_MOVE) {
                    finger = event.getPointerId(event.actionIndex)
                }
                if (event.getPointerId(event.actionIndex) == finger) {
                    cx = event.getX(event.actionIndex)
                    cy = event.getY(event.actionIndex)

                    val lx = cx - radius
                    val ly = cy - radius
                    val lr = radius / 2
                    val cr = sqrt((lx * lx) + (ly * ly))
                    if (cr > lr) {
                        cx = radius + (lx * lr / cr)
                        cy = radius + (ly * lr / cr)
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_POINTER_UP -> {
                if (event.getPointerId(event.actionIndex) == finger) {
                    cx = radius
                    cy = radius
                    finger = -1
                }
            }
        }
        invalidate()
        return true
    }

    fun setRockerListener(listener: (x: Int, y: Int) -> Unit) {
        rockerListener = listener
    }


    private var rock = GlobalScope.launch(Dispatchers.IO) {
        while (isActive) {
            val x = ((cx * 200 / radius + 0.5f) - 200).toInt()
            val y = ((cy * 200 / radius + 0.5f) - 200).toInt()
            rockerListener(x, y)
            delay(100)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        rock.cancel()
    }

    private fun dp2px(dp: Int): Int {
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ) + 0.5).toInt()
    }
}