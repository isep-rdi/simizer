package simizer;

import simizer.laws.Law;
import simizer.network.Message;
import simizer.network.Network;
import simizer.requests.Request;
import simizer.requests.RequestFactory;

/**
 * Simulates a client machine.
 * <p>
 * All of the instances are configured by three laws determining the resource
 * selection, "think time" between requests, and the session duration.
 * <p>
 * It is also possible to create {@code ClientNode} instances that send a
 * specified number of requests, rather than sending requests for some duration
 * of time.
 *
 * @author Sylvain Lefebvre
 */
public class ClientNode extends Node {

  private static Law requestLaw;
  private static Law thinkTimeLaw;
  private static Law durationLaw;
  private static RequestFactory requestFactory;

  /**
   * Sets the {@code RequestFactory} used by the clients.
   *
   * @param factory the {@link RequestFactory} for the clients to use
   */
  public static void configureRequestFactory(final RequestFactory factory) {
    ClientNode.requestFactory = factory;
  }

  /**
   * Sets the {@code Law}s determining client behavior.
   *
   * @param requestLaw how the client decides which resource to request
   * @param thinkTimeLaw the amount of time from when the client receives a
   *            response to when the client sends its next request
   * @param durationLaw the total amount of time for which the client will exist
   */
  public static void configureLaws(final Law requestLaw, final Law thinkTimeLaw,
        final Law durationLaw) {

    ClientNode.durationLaw = durationLaw;
    ClientNode.requestLaw = requestLaw;
    ClientNode.thinkTimeLaw = thinkTimeLaw;
  }

  private long startTime;
  private long endTime;
  private boolean ended = false;
  private int requestCount;
  private final int MAX_REQ = 10;

  protected Node serviceAddress;

  /**
   * Creates a new Client with its arrival time and network, Automatically
   * computes the session duration for the client.
   *
   * @param id - long
   * @param startTime
   * @param network
   */
  public ClientNode(int id, Network network, long startTime) {
    super(id, network);
    this.startTime = startTime;
  }

  /**
   * Reinitialize the client for additional use in the simulation.
   * <p>
   * This can avoid needing to instantiate new instances of the class.
   *
   * @param startTime the new starting time
   */
  public void reinit(long startTime) {
    this.startTime = startTime;
    start();
  }

  public void setServiceAddress(Node serviceNode) {
    this.serviceAddress = serviceNode;
  }

  public long getEndTime() {
    return this.endTime;
  }

  public boolean getEnded() {
    return this.ended;
  }

  /**
   * Starts the client by scheduling its first request at startTime.
   */
  @Override
  public void start() {
    // determine the end time of the client
    this.endTime = startTime + durationLaw.nextValue();
    requestCount = 0;
    scheduleNextRequest(this.startTime);
  }

  /**
   * Schedules the next {@code Request}.
   *
   * @param timestamp the current timestamp of the simulation
   */
  private void scheduleNextRequest(long timestamp) {
    long nextTime = thinkTimeLaw.nextValue();
    int templateId = requestLaw.nextValue();
    Request request = requestFactory.getRequest(timestamp + nextTime, templateId);
    getNetwork().send(this, serviceAddress, request, timestamp + nextTime);
  }

  /**
   * Prints the response, and then schedules the next {@code Request}.
   * <p>
   * This method will only schedule the next {@link Request} if it has not yet
   * reached its completion.
   *
   * @param timestamp the current timestamp of the simulation
   * @param message the message that was received
   */
  @Override
  public void onMessageReceived(long timestamp, Message message) {
    Request r = message.getRequest();
    if (timestamp - r.getArTime() < 0) {
      System.out.println("PROBLEM");
    }

    System.out.println(r.toString()
        + ";" + (timestamp - r.getArTime())
        + ";" + getId());
    
    //if(timestamp < endTime)
    requestCount++;
    if (requestCount < MAX_REQ) {
      scheduleNextRequest(timestamp);
    } else {
      ended = true;
    }
  }

  @Override
  public void onRequestReceived(Node orig, Request r) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
