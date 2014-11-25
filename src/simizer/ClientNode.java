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
  private Integer requestsToSend = null;

  protected Node serviceAddress;

  /**
   * Creates a client that sends requests until it expires.
   * <p>
   * Clients expire after an amount of time defined by the {@code durationLaw}.
   * See {@link #configureLaws(simizer.laws.Law, simizer.laws.Law,
   * simizer.laws.Law)} for a description of the laws.
   *
   * @param id the ID of the client
   * @param network the {@link Network} that the client should use
   * @param startTime the time when the client should begin its operations
   */
  public ClientNode(Integer id, Network network, long startTime) {
    super(id, network);
    this.startTime = startTime;
  }

  /**
   * Creates a client that sends a specific number of requests.
   *
   * @param id the ID of the client
   * @param network the {@link Network} that the client should use
   * @param startTime the time when the client should begin its operations
   * @param requests the number of requests that the client should send before
   *            ending its execution
   */
  public ClientNode(Integer id, Network network, long startTime, int requests) {
    this(id, network, startTime);
    this.requestsToSend = requests;
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
    if (requestsToSend != null) {
      // reset the request counter if that is the mode of the client
      requestCount = 0;
    } else {
      // determine the end time of the client (for this mode)
      this.endTime = startTime + durationLaw.nextValue();
    }
    
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

    if (requestsToSend != null) {
      // if the number of requests to send is set, use that method
      if (requestCount >= requestsToSend) {
        ended = true;
      }
    } else {
      // otherwise, we want to rely on the calculated endTime
      if (timestamp >= endTime) {
        ended = true;
      }
    }

    if (!ended) {
      scheduleNextRequest(timestamp);
    }
  }

  @Override
  public void onRequestReceived(Node orig, Request r) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
