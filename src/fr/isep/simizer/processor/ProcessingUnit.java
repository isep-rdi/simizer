package fr.isep.simizer.processor;

import fr.isep.simizer.nodes.VM;
import fr.isep.simizer.processor.events.EpochEndedEvent;
import fr.isep.simizer.processor.events.ProcTaskEndedEvent;
import fr.isep.simizer.processor.tasks.ProcTask;
import fr.isep.simizer.processor.tasks.SendTask;
import fr.isep.simizer.processor.tasks.Task;
import fr.isep.simizer.processor.tasks.TaskStatus;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Processor Default class for simizer. Provides performance model for
 * Instructions-Based request execution, read and write tasks. Has access to
 * StorageElement's resources (for reading and writing) Implements a (kind of)
 * page caching, and swaps overflowing contents.
 *
 * Tries to mimick the CFS scheduler.
 *
 * @author Sylvain Lefebvre
 */
public class ProcessingUnit extends TaskProcessor {

  private static final int DEF_EPOCH_GRAN = 4; //ms
  private static final int EPOCH_THRESOLD = 20; //ms
  private static final long MILLION = 1_000_000;
  private VM vmInstance = null;

  private long lastEpoch = 0;
  private LinkedList<ProcTask> runningQueue = new LinkedList<>();
  private LinkedList<ProcTask> readyQueue = new LinkedList<>();
  private int nbProc;
  private final double coreMips;
  private int epochCounter = 0;
  private EpochEndedEvent upcomingEpoch;
  private Map<ProcTask, ProcTaskEndedEvent> upComingTasks = new HashMap<>();

  public ProcessingUnit(int nbProc, double coreMips) {
    this(nbProc, coreMips, null);
  }

  public ProcessingUnit(int nbProc, double coreMips, VM vm) {
    this.nbProc = nbProc;
    this.coreMips = coreMips;
    this.vmInstance = vm;
  }

  public final void setNodeInstance(VM vm) {
    this.vmInstance = vm;
  }

  public int getNbCores() {
    return this.nbProc;
  }

  @Override
  public void runTask(Task task, long timestamp) {
    // If it is processing task, then it will preempt currenlty running tasks.
    // Clear upcoming epochs and TaskEvents and restart a new Epoch.
    if (task instanceof ProcTask) {
      cancelEvent(upcomingEpoch);
      for (ProcTaskEndedEvent tee : upComingTasks.values()) {
        cancelEvent(tee);
      }
      upComingTasks.clear();

      readyQueue.addLast((ProcTask) task);
      scheduleEpoch(timestamp);
    }

    super.runTask(task, timestamp);
  }

  @Override
  public void endTask(Task task, long timestamp) {
    if (task instanceof ProcTask) {
      upComingTasks.remove((ProcTask) task);
    }

    super.endTask(task, timestamp);
  }

  public void onProcTaskEnded(long timestamp, ProcTask task) {
    task.finish(timestamp);
  }

  /**
   * Determines whether the current epoch has ended by checking the ready queue
   * Size and the time of the last epoch with DEF_EPOCH_GRAN per task. Switches
   * the empty ready and expired queues, schedule the next epoch event and
   * starts execution of new tasks, CFS style.
   *
   * @param timestamp
   * @param data
   */
  public void onEpochEnded(long timestamp, Integer data) {
    scheduleEpoch(timestamp);
  }

  /**
   * Computes and the upcoming next EpochEnded and TaskEnded event, after
   * updating the current tasks with a call to updateProcTasks
   *
   * @see UpdateProcTask
   * @param timestamp
   */
  private void scheduleEpoch(long timestamp) {
    updateProcTasks(timestamp);

    long left = 0;
    int nbTasks = runningQueue.size();
    long span = ((nbTasks / nbProc < 6) ? EPOCH_THRESOLD : nbTasks * DEF_EPOCH_GRAN);
    long nextEpoch = timestamp + span;

    //1. Tries to make a prediction for the finishing time of each tasK.
    for (int i = 0; i < nbTasks; i++) {
      ProcTask t = runningQueue.remove();
      long instLeft = t.getInstructionsRemaining();
      long nbInst = getNBInst(timestamp + left, DEF_EPOCH_GRAN);

      if (nbInst >= instLeft) {
        int timeLeft = (int) (instLeft * 1.0 / (nbInst * 1.0 / DEF_EPOCH_GRAN));

        scheduleTaskEnded(t, (timestamp + left + timeLeft));
      }
      if (i % nbProc == 0) {
        left += DEF_EPOCH_GRAN;
      }
      runningQueue.addLast(t);
    }

    if (runningQueue.size() > 0) {
      lastEpoch = timestamp;
      upcomingEpoch = new EpochEndedEvent(nextEpoch, ++epochCounter, this);
      registerEvent(upcomingEpoch);
    }
  }

  /**
   * Updates the number of processed instructions of the running tasks from the
   * last epoch. Removes finished tasks from the ready Queue.
   *
   * @param timestamp
   */
  private void updateProcTasks(long timestamp) {
    long span = timestamp - lastEpoch, spent = 0;
    int nbTasks = runningQueue.size();
    int taskCounter = 0;
    long dur = (spent + DEF_EPOCH_GRAN > span) ? span - spent : DEF_EPOCH_GRAN;
    long nbInst = getNBInst(lastEpoch + spent, (int) dur);

    while (taskCounter < nbTasks && spent + lastEpoch <= timestamp) {
      ProcTask task = runningQueue.remove();

      task.updateProc(nbInst);
      if (task.getStatus() != TaskStatus.FINISHED) {
        runningQueue.addLast(task);
      }

      if (taskCounter % nbProc == 0) { // timings update
        spent += dur;
        dur = (spent + DEF_EPOCH_GRAN > span) ? span - spent : DEF_EPOCH_GRAN;
        nbInst = getNBInst(lastEpoch + spent, (int) dur);
      }
      taskCounter++;
    }

    // moves from ready queue to running queue
    ProcTask ptNew = readyQueue.poll();
    while (ptNew != null) {
      runningQueue.addFirst(ptNew);
      ptNew = readyQueue.poll();
    }
  }

  /**
   * Computes the currently available CPU power
   *
   * @return current CPU power according to the performance model
   */
  private long getNBInst(long timestamp, int length) {
    return getAvailableMips(length, coreMips);
  }

  /**
   * Creates and schedule a task ended event. Puts these events in a list so
   * that they can be cancelled when a rescheduling is
   *
   * @param t needed.
   * @param timestamp
   */
  public void scheduleTaskEnded(final ProcTask t, long timestamp) {
    ProcTaskEndedEvent tee = new ProcTaskEndedEvent(timestamp, t, this);
    upComingTasks.put((ProcTask) t, tee);
    this.registerEvent(tee);
  }

  private static long getAvailableMips(int duration, double coreMips) {
    long quant = (long) Math.floor(duration * ((coreMips * MILLION) / 1000.0D));
    return quant;
  }

  /**
   * Method called when a response is received, from a remote message
   */
  public void onResponseReceived(long timestamp, SendTask data) {}

}
