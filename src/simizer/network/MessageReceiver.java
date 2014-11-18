package simizer.network;

/**
 * Interface for request receiving entities. Can be part of @see Network and
 * @see Message
 */
public interface MessageReceiver {

  public void onMessageReceived(long timestamp, Message m);

}
