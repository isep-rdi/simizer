package simizer.network;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import simizer.Node;
import simizer.event.EventProducer;
import simizer.event.RequestReceivedEvent;
import simizer.laws.Law;
import simizer.requests.Request;

/**
 * Network simulation class. A set of nodes is associated to this network. The
 * class can be set up to use the arrival law to simulate node arrivals.
 *
 * @author Sylvain Lefebvre
 */
public class Network extends EventProducer {

  protected Law delayLaw;
  protected Map<Integer, Node> nodeMap;

  public Network(final Map<Integer, Node> nodeMap, Law delayLaw) {
    this.delayLaw = delayLaw;
    this.nodeMap = nodeMap;
  }

  public Network(Law delayLaw) {
    this.delayLaw = delayLaw;
    this.nodeMap = new TreeMap<>();
  }

  protected long getDelay() {
    return delayLaw.nextValue();
  }

  public void send(Node source, Node dest, Request r, long timestamp) {
    //1. create a message and set delay
    long delay = (long) this.getDelay();
    // System.out.println("Delay == " + delay);
    r.setDelay(r.getDelay() + delay);
    //2. send through sender
    Message m = new Message(source, dest, r, 0, delay);
    registerEvent(new RequestReceivedEvent(timestamp + delay, m, dest));
    //System.out.println("Sending from " + source + " to " + dest);
  }

  public Node getNode(Integer nodeId) {
    return nodeMap.get(nodeId);
  }

  public List<Node> getNodeList() {
    return new LinkedList(this.nodeMap.values());
  }

  public void putNode(Node n) {
    nodeMap.put(n.getId(), n);
  }

}
