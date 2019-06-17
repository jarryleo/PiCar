package cn.leo.picar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import cn.leo.picar.R
import kotlin.math.min
import kotlin.math.sqrt

class RockerView : View {
    val backPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rockerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var radius = 0f
    var cx = 0f
    var cy = 0f
    var listener: (x: Int, y: Int) -> Unit = { _, _ -> }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        backPaint.color = resources.getColor(R.color.colorPrimary)
        rockerPaint.color = resources.getColor(R.color.colorPrimaryDark)
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        //绘制底圆
        canvas?.drawCircle(radius, radius, radius, backPaint)
        canvas?.drawCircle(cx, cy, radius / 2, rockerPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event?.action
        if (action == MotionEvent.ACTION_DOWN ||
            action == MotionEvent.ACTION_MOVE
        ) {
            cx = event.x
            cy = event.y

            val lx = cx - radius
            val ly = cy - radius
            val lr = radius / 2
            val cr = sqrt((lx * lx) + (ly * ly))
            if (cr > lr) {
                cx = radius + (lx * lr / cr)
                cy = radius + (ly * lr / cr)
            }

        } else if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_CANCEL
        ) {
            cx = radius
            cy = radius
        }
        val x = cx * 200 / width
        val y = cy * 200 / height
        listener(x.toInt(), y.toInt())
        invalidate()
        return true
    }


    private fun dp2px(dp: Int): Int {
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ) + 0.5).toInt()
    }
}