/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.policy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import simizer.LBNode;
import simizer.Node;
import simizer.ServerNode;
import simizer.requests.Request;

/**
 *
 * @author isep
 */
public class LeastLoadedPolicy implements Policy, PolicyAfterCallback {
    private List<ServerNode> nodeList;
    private final Map<Integer, Integer> nodeReq = new ConcurrentHashMap<Integer,Integer>();

    @Override
    public void printAdditionnalStats() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize(List<ServerNode> availableNodes, LBNode f) {
       nodeList = new LinkedList(availableNodes);
       for(Node n : nodeList) {
           nodeReq.put(n.getId(), 0);
       }
       f.registerAfter(this);
    }

    @Override
    public void addNode(Node n) {
        nodeList.add((ServerNode)n);
    }

    @Override
    public void removeNode(Node n) {
        nodeList.remove(n);
    }

    @Override
    public Node loadBalance(Request r) {
        Node tgt = nodeList.get(0);
        
        for(Node n: nodeList) {
            if(nodeReq.get(n.getId())< nodeReq.get(tgt.getId()) ) {
                tgt = n;
                
            }
        }
        nodeReq.put(tgt.getId(), nodeReq.get(tgt.getId()) +1 );
        //System.out.println(tgt.getId() + " requests " + tgt.getRequestCount(0) + " " + r.getId());
        return tgt;
    }

    @Override
    public void receivedRequest(Node n, Request r) {
        nodeReq.put(n.getId(), nodeReq.get(n.getId()) -1 );
    }
    
}
