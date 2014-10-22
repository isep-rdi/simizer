/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.requests;

import simizer.network.Message;

/**
 * Interface for request receiving entities.
 * Can be part of @see Network and @see Message
 * @author isep
 */
public interface RequestReceiver {
    public void onRequestReception(long timestamp,Message m);
    
}
