/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor.events;

import simizer.event.Event;
import simizer.requests.Request;
import simizer.requests.RequestProcessor;

/**
 * @deprecated 
 * @author Sylvain Lefebvre
 */

public class DataReadyEvent extends Event<Request, RequestProcessor> {
    
     public DataReadyEvent(long timestamp, Request r, RequestProcessor rp) {
       super(timestamp,r,rp);
    }

    @Override
    public void dispatch() {
        this.target.onDataReady(timestamp, this.data);
    }
    
}
