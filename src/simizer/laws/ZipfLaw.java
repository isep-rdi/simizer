package simizer.laws;

import java.util.Random;

/**
 *
 * @author Hyunsik Choi
 * http://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/
 */
public class ZipfLaw extends Law {

  private Random rnd = new Random(System.currentTimeMillis());
  private double skew;
  private double bottom;
  private double[] rankPbb;

  public ZipfLaw(int nbParams) {
    this(nbParams, 1.0);
  }

  public ZipfLaw(int nbParams, double skew) {
    super(nbParams);

    this.setParam(skew);
    rankPbb = new double[nbParams];
    for (int i = 0; i < rankPbb.length; i++) {
      rankPbb[i] = getProbability(i + 1);
    }
  }

  @Override
  public final void setParam(double par) {
    this.skew = par;
    this.bottom = 0;
    for (int i = 1; i <= nbParams; i++) {
      this.bottom += (1 / Math.pow(i, this.skew));
    }
  }

  // This method returns a probability that the given rank occurs.
  public final double getProbability(int rank) {
    return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
  }

  // the next() method returns an rank id.
  // The frequency of returned rank ids are followiong Zipf distribution.
  public int nextParam2() {
    int rank;
    double friquency;
    double dice;

    rank = rnd.nextInt(nbParams);
    friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
    dice = rnd.nextDouble();

    while (!(dice < friquency)) {
      rank = rnd.nextInt(nbParams);
      friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
      dice = rnd.nextDouble();
    }

    return rank;
  }
  // iterate ranks until the appropriate one is reached
  // should be faster (less random than nextParam)

  @Override
  public int nextParam() {
    int rank = 1;
    double p = rnd.nextDouble();
    double blow = 0.0, bup = rankPbb[rank - 1];

    while ((p < blow || p >= bup) && rank <= nbParams) {
      rank++;
      blow = bup;
      bup += rankPbb[rank - 1];
    }
    return rank - 1;
  }

  public static void main(String[] args) {
    ZipfLaw zipf = new ZipfLaw(30, 0.8);

    long total1 = 0, total2 = 0;
    int p;
    for (int i = 0; i < 10000; i++) {
      p = zipf.nextParam();
      System.out.println(i + ";" + p);
    }

    System.out.println("avg1 = " + (total1 * 1.0 / 1000));
    System.out.println("avg2 = " + (total2 * 1.0 / 1000));
  }
}
