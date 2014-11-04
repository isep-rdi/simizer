/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.network;

import java.util.Iterator;
import java.util.List;
import simizer.ClientNode;
import simizer.LBNode;
import simizer.Node;
import simizer.event.ArrivalEvent;
import simizer.laws.Law;

/**
 *
 * @author slefebvr for ISEP
 */
public class ClientGenerator {
    
    private Law arrivalLaw;
    protected final LBNode frontend;
    private final int interval;
    private Network nw;
    private long endSim;
    private int lastId=0,maxUsers=0;
    
    public ClientGenerator(Network nw, Law arrivalLaw, int interval, LBNode frontend,long endSim, int maxUsers) {
        this.arrivalLaw = arrivalLaw;
        this.interval = interval;
        this.frontend = frontend;
        this.nw = nw;
        this.maxUsers = maxUsers;
        this.endSim = endSim;
        nw.registerEvent(new ArrivalEvent(0L,new Long(arrivalLaw.nextValue()),this));
        
    }
    
    private Long reinitNodes(List<Node> nwNodes, Long nb, final long timestamp) {
        Iterator<Node> itNode = nwNodes.iterator();
        Node n = itNode.next();
                
       while(itNode.hasNext() && nb > 0) {
            
            if(n instanceof ClientNode && ((ClientNode) n).getEnded()) {
                if(nb > 0) {
                   ((ClientNode) n).reinit(timestamp);
                    nb--;
                }
            }
            n = itNode.next();
        }
       return nb;
    }
    
     /**
     * This method is called on every interval;
     * It creates new nodes if necessary, and reuses ended nodes to preserve memory.
     * @param ae : ArrivalEvent 
     */
    public void onArrivalEvent(long timestamp, ArrivalEvent ae) {
        List<Node> nodeList = nw.getNodeList();
        int nbNodes = nodeList.size();
        if(timestamp < endSim) {
            Long nb = ae.getData();
            if(nbNodes > 0)
                nb = reinitNodes(nodeList, nb, timestamp);
            
            nb = (nb + nodeList.size()) > maxUsers ? maxUsers - nodeList.size() : nb;
            
            while(nb > 0) {
                createNode(lastId++, timestamp);
                nb--;
            }
            //System.out.println("Registering next arrival at " + (timestamp+interval));
                  
                    nw.registerEvent(
                    new ArrivalEvent(timestamp + interval
                    ,(long) arrivalLaw.nextValue()
                    , this));
        }
    }
    private void createNode(int id, long timestamp) {
        
         ClientNode cn = new ClientNode(id, nw,timestamp, frontend);
         nw.putNode(cn);
         cn.startClient();
                
    }
    
}
