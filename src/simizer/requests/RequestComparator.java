package simizer.requests;

import java.util.Comparator;

/**
 * Compares two {@code Request} objects by their arrival timestamps.
 * <p>
 * This {@link Comparator} can be used to order {@link Request} objects by their
 * arrival timestamps in ascending order.
 */
public class RequestComparator implements Comparator<Request> {

  /**
   * Compares two {@code Request} objects to see which comes first.
   *
   * @param first the object to compare
   * @param second the object to compare against
   * @return a negative value, zero, or a positive value if the first object is
   *             less than, equal to, or greater than the second object
   */
  @Override
  public int compare(Request first, Request second) {
    return Long.compare(first.artime, second.artime);
  }

}
