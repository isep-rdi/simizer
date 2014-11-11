package simizer.processor.tasks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import simizer.VM;
import simizer.processor.ProcessingUnit;

/**
 * This class is used for figuring a process. It is a composition of tasks, that
 * is passed to the processing unit for execution. Tasks in the TaskSession are
 * executed in order by the processing unit, by by successive calls to
 * getNextTask();
 *
 * @see VM
 * @see ProcessingUnit
 *
 * @author Sylvain Lefebvre
 */
public class TaskSession {

  private int sessionId;
  private final Queue<Task> taskList = new LinkedList<>();
  private final List<Integer> resList = new ArrayList<>();
  private Task currentTask;

  public TaskSession(int sesId) {
    this.sessionId = sesId;
  }

  public void addResource(Integer rId) {
    resList.add(rId);
  }

  public List<Integer> getResourceList() {
    return this.resList;
  }

  public int getNbTasks() {
    return taskList.size();
  }

  public Task getCurrentTask() {
    return this.currentTask;
  }

  public void addTask(Task t) {
    t.setTaskSession(this);
    taskList.add(t);
  }

  public Task getNextTask() {
    currentTask = taskList.poll();
    return currentTask;
  }

  /**
   *
   * @return true if the task session is complete
   */
  public boolean isComplete() {
    return (taskList.size() == 0);
  }

  public int getSessionId() {
    return this.sessionId;
  }
}
