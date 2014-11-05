package simizer.laws;

import simizer.utils.StdRandom;

/**
 * A {@code Law} representing a uniform probability distribution.
 * <p>
 * This class will sample a uniform distribution, returning values in the range
 * [0, upperBound) with equal probability.
 */
public class UniformLaw extends Law {

  /**
   * Initializes a new {@code UniformLaw} instance.
   *
   * @param upperBound an open upper bound on the values returned by this law
   */
  public UniformLaw(int upperBound) {
    super(upperBound);
  }

  /**
   * Samples this uniform distribution, returning the next value.
   *
   * @return a uniformly-distributed value in the range [0, upperBound)
   */
  @Override
  public int nextValue() {
    return StdRandom.uniform(upperBound);
  }
}
