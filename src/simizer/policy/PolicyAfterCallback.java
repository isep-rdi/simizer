/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.policy;

import simizer.Node;
import simizer.requests.Request;

/**
 *
 * @author isep
 */
public interface PolicyAfterCallback {
    public void receivedRequest(Node n, Request r);
    
}
