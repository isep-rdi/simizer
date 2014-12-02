package fr.isep.simizer.laws;

import fr.isep.simizer.utils.StdRandom;

/**
 * A {@code Law} representing a Zipf distribution.
 * <p>
 * The code has been adapted from that provided at the specified URL.
 * 
 * @author Hyunsik Choi
 * (<a href="http://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/">http://diveintodata.org/2009/09/13/zipf-distribution-generator-in-java/</a>)
 */
public class ZipfLaw extends Law {

  /** The parameter that defines the distribution. */
  private double skew;

  /** The sum of all the probabilities to normalize the values. */
  private double sum;

  /** A cache of the probability values to quickly sample values. */
  private double[] pdf;  // probabilityDistributionFunction

  /**
   * Initializes a new instance of the class with the skew set to {@code 1}.
   *
   * @param upperBound the maximum value that should be returned.  See {@link
   *            #ZipfLaw(int, double)} for more details.
   */
  public ZipfLaw(int upperBound) {
    this(upperBound, 1.0);
  }

  /**
   * Initializes a new instance of the class.
   *
   * @param upperBound the maximum value that should be returned when sampling
   *            the distribution.  This sets the maximum value of the
   *            distribution using the {@link setUpperBound(int)} method.
   *            {@code ZipfLaw} instances require that the upper bound is set.
   * @param skew the skew parameter for the distribution.  According to
   *            Wikipedia, this value should be greater than 1.
   */
  public ZipfLaw(int upperBound, double skew) {
    super();

    // configure the parameters
    this.skew = skew;
    setUpperBound(upperBound);
  }

  @Override
  public final void setUpperBound(int upperBound) {
    super.setUpperBound(upperBound);

    // we need to recalculate all of the values whenever the upper bound changes
    recalculateSum(upperBound);
    recalculateTable(upperBound);
  }

  @Override
  public void removeUpperBound() {
    throw new RuntimeException("Zipf laws require an upper bound.");
  }

  /**
   * Calculates the sum of all the individual values.
   * <p>
   * This allows the result to be normalized for consistency purposes.
   *
   * @param upperBound the number of values to include
   */
  private void recalculateSum(int upperBound) {
    this.sum = 0;
    for (int i = 1; i <= upperBound; i++) {
      this.sum += (1 / Math.pow(i, this.skew));
    }
  }

  /**
   * Generates and caches the PDF values for this distribution.
   * 
   * @param upperBound the number of values to includes
   */
  private void recalculateTable(int upperBound) {
    pdf = new double[upperBound];
    for (int i = 0; i < pdf.length; i++) {
      pdf[i] = getProbability(i + 1);
    }
  }

  /**
   * Returns the probability that a given rank occurs.
   *
   * @param rank the rank to check
   * @return the probability that the rank occurs
   */
  private double getProbability(int rank) {
    return (1.0d / Math.pow(rank, this.skew)) / this.sum;
  }

  @Override
  protected int generateNextValue() {
    int upperBound = pdf.length;
    
    // pick a value to find; we have a bunch of unevenly spaced buckets stored
    // in rankPbb, and we want to find where the value is located
    double value = StdRandom.uniform();
    for (int i = 0; i < upperBound; i++) {
      value -= pdf[i];

      // If we go below zero when subtracting the bucket, it means that this
      // bucket contains the value.  Otherwise, this bucket was not big enough
      // to contain the value and we should move on to check the next bucket.
      if (value <= 0) {
        return (i + 1);
      }
    }

    // Theoretically, we should never reach this code since sum(rankPbb) should
    // always be 1.  That means that we should reach zero by the end of the
    // above code.  However, due to rounding errors, this may not always happen.
    // If it breaks because of a rounding error, then we must have gone "off the
    // end" and want to return the last valid value.
    return upperBound - 1;
  }

}
