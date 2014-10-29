package simizer.laws;

import java.util.Random;

/**
 * A {@code Law} representing a uniform probability distribution.
 */
public class UniformLaw extends Law {

  private Random ran = new Random(System.currentTimeMillis());

  public UniformLaw(int nbparams) {
    super(nbparams);
  }

  @Override
  public void setParam(double par) {

  }

  @Override
  public int nextParam() {
    int result;
    do {
      result = (int) Math.round(nbParams * ran.nextDouble());
    } while (result < 0 || result >= nbParams);
    return result;
  }

  public static void main(String[] args) {
    UniformLaw gl = new UniformLaw(1000);
    for (int i = 0; i < 10000; i++) {
      System.out.println(gl.nextParam());
    }
  }
}
