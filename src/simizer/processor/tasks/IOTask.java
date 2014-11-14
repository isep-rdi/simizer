package simizer.processor.tasks;

import simizer.VM;

/**
 * Represents a task that performs an IO operation.
 * <p>
 * This abstract class encompasses the shared behavior between all IO-related
 * tasks, such as the requirement to have a size.
 *
 * @author Sylvain Lefebvre
 */
public abstract class IOTask extends Task {

  /** The size of the operation. */
  private final int size;

  /**
   * Initializes a new task with the specified size.
   *
   * @param size the size of the operation
   */
  public IOTask(int size) {
    super();

    this.size = size;
  }

  /**
   * Returns the size of the IO operation.
   *
   * @return the size of the IO operation
   */
  public int getSize() {
    return size;
  }

  @Override
  public abstract void startTask(VM vm, long timestamp);

}
