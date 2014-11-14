/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import simizer.VM;
import simizer.processor.events.EpochEndedEvent;
import simizer.processor.events.ProcTaskEndedEvent;
import simizer.processor.tasks.*;

/**
 * Processor Default class for simizer.
 * Provides performance model for Instructions-Based 
 * request execution, read and write tasks.
 * Has access to StorageElement's resources (for reading and writing)
 * Implements a (kind of) page caching, and swaps overflowing contents.
 * 
 * Tries to mimick the CFS scheduler.
 * 
 * @author Sylvain Lefebvre
 */
public class ProcessingUnit extends TaskProcessor {
    
    static private int DEF_EPOCH_GRAN = 4; //ms
    static private int EPOCH_THRESOLD = 20; //ms
    static private long MILLION = 1000 * 1000;
    private VM vmInstance = null;
   
    private long lastEpoch= 0;
    private LinkedList<ProcTask> runningQueue = new LinkedList<>();
    private LinkedList<ProcTask> readyQueue = new LinkedList<>();
    private int nbProc;
    private final double coreMips;
    private int epochCounter=0;
    private EpochEndedEvent upcomingEpoch;
    private Map<ProcTask, ProcTaskEndedEvent> upComingTasks =new HashMap<>();
  
    public ProcessingUnit(int nbProc, double coreMips) {
        this(nbProc, coreMips,null);
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

    /**
     * Called when a IOTask has ended.
     * If the next Task in the TaskSesssion is a ProcTask, 
     * then it will preempt currently running tasks by scheduling a new Epoch.
     * Other wise the next IO task is delegated to the vm.
     * @param t
     * @see  IOTask
     * @see TaskSession
     * @see VM
     * 
     * @param timestamp 
     */
    @Override
    public void onDataReady(long timestamp, IOTask t) {
        
         //1. gets next task.
        t.finishTask();
        vmInstance.commitTask(t);
        TaskSession ts = t.getTaskSession();
        
        Task nextTask = ts.getNextTask();
        
        if(nextTask==null) { //nothing to do here anymore, session is ended.
            vmInstance.endTaskSession(ts, timestamp);
            return;
        }
        //If it is processing task, then it will preempt currenlty
        // running tasks.
        //clear upcoming epochs and taskevents.
        // restart a new Epoch.
        if(nextTask instanceof ProcTask) {
            //startProcTask(timestamp, (ProcTask)nextTask);
            cancelEvent(upcomingEpoch);
            for(ProcTaskEndedEvent tee: upComingTasks.values())
                cancelEvent(tee);
            upComingTasks.clear();
            //readyQueue.addLast((ProcTask) nextTask);
            //scheduleEpoch(timestamp);
        }
            nextTask.startTask(vmInstance, timestamp);
        
       
    }
    /**
     * Starts a new processing task
     * @param timestamp
     * @param pt 
     */
    public void startProcTask(long timestamp, ProcTask pt) {
            readyQueue.addLast(pt);
//            if(runningQueue.size()==0) // ?? WTF ???
                scheduleEpoch(timestamp);
    }
    
    /**
     * Determines whether the current epoch has ended by checking the ready queue Size
     * and the time of the last epoch with DEF_EPOCH_GRAN per task.
     * Switches the empty ready and expired queues, schedule the next epoch event 
     * and starts execution of new tasks, CFS style.
     * 
     * @param timestamp
     * @param r 
     */
   
    public void onEpochEnded(long timestamp, Integer data) {
        scheduleEpoch(timestamp);
    }
 
    /**
     * Computes and the upcoming next EpochEnded and TaskEnded event, after updating
     * the current tasks with a call to updateProcTasks
     * @see  UpdateProcTask
     * @param timestamp 
     */
    private void scheduleEpoch(long timestamp) {
        updateProcTasks(timestamp);
        
        long left=0;
        int nbTasks = runningQueue.size();
        long span = ((nbTasks / nbProc < 6) ? EPOCH_THRESOLD : nbTasks * DEF_EPOCH_GRAN);
        long nextEpoch = timestamp +span;
        
        
        //1. Tries to make a prediction for the finishing time of each tasK.
        for (int i = 0; i < nbTasks; i++) {
            
            ProcTask t = runningQueue.remove();
            long instLeft = t.getInstructionsRemaining();
            long nbInst = getNBInst(timestamp+left, DEF_EPOCH_GRAN);
        
            
            if (nbInst >= instLeft) {
                int timeLeft = (int) (instLeft *1.0/ (nbInst*1.0 / DEF_EPOCH_GRAN));
        
                scheduleTaskEnded(t, (timestamp + left + timeLeft));
            }
            if(i%nbProc==0)
                left+=DEF_EPOCH_GRAN;
            runningQueue.addLast(t);
            
        }
        if(runningQueue.size() > 0) {
        
            lastEpoch = timestamp;
            upcomingEpoch = new EpochEndedEvent(nextEpoch,++epochCounter,this);
            registerEvent(upcomingEpoch);
        }
        
    }
    /**
     * Updates the number of processed instructions of the running tasks
     * from the last epoch.
     * Removes finished tasks from the ready Queue.
     * 
     * @param timestamp 
     */
    private void updateProcTasks(long timestamp) {
        long span = timestamp - lastEpoch, spent=0;
        int nbTasks = runningQueue.size();
        int taskCounter=0;
        long dur= (spent+DEF_EPOCH_GRAN > span) ? span-spent: DEF_EPOCH_GRAN;
        long nbInst = getNBInst(lastEpoch+spent, (int)dur);
        
        while(taskCounter<nbTasks && spent+lastEpoch <=timestamp) {
            ProcTask task = runningQueue.remove();
            
            task.updateProc(nbInst);
            if(task.getStatus() != TaskStatus.FINISHED)
                    runningQueue.addLast(task);
            
            if(taskCounter%nbProc==0) { // timings update
                spent+= dur;
                dur= (spent+DEF_EPOCH_GRAN > span) ? span-spent: DEF_EPOCH_GRAN;
                nbInst = getNBInst(lastEpoch+spent,(int) dur);
            }
            taskCounter++;
            
        }
        
        // moves from ready queue to running queue
        ProcTask ptNew = readyQueue.poll();
        while(ptNew != null) {
               runningQueue.addFirst(ptNew);
               ptNew = readyQueue.poll();
        }
                
    }
    
    /**
     * Computes the currently available CPU power
     * @return current CPU power according to the performance model
     */

    private long getNBInst(long timestamp, int length) {
        return getAvailableMips(length, coreMips);
    }

   
    /**
     * Creates and schedule a task ended event. Puts these events in a list
     * so that they can be cancelled when a rescheduling is
     * @param t needed.
     * @param timestamp 
     */
    public void scheduleTaskEnded(final ProcTask t, long timestamp) {
        ProcTaskEndedEvent tee = new ProcTaskEndedEvent(timestamp, t, this);
        upComingTasks.put((ProcTask) t, tee);
        this.registerEvent(tee);
    }
   
    
    private static long getAvailableMips(int duration, double coreMips) {
         long quant = (long) Math.floor(duration  * ((coreMips * MILLION) /1000.0D));
        return quant;
        
    }
    /**
     * Method called when a response is received, from a remote message
     */
    public void onResponseReceived(long timestamp, SendTask data) {
        
    }
    /**
     * Ends the current task and starts the next one
     * @param timestamp
     * @param data 
     */
    @Override
    public void onProcTaskEnded(long timestamp, ProcTask data) {
        
        data.finishTask();
        upComingTasks.remove(data);
        if(!data.getTaskSession().isComplete())
            data.getTaskSession().getNextTask().startTask(vmInstance, timestamp);
        
            
    }
    
}
