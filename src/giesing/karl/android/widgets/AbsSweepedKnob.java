/**
 * 
 */
package giesing.karl.android.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * This class handles knobs with sweep. Sweep is the total degrees of motion it
 * has, centered at 12 o'clock. (This is the equivalent of the total mechanical
 * travel in a potentiometer.) Both rotary knobs and center-tapped knobs are of
 * this type; continuously rotating knobs are not.
 * <p>
 * For convenience, this class includes fields representing the sweep range and
 * the start angle.. These objects are protected (rather than private) so
 * subclasses can use them directly, without method call overhead. This is done
 * for performance reasons; see <a href=
 * "http://developer.android.com/training/articles/perf-tips.html#GettersSetters"
 * >Avoid Internal Getters/Setters</a> on the Android developer site.
 * 
 * @author Karl Giesing
 * 
 */
public abstract class AbsSweepedKnob extends AbsKnob {
	/**
	 * The default knob sweep range.
	 */
	public static float SWEEP_RANGE_DEFAULT = 270;
	
	/**
	 * The start angle. It is automatically calculated when the sweep range is
	 * set.
	 */
	protected float startAngle;
	/**
	 * The sweep range.
	 */
	protected float sweepRange;

	/**
	 * @param context
	 */
	public AbsSweepedKnob(Context context) {
		super(context);
		setSweepRange(SWEEP_RANGE_DEFAULT);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbsSweepedKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSweepRange(SWEEP_RANGE_DEFAULT);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public AbsSweepedKnob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setSweepRange(SWEEP_RANGE_DEFAULT);
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
	 * @see AbsSweepedKnob#SWEEP_RANGE_DEFAULT
	 */
	public synchronized void setSweepRange(float sweepRange) {
		// Make sure it's in the range of 0 - 360
		if (sweepRange > 360)
			sweepRange %= 360.0f;
		if (sweepRange < 0)
			sweepRange = 360 + sweepRange;
		this.sweepRange = sweepRange;
		startAngle = 360 - (sweepRange / 2);
	}
	
	@Override
	protected float toAngle(float scale) {
		return (scale * sweepRange + startAngle) % 360.0f;
	}

}
