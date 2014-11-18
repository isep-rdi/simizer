package simizer.network;

import simizer.Node;
import simizer.requests.Request;

/**
 * Encapsulates information to send a {@code Request} between {@code Node}s.
 * <p>
 * The {@code Message} class can be thought of as a kind of networking packet.
 * It contains source and destination information, as well as some sort of
 * payload.  When delivered, these values can be retrieved and referenced (for
 * example, to handle the request and reply to the requestor).
 */
public class Message {

  /** The source of the {@code Message}. */
  private final Node origin;

  /** The destination of the {@code Message}. */
  private final MessageReceiver destination;

  /** The request contained in the {@code Message}. */
  private final Request request;

  public Message(Node origin, MessageReceiver destination, Request request) {
    this.origin = origin;
    this.destination = destination;
    this.request = request;
  }

  /**
   * Returns the {@code Node} where the {@code Message} originated.
   *
   * @return the {@code Node} where the {@code Message} originated
   */
  public Node getOrigin() {
    return origin;
  }

  /**
   * Returns the {@code Node} where the {@code Message} will be delivered.
   * 
   * @return the {@code Node} where the {@code Message} will be delivered
   */
  public MessageReceiver getDestination() {
    return destination;
  }

  /**
   * Returns the {@code Request} contained in this {@code Message}.
   *
   * @return the {@code Request} contained in this {@code Message}
   */
  public Request getRequest() {
    return request;
  }

}
