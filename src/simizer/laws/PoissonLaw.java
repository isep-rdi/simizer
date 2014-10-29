package simizer.laws;

import java.util.Random;

/**
 *
 * @author isep
 */
public class PoissonLaw extends Law {

  private Random ran = new Random();
  private double alpha;

  public PoissonLaw(int nbParams) {
    this(nbParams, 1.0);
  }

  public PoissonLaw(int nbParams, double alpha) {
    super(nbParams);

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
        p = ran.nextDouble() * p;
      } while (p > l);
    } while (k >= nbParams);
    return k - 1;
  }
}
