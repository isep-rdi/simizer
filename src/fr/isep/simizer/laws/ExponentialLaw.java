package fr.isep.simizer.laws;

import fr.isep.simizer.utils.StdRandom;

/**
 * A {@code Law} representing an exponential distribution.
 * <p>
 * This a standard exponential distribution, and the distribution is defined by
 * its mean value.
 */
public class ExponentialLaw extends Law {

  /** The parameter that defines the exponential distribution. */
  private double lambda = 1.0;

  /**
   * Initializes an {@code ExponentialLaw} with the specified mean.
   *
   * @param mean the mean of the distribution.  This is the only parameter
   *            necessary to define an exponential distribution.
   */
  public ExponentialLaw(double mean) {
    super();

    this.lambda = 1 / mean;
  }

  @Override
  protected int generateNextValue() {
    return (int) Math.floor(StdRandom.exp(lambda));
  }

}
