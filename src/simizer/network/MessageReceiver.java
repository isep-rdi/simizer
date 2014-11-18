/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.network;

import simizer.network.Message;

/**
 * Interface for request receiving entities.
 * Can be part of @see Network and @see Message
 * @author isep
 */
public interface MessageReceiver {
    public void onMessageReceived(long timestamp,Message m);
    
}
