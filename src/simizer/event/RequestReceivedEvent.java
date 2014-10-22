/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.event;

import simizer.network.Message;
import simizer.requests.RequestReceiver;

/**
 *
 * @author isep
 */
public class RequestReceivedEvent extends Event<Message, RequestReceiver> {

    public RequestReceivedEvent(long timestamp, Message m, RequestReceiver lrr) {
       super(timestamp,m,lrr);
    }
    @Override
    public void dispatch() {
        
       this.target.onRequestReception(timestamp, this.data);
    }
    
}
