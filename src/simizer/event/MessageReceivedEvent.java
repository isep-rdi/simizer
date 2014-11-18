package simizer.event;

import simizer.network.Message;
import simizer.network.MessageReceiver;

public class MessageReceivedEvent extends Event<Message, MessageReceiver> {

  public MessageReceivedEvent(long timestamp, Message m, MessageReceiver lrr) {
    super(timestamp, m, lrr);
  }

  @Override
  public void dispatch() {
    this.target.onMessageReceived(timestamp, this.data);
  }

}
