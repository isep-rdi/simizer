/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.tasks;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author slefebvr 
 */
public class TaskSessionTest {
    
    public TaskSessionTest() {
    }
    
 
    /**
     * Test of getCurrentTask method, of class TaskSession.
     * returns null if getNextTask() was not called yet
     */
    @Test
    public void testGetCurrentTask() {
        System.out.println("getCurrentTask");
        TaskSession instance = new TaskSession(1);
        
        Task firstTask = new ProcTask(100,100);
        //Task secondTask = new ProcTask(100,820);
        
        instance.addTask(firstTask);
        //instance.addTask(secondTask);
        Task result = instance.getCurrentTask();
        
        assertEquals(null, result);
        Task firstTaskAgain = instance.getNextTask();
        assertEquals(firstTask, instance.getCurrentTask());
        assertEquals(firstTaskAgain, instance.getCurrentTask());
        
    }

    /**
     * Test of addTask method, of class TaskSession.
     */
    @Test
    public void testAddTask() {
        System.out.println("addTask");
        Task t = new ProcTask(100,100);
        TaskSession instance = new TaskSession(0);
        instance.addTask(t);
        assertEquals(1,instance.getNbTasks());
        assertEquals(instance,t.getTaskSession());
    }

    /**
     * Test of getNextTask method, of class TaskSession.
     */
    @Test
    public void testGetNextTask() {
        System.out.println("getNextTask");
       TaskSession instance = new TaskSession(1);
        
        Task firstTask = new ProcTask(100,100);
        Task secondTask = new ProcTask(100,820);
        
        instance.addTask(firstTask);
        instance.addTask(secondTask);
        assertEquals(firstTask, instance.getNextTask());
        assertEquals(secondTask, instance.getNextTask());
     }

   


 
}