package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

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
public class Knob extends ImageView {
	/**
	 * The default knob sweep range.
	 */
	public static float SWEEP_RANGE_DEFAULT = 270;
	
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

	/**
	 * The bounds for drawing the background arc.
	 */
	protected RectF arcBounds;
	/**
	 * The Paint object used for drawing the background arc.
	 */
	protected Paint arcPaint;
	private PointF center;
	private float rotation;
	private OnKnobChangeListener listener;
	private float max;
	private float min;
	private float level;
	private float range; // To avoid computations on min/max update
	private float startAngle;
	private float sweepRange;
	private float sweepAngle;
	private float touchStartAngle;
	private float touchStartRotation;

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
	 * Returns the center coordinates of the View that contains the knob. The
	 * center is calculated automatically when the knob's measured size changes.
	 * 
	 * @return the center coordinates of the View that contains the knob.
	 */
	public synchronized PointF getCenter() {
		return center;
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
	 * Returns the knob's current level of level.
	 * 
	 * @return the knob's current level of level.
	 */
	public synchronized float getLevel() {
		return level;
	}
	
	/**
	 * Returns the knob's sweep range. The sweep range is the knob's circular
	 * range of motion, in degrees. The range is centered around the knob's
	 * twelve o'clock position. The default sweep range is 240 degrees.
	 * 
	 * @return the knob's sweep range, in degrees.
	 */
	public synchronized float getSweepRange() {
		return sweepRange;
	}

	/**
	 * Sets the knob's sweep range. The sweep range is the knob's circular range
	 * of motion, in degrees. The range is centered around the knob's twelve
	 * o'clock position. The default sweep range is in SWEEP_RANGE_DEFAULT.
	 * 
	 * @param sweepRange
	 *            the knob's sweep range, in degrees.
	 * @see Knob#SWEEP_RANGE_DEFAULT
	 */
	public synchronized void setSweepRange(float sweepRange) {
		// TODO Handle this!
		if (sweepRange > 360)
			sweepRange %= 360.0f;
		if (sweepRange < 0)
			sweepRange = 360 + sweepRange;
		this.sweepRange = sweepRange;
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
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchStartRotation = rotation;
			touchStartAngle = toAngle(event);
			if (listener != null) {
				listener.onStartTrackingTouch(this);
			}
			// fall through
		case MotionEvent.ACTION_MOVE:
			trackTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			trackTouchEvent(event);
			// fall through
		case MotionEvent.ACTION_CANCEL:
			touchStartRotation = rotation;
			touchStartAngle = 0.0f;
			setPressed(false);
			if (listener != null) {
				listener.onStopTrackingTouch(this);
			}
			break;
		}
		return true;
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
	 * Sets the upper limit of this knob's level range.
	 * 
	 * @param max
	 *            the upper limit of this knob's level range.
	 */
	public synchronized void setMax(int max) {
		this.max = max;
		range = max - min;
		if (level > max) {
			onScaleRefresh(max, false);
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
			onScaleRefresh(min, false);
		}
	}

	/**
	 * Sets the OnKnobChangeListener for this knob.
	 * 
	 * @param the
	 *            OnKnobChangeListener for this knob.
	 */
	public void setOnKnobChangeListener(OnKnobChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the knob's current level of level.
	 * 
	 * @param level
	 *            the knob's current level of level.
	 */
	public synchronized void setProgress(int progress) {
		float rawScale = (progress - min) / range;
		onScaleRefresh(rawScale, false);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Draw the arc
		if (arcPaint != null) {
			canvas.drawArc(arcBounds, startAngle + 270, sweepAngle, false, arcPaint);
		}
		// Rotate the canvas (for image rotation)
		canvas.rotate(rotation, center.x, center.y);
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	/**
	 * This method is invoked whenever the raw scale is changed, either
	 * programmatically or from a user interaction. The scale passed to the
	 * method will be a linear scale.
	 * 
	 * @param scale
	 *            the raw scale.
	 * @param fromUser
	 *            whether the change came from the user.
	 */
	protected void onScaleRefresh(float scale, boolean fromUser) {
		rotation = toRotation(scale);
		level = toLevel(scale);
		sweepAngle = toSweepAngle(rotation, startAngle);
		if (listener != null) {
			listener.onLevelChanged(this, level, fromUser);
		}
	}

	/**
	 * Calculates the sweep angle of the arc drawn behind the image.
	 * 
	 * @param rotation
	 *            The angle of rotation, determined by the scale.
	 * @param startAngle
	 *            The start angle.
	 * @return Sweep angle of arc.
	 */
	protected float toSweepAngle(float rotation, float startAngle) {
		return (360 + rotation - startAngle) % 360;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		center.set(w / 2.0f, h / 2.0f);
		final int stroke = (int) (arcPaint.getStrokeWidth() / 2);
		final int left = getPaddingLeft() + stroke;
		final int top = getPaddingTop() + stroke;
		final int width = w - getPaddingRight() - stroke;
		final int height = h - getPaddingBottom() - stroke;
		arcBounds.set(left, top, width, height);
	}

	/**
	 * Abstract method that is invoked whenever the system changes the raw scale
	 * of the knob.
	 * <p>
	 * Subclasses should use this method to calculate and return the new angle
	 * of rotation. The angle must be measured clockwise, in degrees, starting
	 * at the 12 o'clock position.
	 * 
	 * @param scale
	 *            the scale. 0.0f = no level, 1.0f = full level.
	 * @return the new angle, in degrees clockwise from 12 o'clock.
	 */
	protected float toRotation(float scale)  {
		return (scale * sweepRange + startAngle) % 360.0f;
	}

	/**
	 * Abstract method that is invoked whenever the user changes the angle of
	 * rotation from a touch event. Subclasses should use this method to
	 * calculate and return the raw scale.
	 * <p>
	 * For knobs that rotate continuously, the old angle is provided to
	 * determine direction. You may also use this value to determine the scale
	 * if the new angle is out of the range of motion.
	 * <p>
	 * The angles that are passed to this method are absolute (not relative to
	 * the start angle). They are measured clockwise starting from 12 o'clock,
	 * in degrees.
	 * 
	 * @param newAngle
	 *            the new angle, measured clockwise from 12 o'clock.
	 * @param oldAngle
	 *            the old angle, measured clockwise from 12 o'clock.
	 * @return the raw scale. 0.0f = minimum, 1.0f = maximum.
	 */
	protected float toScale(float newAngle, float oldAngle) {
		float sweep = (360 + newAngle - startAngle) % 360.0f;
		// If we're not in valid range of motion, use old value
		if (sweep > sweepRange) {
			sweep = (360 + oldAngle - startAngle) % 360.0f;
		}
		return sweep / sweepRange;
	}

	/**
	 * Convert a raw scale to the level.
	 * <p>
	 * The default implementation calculates the level from the scale using a
	 * linear relationship. Subclasses may override this method to implement
	 * knobs with a nonlinear response curve.
	 * 
	 * @param scale
	 *            the raw scale of the knob.
	 * @return the level.
	 */
	protected float toLevel(float scale) {
		return scale * range + min;
	}

	/**
	 * Initializes the Knob object.
	 */
	private void initAbsKnob() {
		// Initialize max, range, level
		max = 100.0f;
		min = 0.0f;
		range = 100.0f;
		onScaleRefresh(0, false);
		// Initialize arc variables
		center = new PointF();
		arcBounds = new RectF();
		arcPaint = new Paint();
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setColor(Color.RED);
		arcPaint.setStrokeWidth(10);
		setImageResource(android.R.drawable.ic_lock_power_off);
		setColorFilter(Color.BLACK);
	}

	/**
	 * Helper method to calculate an angle from a MotionEvent.
	 * 
	 * @param event
	 *            MotionEvent used to calculate the angle
	 * @return the angle, in degrees clocwise from 12 o'clock
	 */
	private float toAngle(MotionEvent event) {
		final float dx = event.getX() - center.x;
		final float dy = event.getY() - center.y;
		final float angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90.0f;
		return (angle < 0 ? angle + 360.0f : angle);
	}

	/**
	 * Helper method that is invoked when tracking touch events.
	 * 
	 * @see android.widget.AbsSeekBar#trackTouchEvent
	 */
	private void trackTouchEvent(MotionEvent event) {
		// Save the old angle
		final float oldAngle = rotation;
		// Get the new angle
		float newAngle = toAngle(event);
		// The new angle should be offset from the angle on touch start
		newAngle = touchStartRotation + newAngle - touchStartAngle;
		// Get raw scale from angles, and redraw
		float scale = toScale(newAngle, oldAngle);
		onScaleRefresh(scale, true);
		invalidate();
	}

}
