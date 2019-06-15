package cn.leo.picar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cn.leo.picar.R

class RockerView: View {
    val backPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rockerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        backPaint.color = resources.getColor(R.color.colorPrimary)
        rockerPaint.color = resources.getColor(R.color.colorPrimaryDark)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

    }
    override fun onDraw(canvas: Canvas?) {

    }
}