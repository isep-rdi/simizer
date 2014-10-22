/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer;

import simizer.laws.Law;
import simizer.network.Message;
import simizer.network.Network;
import simizer.requests.Request;
import simizer.requests.RequestFactory;

/**
 * This class simulates a web user client machine.
 * It is configured by three laws determining the parameters selection, 
 * think time , session duration.
 * 
 * Clients will generally send theirs requests to the LBNode corresponding to their attributes DC.
 * @see LBNode
 * @author Sylvain Lefebvre
 */
public class ClientNode extends Node {
    
    private static Law requestLaw, thinkTimeLaw, durationLaw;
    private static RequestFactory requestFact;
    
    
    /**
     * Sets the request factory for the clients
     * @param rf 
     */
    public static void configureRequestFactory(final RequestFactory rf) {
        requestFact = rf;
    }
    
    /**
     *  three laws determining: parameters selection, think time , session duration.
     * @param requestLaw
     * @param thinkTimeLaw
     * @param durationLaw 
     */
    public static void configureLaws(final Law requestLaw, final Law thinkTimeLaw, final Law durationLaw) {
        ClientNode.durationLaw = durationLaw;
        ClientNode.requestLaw = requestLaw;
        ClientNode.thinkTimeLaw = thinkTimeLaw;
    }
    
    
    private long startTime;
    private long endTime;
    private boolean ended = false;
    private int requestCount=0;
    private final int MAX_REQ=10;
   
    protected Node serviceAddress;
   
    /**
     * Creates a new Client with its arrival time and network,
     * Automatically computes the session duration for the client. 
     * @param id - long
     * @param startTime
     * @param network 
     */
    
    public ClientNode(int id, Network network, long startTime) {
          this(id, network,startTime, null);
    }
    
    public ClientNode(int id,  Network network,long startTime, LBNode lbnode) {
        super(id, network);
        this.startTime = startTime;
        this.serviceAddress = lbnode;
    }
    /**
     * @deprecated use set service address.
     * @param lbNode 
     */
    public void setLBNode(LBNode lbNode) {
        this.serviceAddress = lbNode;
    }
    public void setServiceAddress(Node svcNode) {
        this.serviceAddress = svcNode;
    }
    /**
     * Starts the client by scheduling its first request at startTime
     */
    public void startClient() {
        this.endTime = startTime + durationLaw.nextParam();
        scheduleNextRequest(this.startTime);
    }
    
    /**
     * reinitialization of the client to be re used by the network.
     * @param startTime: long new starting time.
     */
    public void reinit(long startTime) {
        this.startTime = startTime;
        this.endTime = startTime + durationLaw.nextParam();
        scheduleNextRequest(this.startTime);
    }
   
    /**
     * On request reception, the node prints out the received request and gets 
     * the next think time, then schedules a request
     * if it does not exceed the duration law.
     * @param timestamp
     * @param m 
     */
    
    @Override
    public void onRequestReception(long timestamp, Message m) {
        Request r  = m.getRequest();
        if(timestamp - r.getArTime() < 0) {
            System.out.println("PROBLEM");
        }
        requestCount++;
        System.out.println(r.toString() + ";" + (timestamp - r.getArTime()) +";"+ this.id);
        if(requestCount < MAX_REQ)//if(timestamp < endTime) 
            scheduleNextRequest(timestamp);
        else 
            ended = true;
    
            
    }

    private void scheduleNextRequest(long timestamp) {
            long nextTime = thinkTimeLaw.nextParam();
            int tplId = requestLaw.nextParam();
            Request r = requestFact.getRequest(timestamp+nextTime, tplId);
            //r.setAppId(1);
            nw.send(this, serviceAddress, r, timestamp+nextTime);
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    public boolean getEnded() {
        return ended;
    }

    @Override
    public void onRequestReceived(Node orig, Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

  
}
