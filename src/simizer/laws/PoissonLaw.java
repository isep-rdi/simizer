package simizer.laws;

import simizer.utils.StdRandom;

/**
 * A {@code Law} representing a Poisson distribution.
 * <p>
 * Values are generated in the range {@code [0, upperBound)}.  The {@code alpha}
 * parameter defaults to a value of {@code 1.0}, but this value can be adjusted
 * by using the {@link #PoissonLaw(int, double)} constructor or the {@link
 * #setParam(double)} method.
 * <p>
 * For an explanation of how out-of-range values are handled, see {@link
 * #nextValue()}.
 */
public class PoissonLaw extends Law {

  /** The alpha value for this Poisson distribution. */
  private double alpha;

  /**
   * Initializes a PoissonLaw instance with an {@code alpha} value of {@code
   * 1.0}.
   *
   * @param upperBound an open upper bound on the generated values
   */
  public PoissonLaw(int upperBound) {
    this(upperBound, 1.0);
  }

  /**
   * Initializes a PoissonLaw with a custom {@code alpha} value.
   *
   * @param upperBound an open upper bound on the generated values
   * @param alpha the alpha value used to describe the distribution
   */
  public PoissonLaw(int upperBound, double alpha) {
    super(upperBound);

    this.alpha = alpha;
  }

  /**
   * Sets the {@code alpha} value for the distribution.
   * <p>
   * The new value will be used in all future calls to {@link #nextValue()}.
   *
   * @param parameter the new alpha value
   */
  @Override
  public void setParam(double parameter) {
    this.alpha = parameter;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Because a Poisson distribution can return any real number, this law must
   * handle values outside of the valid range ({@code [0, upperBound)}).  If a
   * generated value is outside of this range, new values will be generated
   * until one is within the acceptable range.
   *
   * @return a value in the range {@code [0, upperBound)} for this distribution
   */
  @Override
  public int nextValue() {
    double l = Math.exp(-alpha);
    double p = 1.0;

    int k;
    do {
      k = 0;
      do {
        k++;
        p = StdRandom.uniform() * p;
      } while (p > l);
    } while (k >= upperBound);
    return k - 1;
  }
}
