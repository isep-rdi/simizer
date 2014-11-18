package simizer;

import simizer.network.Message;
import simizer.network.Network;
import simizer.requests.Request;
import simizer.network.MessageReceiver;

/**
 * The Node Class represents a system being able to communicate through a
 * network it can be a VM of a router or another type of machine. This
 * architecture simulation class keeps track of the timestamps in order to call
 * the actions at the right moment.
 *
 * @author Sylvain Lefebvre
 *
 * comment
 */
public abstract class Node implements MessageReceiver {

  protected int id;
  protected Network nw;

  protected long clock;

  public Node(int id, Network nw) {
    this.id = id;
    this.nw = nw;

  }

  public Integer getId() {
    return this.id;
  }

  public void setNetwork(Network nw) {
    this.nw = nw;
  }

  public void send(Request req, Node dest) {
    nw.send(this, dest, req, clock);
  }

  public Network getNetwork() {
    return this.nw;
  }

  public void start() {
  }

  @Override
  public void onMessageReceived(long timestamp, Message m) {
    this.clock = timestamp;
    onRequestReceived(m.getOrigin(), m.getRequest());
  }

  public abstract void onRequestReceived(Node orig, Request r);

}
