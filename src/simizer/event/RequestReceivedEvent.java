package simizer.event;

import simizer.network.Message;
import simizer.requests.RequestReceiver;

public class RequestReceivedEvent extends Event<Message, RequestReceiver> {

  public RequestReceivedEvent(long timestamp, Message m, RequestReceiver lrr) {
    super(timestamp, m, lrr);
  }

  @Override
  public void dispatch() {
    this.target.onRequestReception(timestamp, this.data);
  }

}
