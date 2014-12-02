package fr.isep.simizer.laws;

import org.junit.Before;

/**
 * Tests the behavior of the UniformLaw class.
 *
 * @author Max Radermacher
 */
public class UniformLawTest extends LawTest {
  @Before
  public void setUp() {
    law = new UniformLaw(100);
  }
}
