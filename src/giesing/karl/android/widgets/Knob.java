package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * This is the base class for Android rotary knob widgets.
 * <p>
 * Knobs are square widgets, with an image that rotates around the center
 * depending upon the level of the knob. They are similar in use to a SeekBar
 * (though they do not share the SeekBar's class heirarchy).
 * <p>
 * A knob's visual element will usually consist of two visual elements: an image
 * of a knob, and an arc that is drawn behind it representing the knob's level.
 * 
 * @author Karl Giesing
 */
public class Knob extends RotatingImageView {
	/**
	 * A callback that notifies clients when the level has been changed. This
	 * includes changes that were initiated by the user through a touch gesture
	 * as well as changes that were initiated programmatically.
	 * <p>
	 * It is essentially the same interface as SeekBar.OnSeekBarChangeListener,
	 * but with Knob objects.
	 * 
	 * @author Karl Giesing
	 * @see android.widget.SeekBar.OnSeekBarChangeListener
	 */
	public interface OnKnobChangeListener {
		/**
		 * Notification that the level has changed. Clients can use the fromUser
		 * parameter to distinguish user-initiated changes from those that
		 * occurred programmatically.
		 * 
		 * @param seekBar
		 *            The SeekBar whose level has changed
		 * @param level
		 *            The current level. This will be in the range 0..max where
		 *            max was set by {@link Knob#setMax(int)} . (The default
		 *            level for max is 100.)
		 * @param fromUser
		 *            True if the level change was initiated by the user.
		 */
		void onLevelChanged(Knob knob, float level, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStartTrackingTouch(Knob knob);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStopTrackingTouch(Knob knob);
	}
	
	/** The default knob sweep range. */
	public static float ROTATION_RANGE_DEFAULT = 240;
	private RectF arcBounds;
	private Paint arcPaint;
	private OnKnobChangeListener knobChangeListener;
	private ResponseCurve responseCurve;
	private float level;
	private float max;
	private float min;
	private float range;
	private float rotationRange;
	private float rotationOffset;
	private float startAngle;
	private float sweepAngle;
	private float sweepRange;

	/**
	 * @param context
	 *            Android context
	 */
	public Knob(Context context) {
		super(context);
		initAbsKnob();
	}

	/**
	 * @param context
	 *            Android context
	 * @param attrs
	 *            Set of attributes associated with this widget's XML
	 */
	public Knob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAbsKnob();
	}
	
	/**
	 * @param context
	 *            Android context
	 * @param attrs
	 *            Set of attributes associated with this widget's XML
	 * @param defStyle
	 *            Default style
	 */
	public Knob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAbsKnob();
	}

	/**
	 * Returns the background arc's boundary rectangle. The boundary rectangle
	 * is the rectangle inside the padding, and is calculated automatically when
	 * the knob's measured size changes.
	 * 
	 * @return the background arc's boundary rectangle.
	 */
	public synchronized RectF getArcBounds() {
		return arcBounds;
	}

	/**
	 * Returns the Paint object used to draw the background arc. This object may
	 * be null.
	 * 
	 * @return the Paint object used to draw the background arc.
	 */
	public Paint getArcPaint() {
		return arcPaint;
	}

	/**
	 * Returns the knob's current level.
	 * 
	 * @return the knob's current level.
	 */
	public synchronized float getLevel() {
		return level;
	}

	/**
	 * Returns the upper limit of this knob's range.
	 * 
	 * @return the upper limit of this knob's range
	 */
	public synchronized float getMax() {
		return max;
	}
	
	/**
	 * Returns the lower limit of this knob's range.
	 * 
	 * @return the lower limit of this knob's range.
	 */
	public synchronized float getMin() {
		return min;
	}
	
	/**
	 * Returns the ResponseCurve object for this Knob.
	 * @return the ResponseCurve object for this Knob.
	 * @see ResponseCurve
	 */
	public ResponseCurve getResponseCurve() {
		return responseCurve;
	}

	/**
	 * Returns the knob's total range of rotation. This is the knob's circular
	 * range of motion, in degrees clockwise. The default is 240 degrees.
	 * 
	 * @return the knob's range of rotation, in degrees clockwise.
	 */
	public synchronized float getRotationRange() {
		return rotationRange;
	}
	
	@Override
	public void reset() {
		rotation = startAngle;
		onScaleRefresh(0.0f, false);
	}

	/**
	 * Sets the Paint object used to draw the background arc. If you do not want
	 * any background arc to be drawn at all, pass null to this method.
	 * 
	 * @param arcPaint
	 *            the Paint object used to draw the background arc.
	 */
	public void setArcPaint(Paint arcPaint) {
		this.arcPaint = arcPaint;
	}

	/**
	 * Sets the knob's current level.
	 * 
	 * @param level
	 *            the knob's current level.
	 */
	public synchronized void setLevel(float level) {
		this.level = level;
		onScaleRefresh(toScale(level), false);
	}

	/**
	 * Sets the upper limit of this knob's level range.
	 * 
	 * @param max
	 *            the upper limit of this knob's level range.
	 */
	public synchronized void setMax(int max) {
		this.max = max;
		range = max - min;
		if (level > max) {
			onScaleRefresh(1.0f, false);
		}
	}

	/**
	 * Sets the lower limit of this knob's level range.
	 * 
	 * @param min
	 *            the lower limit of this knob's level range.
	 */
	public synchronized void setMin(int min) {
		this.min = min;
		range = max - min;
		if (level < min) {
			onScaleRefresh(0.0f, false);
		}
	}

	/**
	 * Sets the OnKnobChangeListener for this knob.
	 * 
	 * @param the
	 *            OnKnobChangeListener for this knob.
	 */
	public void setOnKnobChangeListener(OnKnobChangeListener listener) {
		knobChangeListener = listener;
	}

	/**
	 * Sets the ResponseCurve interface for this Knob. This allows the widget to
	 * mimic the behavior if different types of knob tapers (e.g. linear or
	 * audio).
	 * <p>
	 * If set to null, the level will be set directly by the angle of rotation
	 * (the equivalent of ResponseCurve.LINEAR).
	 * 
	 * @param responseCurve
	 *            the ResponseCurve object for this Knob.
	 * @see ResponseCurves#LINEAR
	 */
	public void setResponseCurve(ResponseCurve responseCurve) {
		this.responseCurve = responseCurve;
	}

	/**
	 * Sets the knob's total range of rotation. This is the knob's circular
	 * range of motion, in degrees clockwise. The default is 240 degrees.
	 * <p>
	 * The range of rotation and start angle are set separately. If you want the
	 * knob's range of rotation to be centered around the 12 o'clock position,
	 * then you should also set the start angle.
	 * <p>
	 * Setting the range of rotation to a value greater than 360.0f will result
	 * in more than one turn of the knob to reach its max value.
	 * 
	 * @param rotationRange
	 *            the knob's range of rotation, in degrees clockwise.
	 * @see Knob#ROTATION_RANGE_DEFAULT
	 * @see Knob#setStartAngle(float)
	 */
	public synchronized void setRotationRange(float rotationRange) {
		this.rotationRange = rotationRange;
		sweepRange = (rotationRange > 360.0f ? 360.0f : rotationRange);
	}

	/**
	 * Sets the start angle. The angle is measured clockwise, in degrees,
	 * starting from the 12 o'clock position.
	 * 
	 * @param startAngle
	 *            the start angle.
	 */
	public synchronized void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {		
		// Draw the arc
		if (arcPaint != null) {
			canvas.drawArc(arcBounds, startAngle + 270, sweepAngle, false,
					arcPaint);
		}
		super.onDraw(canvas);
	}
	
	@Override
	protected void onRotationChanged(float delta) {
		if (rotationOffset + delta > rotationRange) {
			delta = rotationRange - rotationOffset;
		}
		if (rotationOffset + delta < 0.0f) {
			delta = -rotationOffset;
		}
		super.onRotationChanged(delta);
		rotationOffset += delta;
		onScaleRefresh(rotationOffset / rotationRange, true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Calculate the arc bounds
		int stroke = 0;
		if (arcPaint != null) {
			stroke = (int) (arcPaint.getStrokeWidth());
		}
		final int left = getPaddingLeft() + stroke;
		final int top = getPaddingTop() + stroke;
		final int width = w - getPaddingRight() - stroke;
		final int height = h - getPaddingBottom() - stroke;
		arcBounds.set(left, top, width, height);
	}
	
	/**
	 * Initializes the Knob object.
	 */
	private void initAbsKnob() {
		// Initialize data variables
		max = 100.0f;
		min = 0.0f;
		range = max - min;
		
		// Initialize angular variables
		setRotationRange(ROTATION_RANGE_DEFAULT);
		startAngle = 360 - sweepRange / 2.0f;
		rotation = startAngle;
		rotationOffset = 0.0f;
		
		// Initialize UI
		center = new PointF();
		arcBounds = new RectF();
		arcPaint = new Paint();
		arcPaint.setStyle(Paint.Style.STROKE);
		// TODO Get color from theme
		arcPaint.setColor(Color.BLUE);
		arcPaint.setStrokeWidth(10);
		setImageResource(android.R.drawable.ic_lock_power_off);
		// TODO Get color from theme
		setColorFilter(Color.BLACK);
		
		// Initialize scale-related variables
		onScaleRefresh(0.0f, false);
	}

	/**
	 * This method is invoked whenever the raw scale is changed, either
	 * programmatically or from a user interaction.
	 * <p>
	 * The scale passed to the method will be a linear scalar value.
	 * 
	 * @param scale
	 *            the raw scale.
	 * @param fromUser
	 *            whether the change came from the user.
	 */
	private void onScaleRefresh(float scale, boolean fromUser) {
		sweepAngle = scale * sweepRange;
		level = toLevel(scale);
		if (knobChangeListener != null) {
			knobChangeListener.onLevelChanged(this, level, fromUser);
		}
	}

	/**
	 * Convert a raw scale to the level. This should be the inverse function of
	 * code>toScale</code>.
	 * 
	 * @param scale
	 *            the raw scale of the knob.
	 * @return the level.
	 * @see Knob#toScale(float)
	 */
	private float toLevel(float scale) {
		if (responseCurve != null) {
			scale = responseCurve.toCurve(scale);
		}
		return scale * range + min;
	}

	/**
	 * Convert a level to the raw scale. This should be the inverse function of
	 * <code>toLevel</code>.
	 * 
	 * @param scale
	 *            the raw scale of the knob.
	 * @return the level.
	 * @see Knob#toLevel(float)
	 */
	private float toScale(float level) {
		float curve = (level - min) / range;
		if (responseCurve != null) {
			return responseCurve.toScale(curve);
		}
		return curve;
	}

}
