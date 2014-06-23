package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class RotaryKnob extends AbsSweepedKnob {
	private float sweepAngle;

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

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Draw the arc
		if (arcPaint != null) {
			canvas.drawArc(arcBounds, startAngle, sweepAngle, false, arcPaint);
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onProgressRefresh(float scale, boolean fromUser) {
		super.onProgressRefresh(scale, fromUser);
		sweepAngle = toAngle(scale) + startAngle;
	}

	@Override
	protected float toProgressScale(float newAngle, float oldAngle) {
		float angle = (startAngle + newAngle) % 360.0f;
		// If we're not in valid range of motion, use old value
		if (angle > sweepRange) {
			angle = (startAngle + oldAngle) % 360.0f;
		}
		return angle / sweepRange;
	}

	/**
	 * Initializes RotaryKnob object.
	 */
	private void initRotaryKnob() {
		setProgress(0);
	}
	
}
