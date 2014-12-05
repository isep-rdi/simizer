package fr.isep.simizer.nodes;

import fr.isep.simizer.Simulation;
import fr.isep.simizer.network.Message;
import fr.isep.simizer.network.MessageReceiver;
import fr.isep.simizer.network.Network;
import fr.isep.simizer.requests.Request;

/**
 * Represents a system able to communicate through a {@code Network}.
 * <p>
 * This could be a virtual machine (VM), a router, a client machine, or some
 * other type of machine.
 *
 * @author Sylvain Lefebvre
 */
public abstract class Node implements MessageReceiver {

  /** The counter for assigning Node IDs. */
  private static Integer nextID = 1;  // start from 1
  
  /** The Node ID for this {@code Node}. */
  private final Integer id;

  /** The {@code Network} associated with this {@code Node}. */
  private Network network = null;

  /** The most recent timestamp of the simulation known to this {@code Node}. */
  protected long clock;

  /**
   * Initializes a new instance with the specified ID and {@code Network}.
   */
  public Node() {
    this.id = nextID++;
  }

  /**
   * Returns the ID of this {@code Node}.
   * 
   * @return the ID of this {@code Node}
   */
  public Integer getId() {
    return this.id;
  }

  /**
   * Sets the {@code Network} associated with this node.
   *
   * @param network the {@link Network} to use
   */
  public void setNetwork(Network network) {
    this.network = network;
  }

  /**
   * Returns the {@code Network} associated with this node.
   * 
   * @return the {@code Network} associated with this node
   */
  public Network getNetwork() {
    return this.network;
  }

  /**
   * Sends a {@code Request} over the {@code Network}.
   * <p>
   * TODO: Make sure that we remove the clock variable.
   * <p>
   * This is a helper method for sending a request over a network.  The source
   * of the request is the current node.  The time that the {@link Message} is
   * sent is the current timestamp of the simulation.
   * 
   * @param request the {@link Request} to send
   * @param destination the {@link MessageReceiver} where the {@link Message}
   *            will be sent
   */
  public void send(Request request, MessageReceiver destination) {
    network.send(this, destination, request, clock);
  }

  /**
   * Starts the {@code Node}.
   * <p>
   * Called by the {@link Simulation} class to tell the {@code Node} to begin
   * its operations.  Subclasses must implement this method to include their
   * starting behavior.
   */
  public abstract void start();

  /**
   * Called when a {@code Request} is received by the system.
   *
   * @param source the {@link Node} that is the origin for the {@link Request}
   * @param request the {@link Request} that was sent
   */
  public abstract void onRequestReceived(Node source, Request request);

  @Override
  public void onMessageReceived(long timestamp, Message m) {
    this.clock = timestamp;
    onRequestReceived(m.getOrigin(), m.getRequest());
  }

}
