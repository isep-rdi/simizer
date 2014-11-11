package simizer.processor.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import simizer.VM;
import simizer.processor.ProcessingUnit;

/**
 * Represents the sequential execution of a list of tasks.
 * <p>
 * It contains a list of tasks that are passed to the {@link VM} for execution.
 * Some of the {@link Task} objects will make use of the {@link ProcessingUnit},
 * but it is not necessary that a {@link Task} interacts with the processor.  It
 * is also possible and acceptable for a task to interact solely with the file
 * system or the network.
 *
 * @author Sylvain Lefebvre
 */
public class TaskSession {

  /** The list of tasks in the queue to be executed. */
  private final Queue<Task> tasks = new LinkedList<>();

  /** The currently executing task. */
  private Task currentTask = null;

  private final int sessionId;
  private final List<Integer> resList = new ArrayList<>();

  public TaskSession(int sesId) {
    this.sessionId = sesId;
  }

  /**
   * Returns the number of tasks remaining to be executed.
   * <p>
   * This does <b>not</b> include the task which is currently executing (if
   * there is a task which is currently executing).
   *
   * @return the number of tasks remaining to be executed
   */
  public int getRemainingTaskCount() {
    return tasks.size();
  }

  /**
   * Adds the specified {@code Task} to this {@code TaskSession}.
   * <p>
   * Tasks are executed sequentially in the order in which they are added.
   * Tasks within the same {@code TaskSession} are not executed in parallel.
   * 
   * @param task the {@link Task} to add
   */
  public void addTask(Task task) {
    task.setTaskSession(this);
    tasks.add(task);
  }

  /**
   * Gets the next task for this {@code TaskSession}.
   * <p>
   * Note that this is not a constant method, meaning that each iteration will
   * cause the current task to be removed from the {@code TaskSession} and
   * replaced with the followign {@code Task}.
   * 
   * @return the next task
   */
  public Task getNextTask() {
    currentTask = tasks.poll();
    return currentTask;
  }

  /**
   * Returns whether or not all of the tasks have been completed.
   *
   * @return true if all the tasks for this {@code TaskSession} have finished
   */
  public boolean isComplete() {
    return (tasks.size() == 0);
  }

  /** @deprecated */
  public int getSessionId() {
    return this.sessionId;
  }

  /**
   * Returns the number of remaining tasks.
   *
   * @deprecated Use {@link #getRemainingTaskCount()} instead.
   *
   * @return the number of remaining tasks
   */
  public int getNbTasks() {
    return getRemainingTaskCount();
  }

  /** @deprecated */
  public void addResource(Integer rId) {
    resList.add(rId);
  }

  /** @deprecated */
  public List<Integer> getResourceList() {
    return this.resList;
  }

  /** @deprecated */
  public Task getCurrentTask() {
    return this.currentTask;
  }
}
