/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.requests;

/**
 *
 * @author isep
 */
public interface RequestFinisher {
     public void onRequestEnded(long timestamp, Request r);
}
