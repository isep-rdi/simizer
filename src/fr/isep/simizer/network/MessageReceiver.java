package fr.isep.simizer.network;

import fr.isep.simizer.Simulation;

/**
 * Applied to objects that can be the destination of {@code Message}s.
 * <p>
 * This interface should be applied to items in the {@link Simulation} that are
 * capable of receiving {@link Message} objects.  In the context of real
 * networks, these devices would be the devices with an IP address or a MAC
 * address.
 */
public interface MessageReceiver {

  /**
   * Processes {@code Message} objects that are received.
   * <p>
   * Implement this method to provide handling for {@link Message}s sent to the
   * object.  The timestamp of delivery and {@link Message} object are provided.
   *
   * @param timestamp the timestamp when the {@link Message} is delivered
   * @param message the {@link Message} being delivered
   */
  public void onMessageReceived(long timestamp, Message message);

}
