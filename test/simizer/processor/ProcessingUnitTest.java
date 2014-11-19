/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import org.junit.*;
import static org.junit.Assert.*;
import simizer.VM;
import simizer.processor.tasks.IOTask;
import simizer.processor.tasks.ProcTask;

/**
 *
 * @author isep
 */
public class ProcessingUnitTest {
    
    public ProcessingUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setNodeInstance method, of class ProcessingUnit.
     */
    @Test
    public void testSetNodeInstance() {
        System.out.println("setNodeInstance");
        VM vm = null;
        ProcessingUnit instance = null;
        instance.setNodeInstance(vm);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNbCores method, of class ProcessingUnit.
     */
    @Test
    public void testGetNbCores() {
        System.out.println("getNbCores");
        ProcessingUnit instance = null;
        int expResult = 0;
        int result = instance.getNbCores();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onDataReady method, of class ProcessingUnit.
     * When the OnDataReady method is called, 
     */
    @Test
    public void testOnDataReady() {
        System.out.println("onDataReady");
        long timestamp = 0L;
        IOTask t = null;
        ProcessingUnit instance = null;
        // instance.onDataReady(timestamp, t);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startProcTask method, of class ProcessingUnit.
     */
    @Test
    public void testStartProcTask() {
        System.out.println("startProcTask");
        long timestamp = 0L;
        ProcTask pt = null;
        ProcessingUnit instance = null;
        // instance.startProcTask(timestamp, pt);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onEpochEnded method, of class ProcessingUnit.
     */
    @Test
    public void testOnEpochEnded() {
        System.out.println("onEpochEnded");
        long timestamp = 0L;
        Integer data = null;
        ProcessingUnit instance = null;
        instance.onEpochEnded(timestamp, data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scheduleTaskEnded method, of class ProcessingUnit.
     */
    @Test
    public void testScheduleTaskEnded() {
        System.out.println("scheduleTaskEnded");
        ProcTask t = null;
        long timestamp = 0L;
        ProcessingUnit instance = null;
        instance.scheduleTaskEnded(t, timestamp);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onProcTaskEnded method, of class ProcessingUnit.
     */
    @Test
    public void testOnProcTaskEnded() {
        System.out.println("onProcTaskEnded");
        long timestamp = 0L;
        ProcTask data = null;
        ProcessingUnit instance = null;
        instance.onProcTaskEnded(timestamp, data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
