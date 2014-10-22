/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.events;

import simizer.event.Event;
import simizer.processor.ProcessingUnit;

/**
 *
 * @author isep
 */
public class EpochEndedEvent extends Event<Integer,ProcessingUnit> {
    
    public EpochEndedEvent(long timestamp, Integer counter, ProcessingUnit pu) {
        super(timestamp, counter, pu);
    }
    @Override
    public void dispatch() {
        target.onEpochEnded(timestamp, data);
       
    }
    
}
