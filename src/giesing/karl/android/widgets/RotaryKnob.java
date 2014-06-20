package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Karl Giesing
 *
 */
public class RotaryKnob extends View {
	private int maxValue;
	private int minValue;
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
		 *            0..max where max was set by
		 *            {@link RotaryKnob#setMax(int)}. (The default progress for
		 *            max is 100.)
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
	 * @param context Android context
	 */
	public RotaryKnob(Context context) {
		super(context);
		initialize();
	}

	/**
	 * @param context Android context
	 * @param attrs Set of attributes associated with this widget's XML
	 */
	public RotaryKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * @param context Android context
	 * @param attrs Set of attributes associated with this widget's XML
	 * @param defStyle Default style
	 */
	public RotaryKnob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}

	public void setOnRotaryKnobChangeListener(OnRotaryKnobChangeListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		paint.setColor(Color.RED);
		paint.setStrokeWidth(10);
		canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(2);
		canvas.drawRect(0, 0, centerX * 2, centerY * 2, paint);
		canvas.drawLine(0, 0, centerX * 2, centerY * 2, paint);
		canvas.drawLine(0, centerY * 2, centerX * 2, 0, paint);
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
		// TODO Method stub
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w / 2.0f;
		centerY = h / 2.0f;
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int width = left + w - getPaddingRight();
		int height = top + h - getPaddingBottom();
		oval.set(left, top, width, height);
		// oval.set(0, 0, w, h);
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
	private void initialize() {
		oval = new RectF();
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		maxValue = 100;
		minValue = 0;
		startAngle = 135;
		sweepAngle = 0;
		sweepRange = 270;
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
		// DEBUG
		Log.i("RotaryKnob.trackTouchEvent", "theta: " + theta);
		
		theta -= startAngle;
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
		progress = (int) (minValue + scale * (maxValue - minValue));
		if (listener != null) {
			listener.onProgressChanged(this, progress, true);
		}
		// DEBUG
		Log.d("RotaryKnob.trackTouchEvent", "theta-startAngle: " + theta);
		Log.d("RotaryKnob.trackTouchEvent", "sweepAngle: " + sweepAngle);
		invalidate();
	}

}
