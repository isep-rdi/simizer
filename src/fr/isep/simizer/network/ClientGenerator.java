package fr.isep.simizer.network;

import fr.isep.simizer.Simulation;
import fr.isep.simizer.event.ArrivalEvent;
import fr.isep.simizer.laws.Law;
import fr.isep.simizer.nodes.ClientNode;
import fr.isep.simizer.nodes.Node;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Automatically creates clients according to a probability distribution.
 * <p>
 * The {@code ClientGenerator} is useful for automatically creating clients in
 * the simulation.  It will generate clients according to a probability
 * distribution at a set interval.
 * <p>
 * It also has the ability to limit the number of simultaneous clients.
 * 
 * @author slefebvr for ISEP
 */
public class ClientGenerator {

  private final Simulation simulation;
  
  private final Law arrivalLaw;
  private final Node frontend;
  private final int interval;
  private final Network network;
  
  private int maxUsers = 0;

  private final List<ClientNode> activeClients = new LinkedList<>();

  /**
   * Initializes a new {@code ClientGenerator}.
   * <p>
   * Creates a new {@link ClientGenerator} that is ready to spawn clients.
   *
   * @param simulation the {@link Simulation} where the clients should be added
   * @param network the {@link Network} that the clients should use
   * @param arrivalLaw the {@link Law} describing the number of clients that
   *            arrive every {@code interval} milliseconds
   * @param interval the number of milliseconds between the timestamps when
   *            clients arrive
   * @param frontend the {@link Node} that the clients should use as their
   *            service address
   * @param maxUsers the maximum number of <strong>simultaneous</strong> clients
   *            that this generator will have spawned at any point in time.
   *            Once a client has finished, the generator will be able to create
   *            another in its place.  However, if this parameter prevents a
   *            client from being created at some point, the generator will
   *            <strong>not</strong> automatically go back to spawn a client
   *            when a space opens up.  Having a space open up simply means that
   *            another client <em>could</em> be created in the future.
   */
  public ClientGenerator(Simulation simulation, Network network, Law arrivalLaw,
      int interval, Node frontend, int maxUsers) {

    this.simulation = simulation;
    this.arrivalLaw = arrivalLaw;
    this.interval = interval;
    this.frontend = frontend;
    this.network = network;
    this.maxUsers = maxUsers;

    scheduleNextArrival(0);
  }

  /**
   * Schedules the next client arrival event.
   *
   * @param timestamp when the event should be delivered
   */
  private void scheduleNextArrival(long timestamp) {
    // don't deliver the event if it would occur after the end of the simulation
    if (timestamp < simulation.getEndTimestamp()) {
      network.registerEvent(
          new ArrivalEvent(timestamp, (long) arrivalLaw.nextValue(), this));
    }
  }

  /**
   * Triggered every {@code interval} ms to create new nodes.
   * <p>
   * It will reuse nodes when possible.
   *
   * @param timestamp the timestamp when the event is delivered
   * @param event the event that occurred
   */
  public void onArrivalEvent(long timestamp, ArrivalEvent event) {
    long nodesToCreate = event.getData();

    // Remove any finished ClientNode instances
    // This will let us create new ones.
    Iterator<ClientNode> iterator = activeClients.iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getEnded()) {
        iterator.remove();
      }
    }

    // Now that we have cleared out any finished clients, let's create new
    // ClientNode objects, making sure that we don't pass the maximum allowed
    // number of simultaneous clients.
    nodesToCreate = Math.min(maxUsers - activeClients.size(), nodesToCreate);

    while (nodesToCreate > 0) {
      createNode(timestamp);
      nodesToCreate--;
    }

    scheduleNextArrival(timestamp + interval);
  }

  /**
   * Creates a new {@code ClientNode} for the simulation.
   * 
   * @param timestamp the timestamp when the client should start its behavior
   */
  private void createNode(long timestamp) {
    ClientNode client = new ClientNode(timestamp);
    client.setServiceAddress(frontend);
    simulation.toNetworkAddNode(network, client);
    activeClients.add(client);
    client.start();
  }

}
