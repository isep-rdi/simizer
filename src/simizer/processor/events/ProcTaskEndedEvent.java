/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.events;

import simizer.event.Event;
import simizer.processor.ProcessingUnit;
import simizer.processor.tasks.ProcTask;

/**
 *
 * @author isep
 */
public class ProcTaskEndedEvent extends Event<ProcTask, ProcessingUnit> {
      public ProcTaskEndedEvent(long timestamp, ProcTask pt, ProcessingUnit pu) {
       super(timestamp,pt,pu);
    }
    @Override
    public void dispatch() {
        this.target.onProcTaskEnded(timestamp, this.data);
    }
    
}
