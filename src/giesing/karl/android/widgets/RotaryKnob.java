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
	protected void onScaleRefresh(float scale, boolean fromUser) {
		super.onScaleRefresh(scale, fromUser);
		sweepAngle = (360 + toRotation(scale) - startAngle) % 360;
	}

	/**
	 * Initializes RotaryKnob object.
	 */
	private void initRotaryKnob() {
		setProgress(0);
	}
	
}
