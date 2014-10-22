/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import simizer.event.EventProducer;
import simizer.processor.tasks.IOTask;
import simizer.processor.tasks.ProcTask;

/**
 *
 * @author isep
 */
public abstract class TaskProcessor extends EventProducer {
    public abstract void onProcTaskEnded(long timestamp, ProcTask pt);
    public abstract void onDataReady(long timestamp, IOTask t);

}
