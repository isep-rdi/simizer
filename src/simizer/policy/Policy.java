/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simizer.policy;

import java.util.List;
import simizer.LBNode;
import simizer.Node;
import simizer.ServerNode;
import simizer.requests.Request;

/**
 *
 * @author isep
 */
public interface Policy {
     public void initialize(List<ServerNode> availableNodes, LBNode lbn);
     public void addNode(Node n);
     public void removeNode(Node n);
     public Node loadBalance(Request r);
     //ublic void receivedRequest(Node n, Request r);
     public void printAdditionnalStats();

    //public void addNode(Node orig);
}
