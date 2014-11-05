package simizer.laws;

import java.lang.reflect.Field;
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

  /**
   * Tests whether or not the theoretically maximum value can be achieved.
   *
   * @throws Exception if there is a problem with the reflection
   */
  @Test
  public void testMaximum() throws Exception {
    Field upperBound = Law.class.getDeclaredField("upperBound");
    upperBound.setAccessible(true);
    int bound = (Integer) upperBound.get(law);

    long target = System.currentTimeMillis() + 4*1000;

    boolean found = false;
    while (System.currentTimeMillis() < target) {
      int value = law.nextValue();
      Assert.assertTrue("generated a value that was too large", value < bound);
      Assert.assertTrue("generated a value that was too small", value >= 0);

      if (value == (bound - 1)) {
        found = true;
        break;
      }
    }
    Assert.assertTrue("could not generate the maximum value", found);
  }

  /**
   * Tests whether or not the theoretically minimum value can be achieved.
   */
  @Test
  public void testMinimum() {
    long target = System.currentTimeMillis() + 4*1000;

    boolean found = false;
    while (System.currentTimeMillis() < target) {
      int value = law.nextValue();
      Assert.assertTrue("generated a value that was too small", value >= 0);

      if (value == 0) {
        found = true;
        break;
      }
    }
    Assert.assertTrue("could not generate the minimum value", found);
  }
}
