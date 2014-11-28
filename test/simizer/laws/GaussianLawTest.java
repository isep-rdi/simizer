package simizer.laws;

import org.junit.Before;

/**
 * Tests the behavior of the GaussianLaw class.
 */
public class GaussianLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new GaussianLaw(30);
    law.setParam(7.5);
  }
}
