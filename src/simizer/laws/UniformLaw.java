package simizer.laws;

import simizer.utils.StdRandom;

/**
 * A {@code Law} representing a uniform probability distribution.
 */
public class UniformLaw extends Law {

  public UniformLaw(int upperBound) {
    super(upperBound);
  }

  @Override
  public void setParam(double par) {

  }

  @Override
  public int nextValue() {
    return StdRandom.uniform(upperBound);
  }

  public static void main(String[] args) {
    UniformLaw gl = new UniformLaw(1000);
    for (int i = 0; i < 10000; i++) {
      System.out.println(gl.nextValue());
    }
  }
}
