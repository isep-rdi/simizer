package simizer.laws;

import simizer.utils.StdRandom;

/**
 *
 * @author isep
 */
public class PoissonLaw extends Law {

  private double alpha;

  public PoissonLaw(int upperBound) {
    this(upperBound, 1.0);
  }

  public PoissonLaw(int upperBound, double alpha) {
    super(upperBound);

    this.alpha = alpha;
  }

  @Override
  public void setParam(double par) {
    this.alpha = par;
  }

  @Override
  public int nextParam() {
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
