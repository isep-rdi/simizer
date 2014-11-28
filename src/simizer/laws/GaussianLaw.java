package simizer.laws;

import simizer.utils.StdRandom;

/**
 * A {@code Law} representing a Gaussian (normal) distribution.
 * <p>
 * Values are generated in the range {@code [0, upperBound)}.  The mean of the
 * distribution is {@code upperBound / 2}, and the standard deviation defaults
 * to half of the mean ({@code mean / 2}).  The standard deviation can be
 * customized by using the {@link #setParam(double)} method.
 * <p>
 * For an explanation of how out-of-range values are handled, see {@link
 * #nextValue()}.
 */
public class GaussianLaw extends Law {

  /** The mean of the distribution. */
  private final int mean;

  /** The standard deviation of the distribution. */
  private double sd;

  /**
   * Initializes a new instance of the GaussianLaw class.
   * <p>
   * Configures the mean and standard deviation with their default values.
   *
   * @param upperBound the open upper bound of the distribution
   */
  public GaussianLaw(int upperBound) {
    super(upperBound);

    mean = upperBound / 2;
    sd = mean / 2.0D;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Because a Gaussian distribution can return any real number, this law must
   * handle values outside of the valid range ({@code [0, upperBound)}).  If a
   * generated value is outside of this range, new values will be generated
   * until one is within the acceptable range.
   * 
   * @return a value in the range {@code [0, upperBound)} for this distribution
   */
  @Override
  public int nextValue() {
    int p;
    do {
      p = (int) Math.round(StdRandom.gaussian(mean, sd));
    } while (p < 0 || p >= upperBound);
    return p;
  }

  /**
   * Changes the standard deviation of this distribution.
   * <p>
   * Update the value of the standard deviation.  The new value will be used in
   * all future calls to {@link #nextValue()}.
   * 
   * @param parameter the new standard deviation
   */
  @Override
  public void setParam(double parameter) {
    sd = parameter;
  }
}
