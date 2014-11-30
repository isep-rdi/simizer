package simizer.laws;

import org.junit.Before;

/**
 * Tests the behavior of the ExponentialLaw class.
 *
 * @author Max Radermacher
 */
public class ExponentialLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new ExponentialLaw(15);
  }
}
