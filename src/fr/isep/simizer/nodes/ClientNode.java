package fr.isep.simizer.nodes;

import fr.isep.simizer.laws.Law;
import fr.isep.simizer.network.Message;
import fr.isep.simizer.network.MessageReceiver;
import fr.isep.simizer.request.printers.PrettyRequestPrinter;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestFactory;
import fr.isep.simizer.requests.RequestPrinter;

/**
 * Simulates a client machine.
 * <p>
 * All of the instances are configured by three laws determining the resource
 * selection, "think time" between requests, and the session duration.
 * <p>
 * It is also possible to create {@code ClientNode} instances that send a
 * specified number of requests, rather than sending requests for some duration
 * of time.  See {@link #ClientNode(long, int)}.
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

  /** The printer used to format the output of the simulation. */
  private static RequestPrinter printer = new PrettyRequestPrinter(System.out);

  /**
   * Sets the {@code RequestPrinter} used to output the results.
   * <p>
   * The simulation allows fully-customizable output through the various {@link
   * RequestPrinter} classes.  A few are provided by default in the simulation,
   * but custom ones can easily be created for each usage case.
   *
   * @param requestPrinter the {@link RequestPrinter} to use
   */
  public static void setRequestPrinter(RequestPrinter requestPrinter) {
    printer = requestPrinter;
  }

  /** The timestamp when the client starts its execution. */
  private long startTime;

  /**
   * The timestamp when the client should finish its execution.
   * <p>
   * This value only has significance when {@code requestsToSend} is {@code
   * null}.
   */
  private long endTime;

  /** Whether or not the client has finished its execution. */
  private boolean ended = false;

  /** The number of requests sent by the client. */
  private int requestCount;

  /** The number of requests that the client should send before terminating. */
  private Integer requestsToSend = null;

  /** The address where the client should send its requests. */
  protected MessageReceiver serviceAddress;

  /**
   * Creates a client that sends requests until it expires.
   * <p>
   * Clients expire after an amount of time defined by the {@code durationLaw}.
   * See {@link #configureLaws(simizer.laws.Law, simizer.laws.Law,
   * simizer.laws.Law)} for a description of the laws.
   *
   * @param startTime the time when the client should begin its operations
   */
  public ClientNode(long startTime) {
    super();
    
    this.startTime = startTime;
  }

  /**
   * Creates a client that sends a specific number of requests.
   *
   * @param startTime the time when the client should begin its operations
   * @param requests the number of requests that the client should send before
   *            ending its execution
   */
  public ClientNode(long startTime, int requests) {
    this(startTime);

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

  /**
   * Changes the address where the client should send its {@code Request}s.
   *
   * @param receiver the {@link MessageReceiver} where the client should send
   *            its {@link Request}s
   */
  public void setServiceAddress(MessageReceiver receiver) {
    this.serviceAddress = receiver;
  }

  /**
   * Returns the timestamp when the client will finish.
   *
   * @return the timestamp when the client will finish
   */
  public long getEndTime() {
    return this.endTime;
  }

  /**
   * Returns whether or not the client has finished.
   * <p>
   * Note that a client may not be finished even if the clock for the simulation
   * has advanced beyond {@link #getEndTime()} because the client may still be
   * waiting for a response to a previous request.
   *
   * @return whether or not the client has finished
   */
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
    requestCount++;
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
    if (timestamp - r.getClientStartTimestamp() < 0) {
      System.out.println("PROBLEM");
    }

    r.setClientEndTimestamp(timestamp);
    printer.print(this, r);

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
