package fr.isep.simizer.processor;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.event.EventProducer;
import fr.isep.simizer.processor.tasks.Task;
import fr.isep.simizer.processor.tasks.TaskSession;

/**
 * Responsible for handling the execution of {@code TaskSession} objects.
 * <p>
 * Subclasses may override certain functionality to more accurately simulate the
 * execution of various kinds of {@link Task} objects, but the purpose of this
 * class is to handle the starting/stopping/finishing of {@link TaskSession}
 * objects.
 */
public abstract class TaskProcessor extends EventProducer {

  /**
   * Schedules the next {@code Task} to be run immediately.
   * <p>
   * When this is called, the next {@link Task} from the specified {@link
   * TaskSession} is scheduled for immediate execution in the "run loop."
   * <p>
   * The return value is present to allow subclasses to add additional behavior
   * depending on whether or not a {@link Task} could be scheduled.
   *
   * @param taskSession where get the {@link Task} to run
   * @param timestamp the current timestamp of the simulation
   * @return whether or not a {@link Task} was scheduled to be run
   */
  public boolean scheduleTask(TaskSession taskSession, long timestamp) {
    Task task = taskSession.getNextTask();
    if (task != null) {
      registerEvent(new RunTaskEvent(timestamp, task, this));
      return true;
    } else {
      // there was not a Task to schedule
      return false;
    }
  }

  /**
   * Runs the specified {@code Task}.
   * <p>
   * This method performs the {@link Task#run(TaskProcessor, long)} method of
   * the specified {@link Task}.  Subclasses can override this method to provide
   * additional functionality or behavior for specific types of tasks.  In
   * addition, a lot of functionality can be implemented within the tasks
   * themselves.
   *
   * @param task the {@link Task} to run
   * @param timestamp the current timestamp of the simulation
   */
  public void runTask(Task task, long timestamp) {
    task.run(this, timestamp);
  }

  /**
   * Called by the {@code Task} when it has finished its execution.
   * <p>
   * Subclasses can override this method to add additional behavior when {@link
   * Task}s are finished.  Note that subclasses must call the superclass's
   * implementation to allow the next {@link Task} to be executed.
   *
   * @param task the {@link Task} that finished
   * @param timestamp the current timestamp of the simulation
   */
  public void endTask(Task task, long timestamp) {
    scheduleTask(task.getTaskSession(), timestamp);
  }

}

/**
 * Runs the specified {@code Task} on a {@code TaskProcessor}.
 */
class RunTaskEvent extends Event<Task, TaskProcessor> {
  protected RunTaskEvent(long timestamp, Task data, TaskProcessor target) {
    super(timestamp, data, target);
  }

  @Override
  public void dispatch() {
    this.target.runTask(this.data, timestamp);
  }
}
