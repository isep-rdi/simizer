/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.tasks;

import simizer.VM;
import simizer.processor.ProcessingUnit;

/**
 * Processing task
 * @author slefebvr
 */
public class ProcTask extends Task {
    private final long nbInstructions;
    private long insLeft;
    
    public ProcTask(long nbInstructions, int memSize) {
        super(null);
        this.nbInstructions = nbInstructions;
        this.insLeft = nbInstructions;
    }
    
    public long getProcDone() {
        return nbInstructions - insLeft;
    }
    public long getProcLeft() {
        return insLeft;
    }
    public long getProc() {
        return nbInstructions;
    }
    public void updateProc(long inst) {
        insLeft-=inst;
        if(insLeft<=0)
            this.finishTask();
    }

    @Override
    public void startTask(VM vm, long timestamp) {
        ProcessingUnit pu = vm.getProcessingUnit();
        pu.startProcTask(timestamp, this);
    }
    
}
