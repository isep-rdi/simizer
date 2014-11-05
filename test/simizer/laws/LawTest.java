package simizer.laws;

import org.junit.*;

/**
 * Basic tests that should be run on all Law subclasses.
 */
@Ignore
public class LawTest {
  protected Law law;

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
