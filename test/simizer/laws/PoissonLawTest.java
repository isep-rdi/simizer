package simizer.laws;

import org.junit.Before;

/**
 * Tests the behavior of the PoissonLaw class.
 *
 * @author Max Radermacher
 */
public class PoissonLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new PoissonLaw(5);
  }
}
