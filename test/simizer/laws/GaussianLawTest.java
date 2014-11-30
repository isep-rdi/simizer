package simizer.laws;

import org.junit.Before;

/**
 * Tests the behavior of the GaussianLaw class.
 *
 * @author Max Radermacher
 */
public class GaussianLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new GaussianLaw(15, 7.5);
  }
}
