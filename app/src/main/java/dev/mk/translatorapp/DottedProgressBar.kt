package dev.mk.translatorapp
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.*

class DottedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Configuration properties
    var dotCount = 5
        set(value) {
            field = value.coerceAtLeast(1)
            requestLayout()
        }

    var currentStep = 0
        set(value) {
            field = value.mod(dotCount + 1)
            invalidate()
        }

    var dotRadius = 8f.dpToPx()
        set(value) {
            field = value
            requestLayout()
        }

    var dotSpacing = 4f.dpToPx()
        set(value) {
            field = value
            requestLayout()
        }

    var activeColor = Color.BLUE
        set(value) {
            field = value
            invalidate()
        }

    var inactiveColor = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }

    // Auto-loop properties
    private var loopJob: Job? = null
    var loopInterval = 1000L
        set(value) {
            field = value.coerceAtLeast(100)
        }
    var isLooping = false
        private set

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DottedProgressBar,
            defStyleAttr,
            0
        ).apply {
            try {
                dotCount = getInt(R.styleable.DottedProgressBar_dotCount, 5)
                currentStep = getInt(R.styleable.DottedProgressBar_currentStep, 0)
                dotRadius = getDimension(R.styleable.DottedProgressBar_dotRadius, 8f.dpToPx())
                dotSpacing = getDimension(R.styleable.DottedProgressBar_dotSpacing, 4f.dpToPx())
                activeColor =  Color.BLUE
                inactiveColor = Color.GRAY
                loopInterval = getInt(R.styleable.DottedProgressBar_loopInterval, 1000).toLong()
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (dotCount * 2 * dotRadius + (dotCount - 1) * dotSpacing).toInt()
        val desiredHeight = (2 * dotRadius).toInt()

        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalDotsWidth = dotCount * 2 * dotRadius + (dotCount - 1) * dotSpacing
        val startX = (width - totalDotsWidth) / 2f
        val y = height / 2f

        for (i in 0 until dotCount) {
            val cx = startX + dotRadius + i * (2 * dotRadius + dotSpacing)
            paint.color = if (i < currentStep) activeColor else inactiveColor
            canvas.drawCircle(cx, y, dotRadius, paint)
        }
    }

    fun setProgress(step: Int) {
        currentStep = step.mod(dotCount + 1)
    }

    fun startAutoLoop() {
        if (isLooping) return

        isLooping = true
        loopJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                currentStep++
                delay(loopInterval)
            }
        }
    }

    fun stopAutoLoop() {
        loopJob?.cancel()
        isLooping = false
        currentStep = 0
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoLoop()
    }

    private fun Float.dpToPx(): Float = this * resources.displayMetrics.density
}