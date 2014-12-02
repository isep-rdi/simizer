package fr.isep.simizer.laws;

import fr.isep.simizer.utils.StdRandom;

/**
 * A {@code Law} representing a Poisson distribution.
 * <p>
 * For an explanation of how out-of-range values are handled, see {@link
 * Law#nextValue()}.
 */
public class PoissonLaw extends Law {

  /** The alpha value for this Poisson distribution. */
  private double alpha;

  /**
   * Initializes a PoissonLaw instance with an {@code alpha} value of {@code
   * 1.0}.
   */
  public PoissonLaw() {
    this(1.0);
  }

  /**
   * Initializes a PoissonLaw with a custom {@code alpha} value.
   *
   * @param alpha the alpha value used to describe the distribution
   */
  public PoissonLaw(double alpha) {
    super();

    this.alpha = alpha;
  }

  @Override
  public int generateNextValue() {
    double l = Math.exp(-alpha);
    double p = 1.0;

    int k = 0;
    do {
      k++;
      p = StdRandom.uniform() * p;
    } while (p > l);
    return k - 1;
  }
}
