package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * This widget represents a rotary knob.
 * 
 * @author Karl Giesing
 */
public class RotaryKnob extends ImageView {
	private int max;
	private int min;
	private int progress;
	private float centerX;
	private float centerY;
	private float startAngle;
	private float sweepAngle;
	private float sweepRange;
	private OnRotaryKnobChangeListener listener;
	private Paint paint;
	private RectF oval;

	/**
	 * A callback that notifies clients when the progress level has been
	 * changed. This includes changes that were initiated by the user through a
	 * touch gesture as well as changes that were initiated programmatically.
	 * <p>
	 * It is essentially the same interface as SeekBar.OnSeekBarChangeListener,
	 * but with RotaryKnob objects.
	 * 
	 * @author Karl Giesing
	 * @see android.widget.SeekBar.OnSeekBarChangeListener
	 */
	public interface OnRotaryKnobChangeListener {
		/**
		 * Notification that the progress level has changed. Clients can use the
		 * fromUser parameter to distinguish user-initiated changes from those
		 * that occurred programmatically.
		 * 
		 * @param seekBar
		 *            The SeekBar whose progress has changed
		 * @param progress
		 *            The current progress level. This will be in the range
		 *            0..max where max was set by {@link RotaryKnob#setMax(int)}
		 *            . (The default progress for max is 100.)
		 * @param fromUser
		 *            True if the progress change was initiated by the user.
		 */
		void onProgressChanged(RotaryKnob knob, int progress, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStartTrackingTouch(RotaryKnob knob);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seek knob.
		 * 
		 * @param seekBar
		 *            The SeekBar in which the touch gesture began
		 */
		void onStopTrackingTouch(RotaryKnob knob);
	}

	/**
	 * @param context
	 *            Android context
	 */
	public RotaryKnob(Context context) {
		super(context);
		initRotaryKnob();
	}

	/**
	 * @param context
	 *            Android context
	 * @param attrs
	 *            Set of attributes associated with this widget's XML
	 */
	public RotaryKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRotaryKnob();
	}

	/**
	 * @param context
	 *            Android context
	 * @param attrs
	 *            Set of attributes associated with this widget's XML
	 * @param defStyle
	 *            Default style
	 */
	public RotaryKnob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initRotaryKnob();
	}

	public void setOnRotaryKnobChangeListener(
			OnRotaryKnobChangeListener listener) {
		this.listener = listener;
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

	/**
	 * Returns the knob's sweep range.
	 * 
	 * @return the knob's sweep range, in degrees.
	 */
	public synchronized float getSweepRange() {
		return sweepRange;
	}

	/**
	 * Sets the upper limit of this knob's range.
	 * 
	 * @param max
	 *            the upper limit of this knob's range.
	 */
	public synchronized void setMax(int max) {
		this.max = max;
		refreshProgress();
	}

	/**
	 * Sets the lower limit of this knob's range.
	 * 
	 * @param min
	 *            the lower limit of this knob's range.
	 */
	public synchronized void setMin(int min) {
		this.min = min;
		refreshProgress();
	}

	/**
	 * Sets the knob's current level of progress.
	 * 
	 * @param progress
	 *            the knob's current level of progress.
	 */
	public synchronized void setProgress(int progress) {
		this.progress = progress;
		refreshProgress();
		if (listener != null) {
			listener.onProgressChanged(this, this.progress, false);
		}
	}

	/**
	 * Sets the knob's sweep range. The sweep range is the knob's circular range
	 * of motion, in degrees. The range is centered around the knob's twelve
	 * o'clock position. The default is 270 degrees.
	 * 
	 * @param sweepRange
	 *            the knob's sweep range, in degrees.
	 */
	public synchronized void setSweepRange(float sweepRange) {
		// Make sure it's in the range of 0 - 360
		if (sweepRange > 360)
			sweepRange %= 360.0f;
		if (sweepRange < 0)
			sweepRange = 360 + sweepRange;
		this.sweepRange = sweepRange;
		refreshProgress();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Draw the arc
		if (paint != null)
			canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
		// Rotate the canvas (for image rotation)
		canvas.rotate(sweepAngle + startAngle + 90, centerX, centerY);
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w / 2.0f;
		centerY = h / 2.0f;
		final int stroke = (int) (paint.getStrokeWidth() / 2);
		final int left = getPaddingLeft() + stroke;
		final int top = getPaddingTop() + stroke;
		final int width = w - getPaddingRight() - stroke;
		final int height = h - getPaddingBottom() - stroke;
		oval.set(left, top, width, height);
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
	 * Initializes local variables.
	 */
	private void initRotaryKnob() {
		max = 100;
		min = 0;
		startAngle = 135;
		sweepAngle = 0;
		sweepRange = 270;
		oval = new RectF();
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
	}

	/**
	 * Refreshes the knob after the progress has changed. This could occur because
	 * of a change in any number of variables: max, min, progress, or sweepRange.
	 */
	private synchronized void refreshProgress() {
		// Make sure progress is in range
		if (progress < min)
			progress = min;
		if (progress > max)
			progress = max;
		// Re-calculate the start angle
		startAngle = 270 - (sweepRange / 2);
		// Set the sweep angle
		sweepAngle = sweepRange * (progress - min) / (float) (max - min);
		// Redraw
		invalidate();
	}

	/**
	 * @see android.widget.AbsSeekBar#trackTouchEvent
	 */
	private void trackTouchEvent(MotionEvent event) {
		double scale;
		// Calculate the sweepAngle
		final float dx = event.getX() - centerX;
		final float dy = event.getY() - centerY;
		float theta = (float) Math.toDegrees(Math.atan2(dy, dx));
		theta -= startAngle;
		// Ignore touch events in "dead spot" at base of knob
		if (theta < 0 && theta > sweepRange - 360) {
			return;
		}
		sweepAngle = (theta < 0 ? theta + 360.0f : theta);
		// Make sure sweepAngle is in bounds when calculating scale
		if (sweepAngle < 0) {
			sweepAngle = 0;
			scale = 0.0f;
		} else if (sweepAngle > sweepRange) {
			sweepAngle = sweepRange;
			scale = 1.0f;
		} else {
			scale = sweepAngle / sweepRange;
		}
		// Calculate and set progress
		progress = (int) (min + scale * (max - min));
		if (listener != null) {
			listener.onProgressChanged(this, progress, true);
		}
		invalidate();
	}

}
