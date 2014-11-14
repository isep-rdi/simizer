package simizer.storage;

/**
 * Thrown when there is not enough space to store a resource.
 * <p>
 * This exception is raised when the system tries to load a resource that is too
 * big to be stored in the currently-available memory.
 *
 * @author Sylvain Lefebvre
 */
public class NotEnoughSpaceException extends Exception {

  /** The object where the error occurred. */
  private final Memory memory;

  /**
   * Initializes the error corresponding to the specified {@code Memory}.
   *
   * @param memory the {@link Memory} object where the problem occurred
   */
  public NotEnoughSpaceException(Memory memory) {
    super("There is not enough space to storage that resource.");

    this.memory = memory;
  }

  /**
   * Returns the {@code Memory} associated with this {@code Exception}.
   *
   * @return the {@link Memory} associated with this {@code Exception}
   */
  public Memory getMemory() {
    return memory;
  }

}
