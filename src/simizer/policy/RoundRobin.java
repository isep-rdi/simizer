/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.policy;

import java.util.ArrayList;
import java.util.List;
import simizer.LBNode;
import simizer.Node;
import simizer.ServerNode;
import simizer.requests.Request;

/**
 *
 * @author isep
 */
public class RoundRobin implements Policy {
    List<ServerNode> availableNodes = new ArrayList<ServerNode>();
    protected int rrIndex = 0;
    protected int nodeCount = 0;
    public RoundRobin() {
        rrIndex = 0;
    }
    
    @Override
    public Node loadBalance(Request r) {
        //System.out.println(rrIndex + " " + availableNodes.size() + "==" + (rrIndex%availableNodes.size()) );
        return availableNodes.get(rrIndex++ % availableNodes.size());
    }

    

    @Override
    public void printAdditionnalStats() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize(List<ServerNode> availableNodes, LBNode f) {
        this.availableNodes = availableNodes;
    }

    @Override
    public void addNode(Node n) {
        ServerNode sn= (ServerNode)n;
        synchronized(this) {
            if(!availableNodes.contains(sn)) {
                availableNodes.add(sn);
            }
        }
    }

    @Override
    public void removeNode(Node n) {
        ServerNode sn= (ServerNode)n;
        synchronized(this) {
            if(availableNodes.contains(sn)) {
                availableNodes.remove(sn);
            }
        }
    }
    
}
