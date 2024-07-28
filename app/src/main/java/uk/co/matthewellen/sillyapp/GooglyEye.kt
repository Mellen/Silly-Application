package uk.co.matthewellen.sillyapp

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.util.AttributeSet
import android.view.View
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * TODO: document your custom view class.
 */
class GooglyEye : View, SensorEventListener {

    private var _pupilXoffset: Float = 0f
    private var _pupilYoffset: Float = 0f

    private lateinit var pupilPaint: Paint
    private lateinit var scleraFillPaint: Paint
    private lateinit var scleraStrokePaint: Paint

    private var pupilXoffset: Float
        get() = _pupilXoffset
        set(value) {
            _pupilXoffset = value
        }

    private var pupilYoffset: Float
        get() = _pupilYoffset
        set(value) {
            _pupilYoffset = value
        }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    fun randomisePupil()
    {
        val paddingLeft = paddingStart
        val paddingTop = paddingTop
        val paddingRight = paddingEnd
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val pupilRadius = if(contentWidth < contentHeight)  contentWidth/4.0f else contentHeight/4.0f
        val scleraRadius = if(contentWidth < contentHeight)  contentWidth/2.0f else contentHeight/2.0f

        pupilXoffset = Random.nextFloat()*scleraRadius - pupilRadius
        pupilYoffset = Random.nextFloat()*scleraRadius - pupilRadius

        invalidate()
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.GooglyEye, defStyle, 0
        )

        a.recycle()

        pupilXoffset = 0f
        pupilYoffset = 0f

        pupilPaint = Paint()
        pupilPaint.style = Paint.Style.FILL
        pupilPaint.color = Color.BLACK

        scleraFillPaint = Paint()
        scleraFillPaint.style = Paint.Style.FILL
        scleraFillPaint.color = Color.WHITE

        scleraStrokePaint = Paint()
        scleraStrokePaint.style = Paint.Style.STROKE
        scleraStrokePaint.color = Color.BLACK
        scleraStrokePaint.strokeWidth = 3.0f

        val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val mSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        mSensor.also {
            sensor: Sensor? -> sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if((event?.values?.get(0)?.absoluteValue ?: 0.0f) > 2.0) {
            this.randomisePupil()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawEye(canvas)
    }

    fun drawEye(canvas: Canvas){
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingStart
        val paddingTop = paddingTop
        val paddingRight = paddingEnd
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val pupilRadius = if(contentWidth < contentHeight)  contentWidth/4.0f else contentHeight/4.0f
        val scleraRadius = if(contentWidth < contentHeight)  contentWidth/2.0f else contentHeight/2.0f

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        canvas.drawCircle(contentWidth/2.0f, contentHeight/2.0f, scleraRadius, scleraFillPaint)
        canvas.drawCircle(contentWidth/2.0f, contentHeight/2.0f, scleraRadius, scleraStrokePaint)
        canvas.drawCircle(contentWidth/2.0f+pupilXoffset, contentHeight/2.0f+pupilYoffset, pupilRadius, pupilPaint)

    }
}