/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer;

import java.util.HashMap;
import java.util.Map;
import simizer.network.Message;
import simizer.network.Network;
import simizer.policy.Policy;
import simizer.policy.PolicyAfterCallback;
import simizer.requests.Request;

/**
 *
 * @author Sylvain Lefebvre
 */
public class LBNode extends Node  {
    private final Policy pol;
    private  PolicyAfterCallback pacb=null;
    Network outside,inside;
    private final Map<Long, Node> pending = new HashMap<>();
    
    
        
    public LBNode(Policy p) {
         super(-1,null);
         this.pol = p;
    }
    public LBNode(int id, Policy p) {
        super(id,null);
        this.pol = p;
    }
    public void setOutsideNetwork(final Network outside) {
        this.outside = outside;
    }
    public void setInsideNetwork(final Network inside) {
        this.inside = inside;
    }

    
    public void registerAfter(final PolicyAfterCallback pacb) {
        this.pacb = pacb;
    }

    @Override
    public void onMessageReceived(long timestamp, Message m) {
       
        Request r = m.getRequest();
        
        if(r.getFtime() == 0) {
            if(m.getOrigin() != null)
                pending.put(r.getId(), m.getOrigin());
    
            long start = System.nanoTime();
            Node target = pol.loadBalance(r);
            r.setFwdTime(System.nanoTime()-start);
             
             inside.send(this, target, r, timestamp);
        }  else  {
        
            if(pacb != null) {
                pacb.receivedRequest(inside.getNode(r.getNodeId()), r);
            }

            if(pending.containsKey(r.getId())) {
                Node back = pending.remove(r.getId());
                outside.send(this, back, r, timestamp);
            } else {
                System.out.println("Should not happen, never ! ");
                System.out.println(r.toString() + " ERROR ");
                
                /* What is this ?
                if (Simizer.requestCount%100==0){
                    long endTime = r.getArTime() + (timestamp - r.getArTime());
                    Simizer.storetime[Simizer.counter] = endTime - Simizer.startTime;
                    Simizer.counter++;
                }
                else if(Simizer.requestCount%100==1){
                    Simizer.startTime = r.getArTime();
                }
                Simizer.requestCount++;
                System.out.println(r.toString() + ";" + (timestamp - r.getArTime()));
                */
            }
        }
    }

    @Override
    public void onRequestReceived(Node orig, Request r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Policy getPolicy() {
       return this.pol;
    }

   
}
