package fr.isep.simizer.laws;

import fr.isep.simizer.utils.StdRandom;

/**
 * A {@code Law} representing a Gaussian (normal) distribution.
 * <p>
 * The mean and the standard deviation can be specified upon creation.  If the
 * standard deviation is not specified, it defaults to half of the mean.
 * <p>
 * For an explanation of how out-of-range values are handled, see {@link
 * Law#nextValue()}.
 */
public class GaussianLaw extends Law {

  /** The mean of the distribution. */
  private final double mean;

  /** The standard deviation of the distribution. */
  private final double sd;

  /**
   * Initializes a new instance of the GaussianLaw class.
   *
   * @param mean the mean of the distribution
   */
  public GaussianLaw(double mean) {
    this(mean, mean / 2);
  }

  /**
   * Initializes a new instance of the GaussianLaw class.
   *
   * @param mean the mean of the distribution
   * @param standardDeviation the standard deviation of the distribution
   */
  public GaussianLaw(double mean, double standardDeviation) {
    super();

    this.mean = mean;
    this.sd = standardDeviation;
  }

  @Override
  protected int generateNextValue() {
    return (int) Math.round(StdRandom.gaussian(mean, sd));
  }

}
