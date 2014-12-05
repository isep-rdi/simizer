/*
 * To change this license header, choose License Headers in Project Properties.a
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.processor.tasks;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Max Radermacher
 */
public class ProcTaskTest {

  private ProcTask task;

  /**
   * ProcTasks are required to have a non-negative number of instructions.
   */
  @Test(expected=Exception.class)
  public void testNegativeInstructions() {
    task = new ProcTask(-10, 10);
  }

  /**
   * Tests that there are no errors when using a negative number.
   */
  @Test
  public void testZeroInstructions() {
    task = new ProcTask(0, 10);

    assertEquals("expected zero instructions", 0, task.getInstructionsCount());
  }
}
