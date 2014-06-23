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
	 * The start angle. Unlike other variables, this is measured clockwise from
	 * 3 o'clock, for compatibility with the Android coordinate system.
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
		setSweepRange(270);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AbsSweepedKnob(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSweepRange(270);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public AbsSweepedKnob(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setSweepRange(270);
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
	
	@Override
	protected float toAngle(float scale) {
		return scale * sweepRange - startAngle;
	}

}
