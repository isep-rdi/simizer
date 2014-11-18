package simizer.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the RequestComparator class.
 */
public class RequestComparatorTest {
  private RequestComparator comparator = null;

  @Before
  public void setUp() {
    comparator = new RequestComparator();
  }

  /**
   * Tests when the first {@code Request} comes first.
   */
  @Test
  public void testAscending() {
    Request r1 = new Request(1, 0, 5, "", 0, null, 0);
    Request r2 = new Request(1, 0, 10, "", 0, null, 0);

    Assert.assertTrue("r1 should be before r2", comparator.compare(r1, r2) < 0);
    Assert.assertTrue("r2 should be after r1", comparator.compare(r2, r1) > 0);
  }

  /**
   * Tests when the second {@code Request} comes first.
   */
  @Test
  public void testDescending() {
    Request r1 = new Request(1, 0, 10, "", 0, null, 0);
    Request r2 = new Request(1, 0, 5, "", 0, null, 0);

    Assert.assertTrue("r1 should be after r2", comparator.compare(r1, r2) > 0);
    Assert.assertTrue("r2 should be before r1", comparator.compare(r2, r1) < 0);
  }

  /**
   * Tests when the two {@code Request} objects are equal.
   */
  @Test
  public void testEquality() {
    Request r1 = new Request(1, 0, 5, "", 0, null, 0);
    Request r2 = new Request(1, 0, 5, "", 0, null, 0);

    Assert.assertEquals("r1 should equal r2", 0, comparator.compare(r1, r2));
    Assert.assertEquals("r2 should equal r1", 0, comparator.compare(r2, r1));
  }

  /**
   * Tests when the two {@code Request} objects refer to the same object.
   */
  @Test
  public void testEqualObjects() {
    Request r = new Request(1, 0, 7, "", 0, null, 0);

    Assert.assertEquals("r should equal r", 0, comparator.compare(r, r));
    Assert.assertEquals("r should equal r", 0, comparator.compare(r, r));
  }
}
