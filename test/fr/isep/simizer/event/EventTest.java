/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.event;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author isep
 */
public class EventTest {
    EventImpl ei1 = new EventImpl(0,1,"deux");
    EventImpl ei2 = new EventImpl(0,2,"trois");
    
    public EventTest() {
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
     * Test of getData method, of class Event.
     */
    @Test
    public void testGetData() {
        System.out.println("getData");
        Integer  expResult = 1;
        Integer result = ei1.getData();
        //System.out.println(expResult + " " + result);
        assertTrue(expResult.intValue()==result.intValue());
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    
    /**
     * Test of dispatch method, of class Event.
     */
    @Test
    public void testDispatch() {
        System.out.println("dispatch");
        
        ei1.dispatch();
        // TODO review the generated test code and remove the default call to fail.
//        fail("dispatch fails.");
    }

    /**
     * Test of compareTo method, of class Event.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        
        boolean expEq = ei1.equals(ei2);
        assertFalse(expEq);
       // fail("Inequality test fail.");
        int expResult = 0;
        int result = ei2.compareTo(ei1);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
       // fail("The test case is a prototype.");
    }

    public class EventImpl extends Event<Integer,String> {

        public EventImpl(long timestamp, Integer t, String str) {
            super(timestamp, t, str);
        }

        @Override
        public void dispatch() {
            System.out.println("HelloOOO" + this.data + this.timestamp + this.target);
        }
    }
}
