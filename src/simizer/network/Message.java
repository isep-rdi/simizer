package simizer.network;

import simizer.Node;
import simizer.requests.Request;

public class Message {

  private Node origin;
  private Node dest;
  private Request r;
  private long size;
  private long delay;

  public Message(Node origin, Node dest, Request r, long size, long delay) {
    this(origin, dest, r, size);
    this.delay = delay;
  }

  public Message(Node origin, Node dest, Request r, long size) {
    this(origin, dest, r);
    this.size = size;
  }

  public Message(Node origin, Node dest, Request r) {
    this.origin = origin;
    this.dest = dest;
    this.r = r;
  }

  public long getDelay() {
    return delay;
  }

  /**
   * @return the origin
   */
  public Node getOrigin() {
    return origin;
  }

  /**
   * @return the dest
   */
  public Node getDest() {
    return dest;
  }

  /**
   * @return the r
   */
  public Request getRequest() {
    return r;
  }

  /**
   * @return the size
   */
  public long getSize() {
    return size;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

}
