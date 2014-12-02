package fr.isep.simizer.event;

import fr.isep.simizer.network.Message;
import fr.isep.simizer.network.MessageReceiver;

public class MessageReceivedEvent extends Event<Message, MessageReceiver> {

  public MessageReceivedEvent(long timestamp, Message m, MessageReceiver lrr) {
    super(timestamp, m, lrr);
  }

  @Override
  public void dispatch() {
    this.target.onMessageReceived(timestamp, this.data);
  }

}
