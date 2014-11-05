package simizer.laws;

import org.junit.*;

/**
 * Tests the behavior of the UniformLaw class.
 */
public class UniformLawTest {
  private UniformLaw law;

  @Before
  public void setUp() {
    law = new UniformLaw(100);
  }

  /**
   * Outputs values from the law to evaluate their distribution.
   */
  @Test
  public void testOutput() {
    for (int i = 0; i < 10000; i++) {
      System.out.println(law.nextValue());
    }
  }
}
