package simizer.laws;

import java.util.Random;

/**
 * Gaussian - distributed integer generator. Values are set between 0 and
 * nbParams following a Gaussian distribution centered on nbParams / 2. The
 * standard deviation is set by using the setParam method, but defaults to the
 * mean divided by 2.
 *
 * @author isep
 */
public class GaussianLaw extends Law {

  private Random ran = new Random(System.currentTimeMillis());
  private int mean;
  private double sd;

  public GaussianLaw(int nbParams) {
    super(nbParams);

    mean = nbParams / 2;
    sd = mean / 2.0D;
  }

  @Override
  public int nextParam() {
    int p;
    do {
      p = (int) Math.round(ran.nextGaussian() * sd + mean);
    } while (p < 0 || p >= nbParams);
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
      System.out.println(gl.nextParam());
    }
  }
}
