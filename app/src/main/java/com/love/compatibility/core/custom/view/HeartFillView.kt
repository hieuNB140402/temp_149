package com.love.compatibility.core.custom.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class HeartFillView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // --- paints & paths ---
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 6f
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val heartPath = Path()
    private val wavePath = Path()

    // --- animation state ---
    private var wavePhase = 0f            // 0..1, dùng để offset sóng ngang
    private var currentPercent = 0f       // 0..100, mức độ dâng nước hiện tại
    private var waveAnimator: ValueAnimator? = null
    private var levelAnimator: ValueAnimator? = null

    // --- customizable properties ---
    var strokeColor: Int = Color.RED
        set(value) { field = value; strokePaint.color = value; invalidate() }
    var strokeWidthPx: Float = 6f
        set(value) { field = value; strokePaint.strokeWidth = value; invalidate() }
    var waterColor: Int = Color.parseColor("#FF6384")
        set(value) { field = value; fillPaint.color = value; invalidate() }

    // --- Wave Constants (Đã điều chỉnh) ---
    private val WAVE_AMPLITUDE_FACTOR = 0.03f // Biên độ sóng (tỉ lệ với chiều cao view)
    private val WAVE_FREQUENCY = 3f           // Số chu kỳ sóng hiển thị trên view
    // 2 * PI * WAVE_FREQUENCY là tổng radian dịch chuyển cho 1 chu kỳ lặp
    private val PERIOD_IN_RADIANS = (2 * Math.PI * WAVE_FREQUENCY).toFloat()


    init {
        fillPaint.color = waterColor
        strokePaint.strokeWidth = strokeWidthPx
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startWave()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
        levelAnimator?.cancel()
    }

    fun setPercent(target: Float, animate: Boolean = true, duration: Long = 5000L) {
        val t = target.coerceIn(0f, 100f)
        levelAnimator?.cancel()
        if (!animate) {
            currentPercent = t
            invalidate()
            return
        }
        levelAnimator = ValueAnimator.ofFloat(currentPercent, t).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                currentPercent = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun startWave() {
        waveAnimator?.cancel()
        waveAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000L
            repeatCount = ValueAnimator.INFINITE
            // Giữ LinearInterpolator để sóng dịch chuyển đều
            interpolator = LinearInterpolator()
            addUpdateListener {
                wavePhase = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createHeartPath(w, h)
    }

    private fun createHeartPath(w: Int, h: Int, topCurveFactor: Float = 0.1f) {
        // ... (Giữ nguyên hàm vẽ trái tim)
        val width = w.toFloat()
        val height = h.toFloat()

        heartPath.reset()

        val mX = width / 2
        val mY = height / 3
        val topCurveY = -height * topCurveFactor

        heartPath.moveTo(mX, height)
        heartPath.cubicTo(
            -width / 6, height * 2 / 3,
            width / 6, topCurveY,
            mX, mY
        )
        heartPath.cubicTo(
            width - width / 6, topCurveY,
            width + width / 6, height * 2 / 3,
            mX, height
        )
        heartPath.close()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val save = canvas.save()

        // clip to heart
        canvas.clipPath(heartPath)

        // draw wave inside clipped region
        drawWave(canvas)

        canvas.restoreToCount(save)

        // draw stroke outline after restore so it's visible
        canvas.drawPath(heartPath, strokePaint)
    }

    private fun drawWave(canvas: Canvas) {
        wavePath.reset()

        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return

        // --- Tính toán Mức nước (Level) ---
        val paddingTop = h * 0.05f
        val paddingBottom = h * 0.05f
        val usableHeight = h - paddingTop - paddingBottom
        // levelY: Tọa độ Y của đường giữa sóng (baseline).
        // currentPercent=100 -> levelY nhỏ (gần đỉnh), 0 -> levelY lớn (gần đáy)
        val levelY = paddingTop + (1f - currentPercent / 100f) * usableHeight

        // --- Tính toán Sóng (Wave) ---
        val amplitude = h * WAVE_AMPLITUDE_FACTOR
        // phaseShift: Dịch chuyển ngang. Khi wavePhase = 1, phaseShift = PERIOD_IN_RADIANS (2*PI*3)
        // -> Sóng dịch chuyển chính xác 3 chu kỳ.
        val phaseShift = wavePhase * PERIOD_IN_RADIANS

        // radiansPerPixel: Số radian tương ứng với 1 pixel.
        // 2*PI*WAVE_FREQUENCY là tổng radian cho chiều rộng w.
        val radiansPerPixel = PERIOD_IN_RADIANS / w

        // Bắt đầu vẽ
        wavePath.moveTo(0f, levelY)

        for (x in 0..w.toInt() step 1) {
            val xFloat = x.toFloat()

            // Tính toán góc (radian) tại tọa độ x
            // xFloat * radiansPerPixel là góc tĩnh, phaseShift là góc động theo thời gian
            val angle = xFloat * radiansPerPixel + phaseShift

            // Tính toán tọa độ Y (levelY là baseline)
            val y = levelY + amplitude * sin(angle)

            wavePath.lineTo(xFloat, y)
        }

        // close the path to bottom (đóng kín path để tô màu)
        wavePath.lineTo(w, h)
        wavePath.lineTo(0f, h)
        wavePath.close()

        // --- Shader (Giữ nguyên) ---
        val shader = LinearGradient(
            0f, levelY - amplitude, 0f, h,
            lightenColor(waterColor, 0.1f),
            waterColor,
            Shader.TileMode.CLAMP
        )
        fillPaint.shader = shader

        canvas.drawPath(wavePath, fillPaint)

        fillPaint.shader = null
    }

    // small helper to slightly lighten color (Giữ nguyên)
    private fun lightenColor(color: Int, fraction: Float): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val newR = (r + (255 - r) * fraction).toInt().coerceIn(0,255)
        val newG = (g + (255 - g) * fraction).toInt().coerceIn(0,255)
        val newB = (b + (255 - b) * fraction).toInt().coerceIn(0,255)
        return Color.argb(Color.alpha(color), newR, newG, newB)
    }

    // Thêm property để lưu trạng thái
    private var isLevelPaused = false
    private var currentML = 0f

    // Hàm tạm dừng
    fun pauseLevelAnimation() {
        isLevelPaused = true
        levelAnimator?.pause()
    }

    // Hàm tiếp tục
    fun resumeLevelAnimation() {
        isLevelPaused = false
        levelAnimator?.resume()
    }
    private val maxML = 3000f

    fun setPercentWithML(
        tIncrementML: Float,  // ML muốn thêm vào
        animate: Boolean = true,
        duration: Long = 5000L,
        onFull: (() -> Unit)? = null
    ) {
        if (currentML >= maxML) {
            onFull?.invoke()
            return
        }

        val targetML = (currentML + tIncrementML).coerceAtMost(maxML)
        val targetPercent = targetML / maxML * 100f

        levelAnimator?.cancel()

        if (!animate || isLevelPaused) {
            currentPercent = targetPercent
            currentML = targetML
            invalidate()
            if (currentML >= maxML) onFull?.invoke()
            return
        }

        levelAnimator = ValueAnimator.ofFloat(currentPercent, targetPercent).apply {
            this.duration = duration
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                if (!isLevelPaused) {
                    currentPercent = it.animatedValue as Float
                    currentML = currentPercent / 100f * maxML
                    Log.d("nbhieu", "currentML: ${currentML}")
                    invalidate()
                    if (currentML >= maxML - 400) {
                        onFull?.invoke()
                        pauseLevelAnimation()
                    }
                }
            }
            start()
        }
    }
    // Reset mức nước về 0
    fun resetLevel() {
        // Dừng animation nếu đang chạy
        levelAnimator?.cancel()
        isLevelPaused = false

        // Đặt giá trị về 0
        currentML = 0f
        currentPercent = 0f

        // Vẽ lại view
        invalidate()
    }

}