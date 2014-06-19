package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * This class creates a vertically-oriented seek bar (a slider). It is based on
 * code from Andro Selva, and modified to better conform to the native Android
 * seek bar.
 * 
 * @see <a href="https://github.com/AndroSelva/Vertical-SeekBar-Android">
 *      Vertical-SeekBar-Android</a> on GitHub
 */
public class VerticalSeekBar extends SeekBar {
	
	/**
	 * We cannot use the listener in the SeekBar class, because both the object
	 * itself and the methods that call it (e.g. onStopTrackingTouch) are
	 * unaccessible.
	 */
	private SeekBar.OnSeekBarChangeListener listener;

	/**
	 * @param context Android application context
	 */
	public VerticalSeekBar(Context context) {
		super(context);
	}

	/**
	 * @param context Android context
	 * @param attrs Set of attributes associated with this widget's XML
	 */
	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context Android context
	 * @param attrs Set of attributes associated with this widget's XML
	 * @param defStyle Default style
	 */
	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// Rotate the canvas
		canvas.rotate(-90.0f);
		// Move (translate) the canvas so that it is seen in View
		canvas.translate(0.0f - getHeight(), 0.0f);
		super.onDraw(canvas);
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		// Swap measured width and height
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// Swap width and height, both old and current
		super.onSizeChanged(h, w, oldh, oldw);
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
	
	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * @see android.widget.AbsSeekBar#trackTouchEvent
	 */
	private void trackTouchEvent(MotionEvent event) {
		float scale;
		int progress;
		// Avoid repeat method dispatch
		final int max = getMax();
		final float y = event.getY();
		final int paddingBottom = getPaddingBottom();
		final int paddingTop = getPaddingTop();
		final int height = getHeight();
		final float available = height - paddingTop - paddingBottom;
		// Make sure y-pos. is in bounds when calculating scale
        if (y < paddingBottom) {
            scale = 0.0f;
        } else if (y > height - paddingTop) {
            scale = 1.0f;
        } else {
            scale = (y - paddingBottom) / available;
        }
		// Calculate and set progress
		progress = max - (int) (scale * max);
		setProgress(progress);
		if (listener != null) {
			listener.onProgressChanged(this, progress, true);
		}
		// AbsSeekBar.onSizeChanged updates the thumb position
		super.onSizeChanged(height, getWidth(), 0, 0);
	}
}
