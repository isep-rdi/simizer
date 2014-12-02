package fr.isep.simizer.laws;

import fr.isep.simizer.utils.StdRandom;

/**
 * A {@code Law} representing a uniform probability distribution.
 * <p>
 * This class will sample a uniform distribution, returning values in the range
 * [0, bound) with equal probability.
 */
public class UniformLaw extends Law {

  private final int bound;

  /**
   * Initializes a new {@code UniformLaw} instance.
   *
   * @param bound an open upper bound on the values returned by this law
   */
  public UniformLaw(int bound) {
    super();

    this.bound = bound;
  }

  @Override
  protected int generateNextValue() {
    return StdRandom.uniform(bound);
  }
}
