package simizer.laws;

import simizer.utils.StdRandom;

/**
 *
 * @author isep
 */
public class ExponentialLaw extends Law {

  protected double lambda = 1.0;
  protected double dNbParam;
  protected double sum;

  public ExponentialLaw(int upperBound) {
    super(upperBound);

    this.dNbParam = (double) upperBound;
  }

  public ExponentialLaw(int upperBound, double alpha) {
    this(upperBound);

    setParam(alpha);
  }

  @Override
  public void setParam(double par) {
    lambda = 1 / par;
    sum = getSum(upperBound, lambda);
  }

  @Override
  public int nextParam() {
    double tmpVal;
    do {
      tmpVal = StdRandom.uniform();
    } while (tmpVal >= sum);
    double cumul = 0.0;
    int rank = -1;
    do {
      cumul += expDist(++rank, lambda);
    } while (cumul < tmpVal && rank < upperBound);

    return rank;
  }

  /**
   * Rank based generation
   *
   * @return exponentially distributed integer between [0,nbParam]
   */
  public int nextParam2() {
    double tmpVal = StdRandom.uniform();
    double cumul = 0.0;
    int rank = -1;
    do {
      cumul += expDist(++rank, lambda) / sum;
    } while (cumul < tmpVal && rank < upperBound);

    return rank;
  }

  public static double expDist(int x, double lambda) {
    return (lambda * Math.exp(-lambda * x));
  }

  public static double[] generateDist(int nbPar, double lambda) {
    double[] result = new double[nbPar];
    double sum = 0.0;
    for (int i = 0; i < nbPar; i++) {
      result[i] = expDist(i, lambda);
      sum += result[i];
    }
    for (int i = 0; i < nbPar; i++) {
      result[i] /= sum;
    }
    return result;
  }

  public static double getSum(int nbPar, double lambda) {
    double sum = 0.0;
    for (int i = 0; i < nbPar; i++) {
      sum += expDist(i, lambda);
    }
    return sum;
  }

  public static void main(String... args) {
    double alpha = 15.0;
    int nbPar = 30;
    ExponentialLaw el = new ExponentialLaw(nbPar, alpha);
    for (int i = 0; i < 10000; i++) {
      System.out.println(el.nextParam() + ";" + el.nextParam2());
    }
  }
}
