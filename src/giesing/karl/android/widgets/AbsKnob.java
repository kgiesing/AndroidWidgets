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
 * This is the abstract base class for Android rotary knob widgets.
 * <p>
 * Knobs are square widgets, with an image that rotates around the center
 * depending upon the progress of the knob. They are similar in use to a SeekBar
 * (though they do not share the SeekBar's class heirarchy).
 * <p>
 * A knob's visual element will usually consist of two visual elements: an image
 * of a knob, and an arc that is drawn behind it representing the knob's
 * progress. This class handles the drawing of the image. Subclasses should
 * handle the painting of the arc onto the canvas.
 * <p>
 * For convenience, this class includes a Paint object used to draw the arc, and
 * a RectF object that represents the arc's boundary box. Subclasses should
 * override onDraw, and use Canvas.drawArc to draw an arc using these objects.
 * These objects are protected (rather than private) so subclasses can use them
 * directly, without method call overhead. This is done for performance reasons;
 * see <a href=
 * "http://developer.android.com/training/articles/perf-tips.html#GettersSetters"
 * >Avoid Internal Getters/Setters</a> on the Android developer site.
 * 
 * @author Karl Giesing
 */
public abstract class AbsKnob extends ImageView {
	/**
	 * A callback that notifies clients when the progress level has been
	 * changed. This includes changes that were initiated by the user through a
	 * touch gesture as well as changes that were initiated programmatically.
	 * <p>
	 * It is essentially the same interface as SeekBar.OnSeekBarChangeListener,
	 * but with AbsKnob objects.
	 * 
	 * @author Karl Giesing
	 * @see android.widget.SeekBar.OnSeekBarChangeListener
	 */
	public interface OnKnobChangeListener {
		/**
		 * Notification that the progress level has changed. Clients can use the
		 * fromUser parameter to distinguish user-initiated changes from those
		 * that occurred programmatically.
		 * 
		 * @param seekBar
		 *            The SeekBar whose progress has changed
		 * @param progress
		 *            The current progress level. This will be in the range
		 *            0..max where max was set by {@link AbsKnob#setMax(int)}
		 *            . (The default progress for max is 100.)
		 * @param fromUser
		 *            True if the progress change was initiated by the user.
		 */
		void onProgressChanged(AbsKnob knob, int progress, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStartTrackingTouch(AbsKnob knob);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStopTrackingTouch(AbsKnob knob);
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
	private float clockAngle;
	private OnKnobChangeListener listener;
	private int max;
	private int min;
	private int progress;

	/**
	 * @param context
	 *            Android context
	 */
	public AbsKnob(Context context) {
		super(context);
		initAbsKnob();
	}

	/**
	 * @param context
	 *            Android context
	 * @param attrs
	 *            Set of attributes associated with this widget's XML
	 */
	public AbsKnob(Context context, AttributeSet attrs) {
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
	public AbsKnob(Context context, AttributeSet attrs, int defStyleAttr) {
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
	public synchronized int getMax() {
		return max;
	}

	/**
	 * Returns the lower limit of this knob's range.
	 * 
	 * @return the lower limit of this knob's range.
	 */
	public synchronized int getMin() {
		return min;
	}

	/**
	 * Returns the knob's current level of progress.
	 * 
	 * @return the knob's current level of progress.
	 */
	public synchronized int getProgress() {
		return progress;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setPressed(true);
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
	 * Sets the upper limit of this knob's range.
	 * 
	 * @param max
	 *            the upper limit of this knob's range.
	 */
	public synchronized void setMax(int max) {
		this.max = max;
		if (progress > max) {
			onProgressRefresh(max, false);
		}
	}

	/**
	 * Sets the lower limit of this knob's range.
	 * 
	 * @param min
	 *            the lower limit of this knob's range.
	 */
	public synchronized void setMin(int min) {
		this.min = min;
		if (progress < min) {
			onProgressRefresh(min, false);
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
	 * Sets the knob's current level of progress.
	 * 
	 * @param progress
	 *            the knob's current level of progress.
	 */
	public synchronized void setProgress(int progress) {
		onProgressRefresh(progress, false);
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Rotate the canvas (for image rotation)
		canvas.rotate(clockAngle, center.x, center.y);
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	/**
	 * This method is invoked whenever the progress is changed, either
	 * programmatically or from a user interaction.
	 * 
	 * @param scale
	 *            the amount to scale the progress. 0.0f = minimum progress,
	 *            1.0f = maximum progress.
	 * @param fromUser
	 *            whether the change came from the user.
	 */
	protected void onProgressRefresh(float scale, boolean fromUser) {
		progress = (int) (scale * (max - min)) + min;
		clockAngle = toAngle(scale);
		if (listener != null) {
			listener.onProgressChanged(this, progress, fromUser);
		}
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
	 * Abstract method that is invoked whenever the system changes the progress
	 * level of the knob. Note that the parameter is a raw float representing
	 * the scale, not the actual progress value.
	 * <p>
	 * Subclasses should use this method to calculate and return the new angle
	 * of rotation from the progress scale. The angle must be measured
	 * clockwise, in degrees, starting at the 12 o'clock position.
	 * 
	 * @param scale
	 *            the progress scale. 0.0f = no progress, 1.0f = full progress.
	 * @return the new clockwise angle.
	 */
	protected abstract float toAngle(float scale);

	/**
	 * Abstract method that is invoked whenever the user changes the angle of
	 * rotation from a touch event. Subclasses should use this method to
	 * calculate and return the amount to scale the progress.
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
	 * @return the amount to scale the progress. 0.0f = minimum progress, 1.0f =
	 *         maximum progress.
	 */
	protected abstract float toProgressScale(float newAngle, float oldAngle);

	/**
	 * Initializes the AbsKnob object.
	 */
	private void initAbsKnob() {
		max = 100;
		min = 0;
		center = new PointF();
		arcBounds = new RectF();
		arcPaint = new Paint();
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setColor(Color.RED);
		arcPaint.setStrokeWidth(10);
		setImageResource(android.R.drawable.ic_lock_power_off);
		setColorFilter(Color.BLACK);
		onProgressRefresh(0, false);
	}

	/**
	 * Helper method that is invoked when tracking touch events.
	 * 
	 * @see android.widget.AbsSeekBar#trackTouchEvent
	 */
	private void trackTouchEvent(MotionEvent event) {
		// Save the old angle
		final float oldAngle = clockAngle;
		// Calculate the new angle
		final float dx = event.getX() - center.x;
		final float dy = event.getY() - center.y;
		float newAngle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90.0f;
		newAngle = (newAngle < 0 ? newAngle + 360.0f : newAngle);
		// Get progress from angles, and redraw
		onProgressRefresh(toProgressScale(newAngle, oldAngle), true);
		invalidate();
	}

}
