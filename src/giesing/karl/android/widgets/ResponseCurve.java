package giesing.karl.android.widgets;

/**
 * A response curve accepts a linear input (the scale) and returns a value
 * determined by a (usually non-linear) response curve. The response curve
 * should be described by a continuous, invertible function.
 * <p>
 * This response curve differs from a general mathematical function in that both
 * the domain and range are bounded by the same interval - in this case, 0.0f to
 * 1.0f. It is the digital equivalent to the taper of an analog potentiometer.
 * <p>
 * Classes could implement this interface when creating widgets that have a
 * non-linear response to linear user interactions, like volume sliders or pan
 * knobs. It has similar uses to the TimeInterpolator interface, except that it
 * is not specific to the time domain.
 * 
 * @author Karl Giesing
 * @see android.animation.TimeInterpolator
 */
public interface ResponseCurve {

	/**
	 * Takes a linear scale value, and returns a value determined by response
	 * curve. The response curve should be described by an invertible
	 * mathematical function.
	 * 
	 * @param scale
	 *            The linear input scale value. Will be in the domain of 0.0f to
	 *            1.0f.
	 * @return The value determined by the response curve. Must be in the range
	 *         of 0.0f to 1.0f.
	 */
	public float toCurve(float scale);

	/**
	 * Takes a response curve value, and maps it to a linear scale value. The
	 * return value should be the inverse of the mathematical function that
	 * describes the response curve. In other words, this statement should
	 * always be true: <blockquote><code>toScale(toCurve(x)) == x</code>
	 * </blockquote>
	 * 
	 * @param curve
	 *            The response curve value. Will be in the domain of 0.0f to
	 *            1.0f.
	 * @return The value of the linear scale value that would result in the
	 *         response curve value. Must be in the range of 0.0f to 1.0f.
	 */
	public float toScale(float curve);

}
