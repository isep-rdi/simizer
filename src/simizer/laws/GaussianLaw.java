package simizer.laws;

import simizer.utils.StdRandom;

/**
 * Gaussian - distributed integer generator. Values are set between 0 and
 * nbParams following a Gaussian distribution centered on nbParams / 2. The
 * standard deviation is set by using the setParam method, but defaults to the
 * mean divided by 2.
 *
 * @author isep
 */
public class GaussianLaw extends Law {

  private int mean;
  private double sd;

  public GaussianLaw(int upperBound) {
    super(upperBound);

    mean = upperBound / 2;
    sd = mean / 2.0D;
  }

  @Override
  public int nextValue() {
    int p;
    do {
      p = (int) Math.round(StdRandom.gaussian(mean, sd));
    } while (p < 0 || p >= upperBound);
    return p;
  }

  @Override
  public void setParam(double par) {
    sd = par;
  }

  public static void main(String[] args) {
    GaussianLaw gl = new GaussianLaw(30);
    gl.setParam(7.5);
    for (int i = 0; i < 10000; i++) {
      System.out.println(gl.nextValue());
    }
  }
}
