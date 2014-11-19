package simizer.processor.tasks;

/**
 * A task representing some CPU-intensive operation.
 * <p>
 * {@code ProcTask} instances are executed by a {@code TaskProcessor}, and there
 * can only be one executing at any given time on the {@code TaskProcessor}.
 * That class handles the necessary behavior of simulating a processor,
 * switching between threads, etc.
 *
 * @author Sylvain Lefebvre
 */
public class ProcTask extends Task {

  /** The total number of instructions needed to execute this task. */
  private final long totalInstructions;

  /** The number of instructions remaining until the completion of this task. */
  private long remainingInstructions;

  /**
   * Initializes a new instance of the class.
   *
   * @param instructions the number of instructions needed to execute the task
   * @param memorySize the memory footprint of the task (unused)
   */
  public ProcTask(long instructions, int memorySize) {
    super();

    this.totalInstructions = instructions;
    this.remainingInstructions = instructions;
  }

  /**
   * Returns the number of instructions already completed.
   *
   * @return the number of instructions already completed
   */
  public long getInstructionsCompleted() {
    return totalInstructions - remainingInstructions;
  }

  /**
   * Returns the number of instructions remaining.
   *
   * @return the number of instructions remaining
   */
  public long getInstructionsRemaining() {
    return remainingInstructions;
  }

  /**
   * Returns the total number of instructions needed to finish the {@code Task}.
   * <p>
   * Note that this includes both completed and remaining instructions.
   *
   * @return the total number of instructions needed to finish the {@code Task}
   */
  public long getInstructionsCount() {
    return totalInstructions;
  }

  /**
   * Updates the task, marking some number of instructions as completed.
   * <p>
   * This is the equivalent of moving this {@code Task} {@code instructions}
   * instructions closer to completion.
   *
   * @param instructions the number of instructions to complete
   */
  public void updateProc(long instructions) {
    remainingInstructions -= instructions;
    if (remainingInstructions <= 0) {
      this.finishTask();
    }
  }

}
