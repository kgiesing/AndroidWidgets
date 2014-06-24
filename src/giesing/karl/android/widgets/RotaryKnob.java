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
			canvas.drawArc(arcBounds, startAngle + 270, sweepAngle, false, arcPaint);
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onProgressRefresh(float scale, boolean fromUser) {
		super.onProgressRefresh(scale, fromUser);
		sweepAngle = (360 + toAngle(scale) - startAngle) % 360;
	}

	@Override
	protected float toProgressScale(float newAngle, float oldAngle) {
		float sweep = (360 + newAngle - startAngle) % 360.0f;
		// If we're not in valid range of motion, use old value
		if (sweep > sweepRange) {
			sweep = (360 + oldAngle - startAngle) % 360.0f;
		}
		return sweep / sweepRange;
	}

	/**
	 * Initializes RotaryKnob object.
	 */
	private void initRotaryKnob() {
		setProgress(0);
	}
	
}
