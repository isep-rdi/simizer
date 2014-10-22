/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import simizer.VM;
import simizer.processor.tasks.DiskTask;
import simizer.processor.tasks.IOTask;
import simizer.processor.tasks.ProcTask;
import simizer.processor.tasks.Task;
import simizer.processor.tasks.TaskSession;

/**
 *
 * @author slefebvr
 */
public class ProcessingUnit2 extends TaskProcessor {
    private static final int TASK_QUEUE_SZ =8; 
    private static Comparator<ProcTask> procTaskComparator = 
            new Comparator<ProcTask>() {
                    // Compare the amount of instruction processed on each task.
                    // Comparison is normalized to the size of each task,
                    // resulting in fair sharing of the respective tasks.
                    @Override
                    public int compare(ProcTask o1, ProcTask o2) {
                        if(o1.equals(o2)) return 0;
                        double leftO1 = 
                                (o1.getProcDone()*1.0D) / o1.getProc();
                        double leftO2 = 
                                (o2.getProcDone()*1.0D) / o2.getProc();
                        int propComp = Double.compare(leftO1,leftO2);
                        if(propComp == 0) // Shortest task first in absolute value 
                            return Long.compare(o1.getProcLeft(), o2.getProcLeft());
                        return propComp;
                    }
     };
    
    private static final int EPOCH = 4; // number of ms for each task
    private static final int MILLION= 1000* 1000;
    private final double coreMips;
    private final VM vmInstance;
    private final long epochInst;
    private ProcTask shortest;
    
    //Considered using priority queue but needed to iterate over the whole set
    //in the updateInstrcutions function. 
    private PriorityQueue<ProcTask> runningTasks = 
            new PriorityQueue<>(8,procTaskComparator);
                
    private List<TaskSession> runningSessions = new ArrayList<>();
    private long lastSchedule=0; // useful for computing task ends. 
    /**
     *
     * @param nbProc
     * @param coreMips
     * @param vm
     */
    public ProcessingUnit2(double coreMips, VM vm) {
        this.shortest = null;
         this.coreMips = coreMips;
         this.vmInstance = vm;
         this.epochInst = Math.round((coreMips*MILLION/1000) * EPOCH);
         
    }
  
    public int getNbTasks() {
        return(runningTasks.size());
    }
    
    /**
     * Computes the expected ending time of the new task, 
     * depending on the number of currently running tasks.
     * updates the number of computed instructions in the tasklist.
     * if necessary reschedules the next task ended event.
     * @param pt 
     */
    public void startTask(ProcTask pt, long timestamp) {
       System.out.println("START");
    }
    /**
     * Commits the task to the VM.
     * Starts the next taks in this task tasksession
     * @param timestamp
     * @param dt 
     */
    public void onDiskTaskEnded(long timestamp, DiskTask dt) {
        vmInstance.commitTask(dt);
        dt.finishTask();
    }
    /**
     * 
     * @param timestamp
     * @param ts 
     */
    public void startTaskSession(long timestamp, TaskSession ts) {
        Task t = ts.getNextTask();
        if(t==null)
            vmInstance.endTaskSession(ts, timestamp);
        else
            t.startTask(vmInstance, timestamp);
    }
    /**
     * Updates instructions of all the running tasks. 
     * Computes expected end time of the next ending task.
     * @param timestamp
     * @param pt 
     */
    public void onProcTaskEnded(long timestamp, ProcTask pt) {
        
    }
    /**
     * Made public for testability
     * computes the next task ended event timestamp
     * @param currentTime
     * @return timestamp of the next TaskEnded event
     */
    public long computeNextTaskEnd(long currentTime, ProcTask smallest, int taskPos) {
        
        long epochLeft = (long) Math.floor(smallest.getProcLeft()*1.0 / EPOCH);
        long endIn = ((epochLeft * runningTasks.size())+1)*EPOCH;
        return 0;
        
    }
    /**
     * Updates the number of processed instructions in the task list, 
     * finding the next shortest task along the way, setting it as
     * shortestTask.
     * 
     * @param currentTime 
     */
    private void updateInstructions(long currentTime) {
        
        if(currentTime == this.lastSchedule)
            return;
        PriorityQueue<ProcTask> tmp = new PriorityQueue<>(
                                runningTasks.size(),
                                procTaskComparator);
        int nbTasks = getNbTasks();
        int nbEpochs = (int) ((currentTime-lastSchedule)/EPOCH);
        int currentEpoch = 0;
        for(int i=(currentEpoch%nbTasks);
                 currentEpoch<nbEpochs;
                 currentEpoch++) {
            
            if(runningTasks.peek() == null) { // runningTasks Empty
                runningTasks = tmp;
                tmp = new PriorityQueue<>();
            }
            ProcTask pt = runningTasks.poll();
            pt.updateProc(epochInst);
            runningTasks.offer(pt);
        }                           
    }

    @Override
    public void onDataReady(long timestamp, IOTask t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
