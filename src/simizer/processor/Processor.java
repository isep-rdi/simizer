// Aruth Perum Jothi, Aruth Perum Jothi, Thani Perum Karunai, Aruth Perum Jothi
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.processor;

import simizer.ServerNode;
import simizer.requests.Request;

/**
 *
 * @author sathiya
 */
public interface Processor {
    
    public void onRequestReception(long timestamp, Request r);
    public void onRequestEnded(long timestamp, Request r);
    public void executeNextRequest(long timestamp);
    public void setNodeInstance(ServerNode n);
    public Request executeRequest(Request r);
    public Request read(Request r);
    public Request write(Request r);
    public Request modify(Request r);

    public int getNbCores();

}