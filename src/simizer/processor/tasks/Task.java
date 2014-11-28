package simizer.processor.tasks;

import simizer.nodes.VM;
import simizer.processor.TaskProcessor;

/**
 * Simulates a task on a VM.
 * <p>
 * The concrete subclasses of this class will handle the various tasks that need
 * to happen to fulfill a request.  Examples of subclasses include {@link
 * DiskTask} for reading from a file system, {@link SendTask} for sending a
 * message across a network, and {@link ProcTask} to represent some
 * processor-intensive task.
 *
 * @author Sylvain Lefebvre
 */
public abstract class Task {

  /** The current status of the task. */
  private TaskStatus status = TaskStatus.RUNNING;

  /** The {@code TaskSession} containing this task. */
  private TaskSession ts = null;

  /** The {@code TaskProcessor} running this task. */
  private TaskProcessor taskProcessor = null;

  /**
   * Initializes a new {@code Task}.
   */
  public Task() {}

  /**
   * Initializes a new {@code Task}.
   *
   * @deprecated Use {@link #Task()} instead.
   *
   * @param ts the corresponding {@link TaskSession}
   */
  @Deprecated
  public Task(TaskSession ts) {
    this.ts = ts;
  }

  /**
   * Returns the {@code TaskSession} associated with this {@code Task}.
   *
   * @return the {@code TaskSession} associated with this {@code Task}
   */
  public TaskSession getTaskSession() {
    return ts;
  }

  /**
   * Changes the {@code TaskSession} associated with this task.
   *
   * @param ts the new {@code TaskSession}
   */
  public void setTaskSession(TaskSession ts) {
    this.ts = ts;
  }

  /**
   * Starts the {@code Task} on the specified {@code VM}.
   * <p>
   * The specified behavior is depends on the subclass.  Tasks are given access
   * to the {@link VM} and all of its subsystems (processor, memory, networking,
   * etc.) to be able to complete their task.
   *
   * @param vm the {@code VM} on which to execute this {@code Task}
   * @param timestamp the time when the execution starts (to schedule events)
   *
   * @deprecated Use {@link #run(TaskProcessor, long)} instead.
   */
  public void startTask(VM vm, long timestamp) {}

  /**
   * Runs the {@code Task}.
   * <p>
   * Subclasses should use this method to implement custom behavior.  Subclasses
   * are also responsible for calling the {@link #finish(long)} method when the
   * {@link Task} is finished.
   * <p>
   * Subclasses must invoke this implementation.  If they do not, the {@link
   * #finish(long)} method will not function properly.
   *
   * @param processor the {@link TaskProcessor} that is executing this {@code
   *            Task}
   * @param timestamp the timestamp when the {@code Task} is being started
   */
  public void run(TaskProcessor processor, long timestamp) {
    this.taskProcessor = processor;
  }

  /**
   * Finishes the current {@code Task}, starting the next {@code Task}.
   * <p>
   * This method will schedule the execution of the next {@code Task}, as
   * appropriate.  Therefore, it is <b>essential</b> that this method is called
   * to ensure the proper execution of the subsequent tasks.
   *
   * @deprecated Use {@link #finish(long)} instead.
   */
  public void finishTask() {
    this.status = TaskStatus.FINISHED;
  }

  /**
   * Finishes the current {@code Task}, starting the next {@code Task}.
   * <p>
   * This method will schedule the execution of the next {@code Task}, as
   * appropriate.  Therefore, it is <b>essential</b> that this method is called
   * to ensure the proper execution of the subsequent tasks.
   *
   * @param timestamp the current timestamp of the simulation
   */
  public void finish(long timestamp) {
    this.status = TaskStatus.FINISHED;

    TaskProcessor processor = this.taskProcessor;
    this.taskProcessor = null;
    processor.endTask(this, timestamp);
  }

  /**
   * Returns the status of the {@code Task}.
   *
   * @return the status of the {@code Task}
   */
  public TaskStatus getStatus() {
    return this.status;
  }
}
