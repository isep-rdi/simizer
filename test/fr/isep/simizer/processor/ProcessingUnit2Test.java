/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.processor;

import fr.isep.simizer.event.Channel;
import fr.isep.simizer.processor.events.ProcTaskEndedEvent;
import fr.isep.simizer.processor.tasks.DiskTask;
import fr.isep.simizer.processor.tasks.ProcTask;
import fr.isep.simizer.processor.tasks.TaskSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author isep
 */
public class ProcessingUnit2Test {
    private final ProcessingUnit2 instance;
    public ProcessingUnit2Test() {
        instance = new ProcessingUnit2(1000,null);
    }

    /**
     * Test of getNbTasks method, of class ProcessingUnit2.
     */
    @Test
    public void testGetNbTasks() {
        System.out.println("getNbTasks");
        
        int expResult = 0;
        int result = instance.getNbTasks();
        assertEquals(0, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of startTask method, of class ProcessingUnit2.
     */
    @Test
    public void testStartTask() {
        System.out.println("startTask");
        ProcTask pt = new ProcTask(1000,1000);
        long timestamp = 0L;
        //ProcessingUnit2 instance = new ProcessingUnit2(1000,null);
        instance.setChannel(new Channel());
        instance.startTask(pt, timestamp);
        assertEquals(instance.getOutputChannel().size(),1);
        System.out.println("Add second task");
        ProcTask pt2 = new ProcTask(1000,1000);
        instance.startTask(pt2, 100);
        ProcTaskEndedEvent ptee = (ProcTaskEndedEvent) instance.getOutputChannel().peek();
        assertEquals(instance.getOutputChannel().size(),1);
        assertEquals(pt,ptee.getData());
    }

    /**
     * Test of onDiskTaskEnded method, of class ProcessingUnit2.
     */
    @Test
    public void testOnDiskTaskEnded() {
        System.out.println("onDiskTaskEnded");
        long timestamp = 0L;
        DiskTask dt = null;
        //ProcessingUnit2 instance = null;
        instance.onDiskTaskEnded(timestamp, dt);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startTaskSession method, of class ProcessingUnit2.
     */
    @Test
    public void testStartTaskSession() {
        System.out.println("startTaskSession");
        long timestamp = 0L;
        TaskSession ts = null;
        ProcessingUnit2 instance = null;
        instance.startTaskSession(timestamp, ts);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onProcTaskEnded method, of class ProcessingUnit2.
     */
    @Test
    public void testOnProcTaskEnded() {
        System.out.println("onProcTaskEnded");
        long timestamp = 0L;
        ProcTask pt = null;
        ProcessingUnit2 instance = null;
        instance.onProcTaskEnded(timestamp, pt);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    /**
     * Test of computeNextTaskEnd method.
     */
    @Test
    public void testcomputeNextTaskEnd() {
        fail("The test case is prototype");
    }
}