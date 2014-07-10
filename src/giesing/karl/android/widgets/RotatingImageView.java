/**
 * 
 */
package giesing.karl.android.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * An ImageView that can be rotated an arbitrary number of degrees.
 * <p>
 * This class contains fields which are protected, rather than private, so that
 * they can be accessed without the overhead of virtual method calls. This is
 * done for performance reasons. See <a href=
 * "http://developer.android.com/training/articles/perf-tips.html#GettersSetters"
 * >Avoid Internal Getters/Setters</a> on the Android developer website.
 * 
 * @author Karl Giesing
 * 
 */
public class RotatingImageView extends ImageView {
	/**
	 * The center coordinates of the View that contains the knob. The center is
	 * calculated automatically when the knob's measured size changes.
	 */
	protected PointF center;
	/**
	 * The current rotation.
	 */
	protected float rotation;
	private float init;
	private float theta;

	/**
	 * Simple constructor to use when creating a RotatingImageView from code.
	 * 
	 * @param context
	 *            The Context the RotatingImageView is running in, through which
	 *            it can access the current theme, resources, etc.
	 */
	public RotatingImageView(Context context) {
		super(context);
		initRotatingImageView();
	}

	/**
	 * Constructor that is called when inflating a RotatingImageView from XML.
	 * This is called when a view is being constructed from an XML file,
	 * supplying attributes that were specified in the XML file. This version
	 * uses a default style of 0, so the only attribute values applied are those
	 * in the Context's Theme and the given AttributeSet.
	 * <p>
	 * The method onFinishInflate() will be called after all children have been
	 * added.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 */
	public RotatingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRotatingImageView();
	}

	/**
	 * Perform inflation from XML and apply a class-specific base style. This
	 * constructor of View allows subclasses to use their own base style when
	 * they are inflating. For example, a Button class's constructor would call
	 * this version of the super class constructor and supply R.attr.buttonStyle
	 * for defStyle; this allows the theme's button style to modify all of the
	 * base view attributes (in particular its background) as well as the Button
	 * class's attributes.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 * @param defStyleAn
	 *            attribute in the current theme that contains a reference to a
	 *            style resource to apply to this view. If 0, no default style
	 *            will be applied.
	 */
	public RotatingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initRotatingImageView();
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
	 * Returns the current rotation, in degrees, clockwise from 12 o'clock.
	 * 
	 * @return the current rotation.
	 */
	public float getRotation() {
		return rotation;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startTrackingTouch(event);
			// fall through
		case MotionEvent.ACTION_MOVE:
			trackTouchEvent(event);
			break;
		case MotionEvent.ACTION_UP:
			trackTouchEvent(event);
			// fall through
		case MotionEvent.ACTION_CANCEL:
			stopTrackingTouch(event);
			break;
		}
		return true;
	}

	/**
	 * Helper method to calculate an angle from a MotionEvent.
	 * 
	 * @param event
	 *            MotionEvent used to calculate the angle
	 * @return the angle in degrees, clockwise from 12 o'clock
	 */
	public float toAngle(MotionEvent event) {
		final float dx = event.getX() - center.x;
		final float dy = event.getY() - center.y;
		final float angle = (float) Math.toDegrees(Math.atan2(dy, dx)) + 90.0f;
		return (angle < 0 ? angle + 360.0f : angle);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		canvas.rotate(rotation, center.x, center.y);
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// final int height = getMeasuredHeight();
		final int height = View.MeasureSpec.getSize(heightMeasureSpec);
		// final int width = getMeasuredWidth();
		final int width = View.MeasureSpec.getSize(widthMeasureSpec);
		final int dimension = height > width ? width : height;
		setMeasuredDimension(dimension, dimension);
	}

	/**
	 * This method is invoked whenever the rotation is changed due to user
	 * interaction.
	 * <p>
	 * The default implementation simply the change in rotation to the existing
	 * rotation, making sure it does not go beyond 360 degrees.
	 * 
	 * @param delta
	 *            The change in rotation, in degrees clockwise. May be negative.
	 */
	protected void onRotationChanged(float delta) {
		rotation = (rotation + delta) % 360.0f;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		center.set(w / 2.0f, h / 2.0f);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * This method is invoked when the touch event is started. The
	 * trackTouchEvent method is always invoked after this method.
	 * 
	 * @param event
	 *            the MotionEvent that initiates the touch events.
	 * @see android.view.MotionEvent#ACTION_DOWN
	 * @see RotatingImageView#trackTouchEvent(MotionEvent)
	 * @see RotatingImageView#stopTrackingTouch(MotionEvent)
	 */
	protected void startTrackingTouch(MotionEvent event) {
		init = toAngle(event);
	}

	/**
	 * This method is invoked when the touch event finishes. The trackTouchEvent
	 * method is always invoked before this method.
	 * 
	 * @param event
	 *            the MotionEvent that ends the touch events.
	 * @see android.view.MotionEvent#ACTION_UP
	 * @see android.view.MotionEvent#ACTION_CANCEL
	 * @see RotatingImageView#trackTouchEvent(MotionEvent)
	 * @see RotatingImageView#startTrackingTouch(MotionEvent)
	 */
	protected void stopTrackingTouch(MotionEvent event) {
		init = rotation;
	}

	/**
	 * This method is invoked on all touch events. If the event is the initial
	 * touch event, it is invoked after the startTrackingTouch method; if it is
	 * the final touch event, it is invoked before the stopTrackingTouch method.
	 * 
	 * @param event
	 *            the MotionEvent representing this touch event.
	 * @see RotatingImageView#startTrackingTouch(MotionEvent)
	 * @see RotatingImageView#stopTrackingTouch(MotionEvent)
	 */
	protected void trackTouchEvent(MotionEvent event) {
		theta = toAngle(event);
		onRotationChanged(theta - init);
		init = theta;
	}

	/**
	 * Initializes the RotatingImageView.
	 */
	private void initRotatingImageView() {
		center = new PointF();
		rotation = 0.0f;
	}

}
