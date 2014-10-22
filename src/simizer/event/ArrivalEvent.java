/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.event;

import simizer.network.ClientGenerator;

/**
 *
 * @author isep
 */
public class ArrivalEvent extends Event<Long, ClientGenerator>{

   public ArrivalEvent(long timestamp, Long nb, ClientGenerator cg) {
       super(timestamp,nb,cg);
    }
    @Override
    public void dispatch() {
        target.onArrivalEvent(this.timestamp, this);
    }
    
}
