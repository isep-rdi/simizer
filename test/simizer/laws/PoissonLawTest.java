package simizer.laws;

import org.junit.*;

/**
 * Tests the behavior of the PoissonLaw class.
 */
public class PoissonLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new PoissonLaw(10, 5);
  }
}