/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.isep.simizer.processor.events;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestProcessor;

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
