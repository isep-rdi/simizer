package simizer.network;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import simizer.Node;
import simizer.event.EventProducer;
import simizer.event.MessageReceivedEvent;
import simizer.laws.Law;
import simizer.requests.Request;

/**
 * Simulates a Network.
 *
 * There is a set of nodes associated with this {@code Network}.  The class is
 * used to facilitate sending messages / requests between nodes.  It will apply
 * realistic delays to messages sent through the {@code Network}.
 *
 * @author Sylvain Lefebvre
 */
public class Network extends EventProducer {

  /** The delay that is added when sending a message across the network. */
  protected final Law delayLaw;

  /** The nodes contained in this network. */
  protected final Map<Integer, Node> nodeMap;

  /**
   * Initializes a network with the specified nodes and delay law.
   *
   * @param nodeMap a {@link Map} containing {@code nodeID} -> {@link Node}
   *            pairs that should be added
   * @param delayLaw a {@link Law} that defines the delays added when sending
   *            traffic through this {@code Network}
   */
  public Network(final Map<Integer, Node> nodeMap, Law delayLaw) {
    this.delayLaw = delayLaw;
    this.nodeMap = nodeMap;
  }

  /**
   * Initializes a network without adding any initial nodes.
   *
   * @param delayLaw a {@link Law} that defines the delays added when sending
   *            traffic through this {@code Network}
   */
  public Network(Law delayLaw) {
    this(new TreeMap<Integer, Node>(), delayLaw);
  }

  /**
   * Generates the random delay for the next sending.
   * <p>
   * This makes use of the specified {@link Law} to select a random delay when
   * sending the message.  This is to simulate network latency.
   *
   * @return the next delay (in milliseconds)
   */
  protected long generateNextDelay() {
    return delayLaw.nextValue();
  }

  /**
   * Sends the specified {@code Request} to the destination.
   *
   * @param source the {@code Node} where the request originated
   * @param destination the {@code MessageReceiver} where the request should be
   *            delivered
   * @param request the {@code Request} to deliver
   * @param timestamp the timestamp when the {@code Message} is sent
   */
  public void send(Node source, MessageReceiver destination, Request request,
        long timestamp) {

    // add the delay to the message
    long delay = this.generateNextDelay();
    request.setDelay(request.getDelay() + delay);

    // send it to the destination
    Message m = new Message(source, destination, request);
    registerEvent(new MessageReceivedEvent(timestamp + delay, m, destination));
  }

  /**
   * Look up a {@code Node} by its nodeID value.
   * <p>
   * This will only find nodes that are a part of this {@code Network}.
   * 
   * @param nodeId the ID of the {@code Node} to find
   * @return the {@code Node} with the specified ID
   */
  public Node getNode(Integer nodeId) {
    return nodeMap.get(nodeId);
  }

  /**
   * Returns a list of all the {@code Node} objects in this {@code Network}.
   * 
   * @return a list of all the {@code Node} objects in this {@code Network}
   */
  public List<Node> getNodeList() {
    return new LinkedList(this.nodeMap.values());
  }

  /**
   * Adds a {@code Node} to this {@code Network}.
   *
   * @param node the {@code Node} to add
   */
  public void putNode(Node node) {
    nodeMap.put(node.getId(), node);
  }

}
