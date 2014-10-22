/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * http://www.ibm.com/developerworks/library/j-jtp0730/index.html
 */
package simizer.event;

import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author isep
 */
public class Channel extends PriorityBlockingQueue<Event>{
    public void registerEvent(final Event e) {
       this.offer(e);
    
    }
}
