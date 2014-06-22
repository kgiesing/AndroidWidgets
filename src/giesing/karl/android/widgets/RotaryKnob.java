package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class RotaryKnob extends AbsKnob {
	private float sweepAngle;
	private float sweepRange;
	private float startAngle;

	public RotaryKnob(Context context) {
		super(context);
		initRotaryKnob();
	}

	public RotaryKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRotaryKnob();
	}

	public RotaryKnob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initRotaryKnob();
	}

	/**
	 * Initializes RotaryKnob object.
	 */
	private void initRotaryKnob() {
		setSweepRange(270);
		setProgress(0);
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Draw the arc
		if (getArcPaint() != null) {
			canvas.drawArc(getArcBounds(), startAngle, sweepAngle, true,
					getArcPaint());
		}
		super.onDraw(canvas);
	}

	@Override
	float toClockAngle(float scale) {
		return scale * sweepRange - startAngle;
	}

	@Override
	float toProgressScale(float newAngle, float oldAngle) {
		float angle = (startAngle + newAngle) % 360.0f;
		// If we're not in valid range of motion, use old value
		if (angle > sweepRange) {
			angle = (startAngle + oldAngle) % 360.0f;
		}
		return angle / sweepRange;
	}
	
	@Override
	protected void updateProgress(float scale, boolean fromUser) {
		super.updateProgress(scale, fromUser);
		sweepAngle = toClockAngle(scale) + startAngle;
	}

	/**
	 * Returns the knob's sweep range. The sweep range is the knob's circular
	 * range of motion, in degrees. The range is centered around the knob's
	 * twelve o'clock position. The default sweep range is 270 degrees.
	 * 
	 * @return the knob's sweep range, in degrees.
	 */
	public synchronized float getSweepRange() {
		return sweepRange;
	}

	/**
	 * Sets the knob's sweep range. The sweep range is the knob's circular range
	 * of motion, in degrees. The range is centered around the knob's twelve
	 * o'clock position. The default sweep range is 270 degrees.
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
		startAngle = 270 - (sweepRange / 2);
	}
	
}
