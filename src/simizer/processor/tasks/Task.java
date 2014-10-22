/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.tasks;

import simizer.VM;

/**
 * This class is used for simulating a Task on a given node.
 * It takes the quantity of instructions to process.
 * Pass it to the processor to begin execution.
 * @author Sylvain Lefebvre
 */
public abstract class Task {
    private TaskStatus status = TaskStatus.RUNNING;
    
    private TaskSession ts;
    
    public Task(TaskSession ts) {
        this.ts = ts;
    }
    
    public TaskSession getTaskSession() {
        return ts;
    }
    public void setTaskSession(TaskSession ts) {
        this.ts = ts;
    }
    public TaskStatus getStatus() {
        return this.status;
    }
    public abstract void startTask(VM vm, long timestamp);
    
    public void finishTask() {
        this.status = TaskStatus.FINISHED;
    }

    
}
