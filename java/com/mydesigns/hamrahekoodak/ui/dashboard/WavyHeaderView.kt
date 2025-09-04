package com.mydesigns.hamrahekoodak.ui.dashboard

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mydesigns.hamrahekoodak.R

class WavyHeaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // --- Paints ---
    private val startColor = ContextCompat.getColor(context, R.color.delight_header_start)
    private val endColor = ContextCompat.getColor(context, R.color.delight_header_end)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val wavePaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = (255 * 0.35).toInt() // شفافیت ۳۵٪
    }
    private val wavePaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = (255 * 0.25).toInt() // شفافیت ۲۵٪
    }
    private val wavePaint3 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = (255 * 0.15).toInt() // شفافیت ۱۵٪
    }

    // --- Paths ---
    private val wavePath1 = Path()
    private val wavePath2 = Path()
    private val wavePath3 = Path()

    // --- Animation ---
    private var waveAnimator: ValueAnimator? = null
    private var masterPhaseShift = 0f

    // --- شروع تغییرات ---
    // این ضریب‌ها سرعت نسبی هر موج را تعیین می‌کنند.
    // برای جلوگیری از پرش انیمیشن، این مقادیر باید اعداد صحیح (Integer) باشند.
    // این تضمین می‌کند که هر موج در پایان دوره انیمیشن، دقیقاً یک یا چند دور کامل را طی کرده
    // و به همان موقعیتی برمی‌گردد که از آن شروع کرده است.
    private val waveSpeedMultiplier1 = 1f
    private val waveSpeedMultiplier2 = 2f
    private val waveSpeedMultiplier3 = 3f
    // --- پایان تغییرات ---

    init {
        setWillNotDraw(false)

        waveAnimator = ValueAnimator.ofFloat(0f, (2f * Math.PI).toFloat()).apply {
            duration = 10000L // مدت زمان یک دور کامل انیمیشن (۱۰ ثانیه)
            repeatCount = ValueAnimator.INFINITE // برای تکرار بی‌نهایت
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                masterPhaseShift = animation.animatedValue as Float
                invalidate()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        backgroundPaint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            startColor, endColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        createWavePaths()
        canvas.drawPath(wavePath3, wavePaint3)
        canvas.drawPath(wavePath2, wavePaint2)
        canvas.drawPath(wavePath1, wavePaint1)

        super.onDraw(canvas)
    }

    private fun createWavePaths() {
        val viewHeight = height.toFloat()
        val viewWidth = width.toFloat()

        // موج اول
        wavePath1.reset()
        wavePath1.moveTo(0f, viewHeight)
        val amplitude1 = viewHeight * 0.20f
        val frequency1 = 0.6f
        val phase1 = masterPhaseShift * waveSpeedMultiplier1
        for (x in 0..width) {
            val y = viewHeight * 0.8f + amplitude1 * kotlin.math.sin(((x / viewWidth) * 2 * Math.PI * frequency1) - phase1).toFloat()
            wavePath1.lineTo(x.toFloat(), y)
        }
        wavePath1.lineTo(viewWidth, viewHeight)
        wavePath1.close()

        // موج دوم
        wavePath2.reset()
        wavePath2.moveTo(0f, viewHeight)
        val amplitude2 = viewHeight * 0.15f
        val frequency2 = 0.7f
        val phase2 = masterPhaseShift * waveSpeedMultiplier2
        for (x in 0..width) {
            val y = viewHeight * 0.85f + amplitude2 * kotlin.math.sin(((x / viewWidth) * 2 * Math.PI * frequency2) - phase2).toFloat()
            wavePath2.lineTo(x.toFloat(), y)
        }
        wavePath2.lineTo(viewWidth, viewHeight)
        wavePath2.close()

        // موج سوم
        wavePath3.reset()
        wavePath3.moveTo(0f, viewHeight)
        val amplitude3 = viewHeight * 0.10f
        val frequency3 = 0.5f
        val phase3 = masterPhaseShift * waveSpeedMultiplier3
        for (x in 0..width) {
            val y = viewHeight * 0.9f + amplitude3 * kotlin.math.sin(((x / viewWidth) * 2 * Math.PI * frequency3) - phase3).toFloat()
            wavePath3.lineTo(x.toFloat(), y)
        }
        wavePath3.lineTo(viewWidth, viewHeight)
        wavePath3.close()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        waveAnimator?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
    }
}

