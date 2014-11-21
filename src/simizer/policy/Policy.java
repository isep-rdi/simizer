package simizer.policy;

import java.util.List;
import simizer.LBNode;
import simizer.Node;
import simizer.ServerNode;
import simizer.requests.Request;

public interface Policy {

  public void initialize(List<ServerNode> availableNodes, LBNode loadBalancer);

  public void addNode(Node node);

  public void removeNode(Node node);

  public Node loadBalance(Request request);

  public void printAdditionnalStats();

}
