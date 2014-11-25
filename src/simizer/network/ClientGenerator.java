package simizer.network;

import java.util.Iterator;
import java.util.List;
import simizer.ClientNode;
import simizer.Node;
import simizer.Simulation;
import simizer.event.ArrivalEvent;
import simizer.laws.Law;

/**
 * Automatically creates clients according to a probability distribution.
 * <p>
 * The {@code ClientGenerator} is useful for automatically creating clients in
 * the simulation.  It will generate clients according to a probability
 * distribution at a set interval.
 * 
 * @author slefebvr for ISEP
 */
public class ClientGenerator {

  private final Simulation simulation;
  
  private final Law arrivalLaw;
  private final Node frontend;
  private final int interval;
  private final Network network;
  
  private int lastId = 0;
  private int maxUsers = 0;

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

    List<Node> nodeList = network.getNodeList();

    // reuse any that are possible
    nodesToCreate = reinitNodes(nodeList, nodesToCreate, timestamp);

    // make sure that we don't exceed maxUsers
    nodesToCreate = Math.min(maxUsers - nodeList.size(), nodesToCreate);

    while (nodesToCreate > 0) {
      createNode(lastId++, timestamp);
      nodesToCreate--;
    }

    scheduleNextArrival(timestamp + interval);
  }

  /**
   * Reinitializes finished nodes to save memory.
   * <p>
   * This will reinitialize up to {@code number} nodes, and it will return the
   * number that still need to be initialized.  (This is in place so that the
   * calling code can create new instances for the remaining nodes.)
   * 
   * @param nodes the nodes that we should try to reuse
   * @param number the number of nodes to create
   * @param timestamp the current timestamp of the simulation
   * @return the number of nodes that still need to be initialized
   */
  private long reinitNodes(List<Node> nodes, long number, final long timestamp) {
    Iterator<Node> iterator = nodes.iterator();

    while (iterator.hasNext() && number > 0) {
      Node node = iterator.next();
      if (!(node instanceof ClientNode)) {
        continue;
      }
      ClientNode client = (ClientNode) node;

      if (client.getEnded()) {
        client.reinit(timestamp);
        number--;
      }
    }

    return number;
  }

  /**
   * Creates a new {@code ClientNode} for the simulation.
   * 
   * @param id the ID of the node to create
   * @param timestamp the timestamp when the client should start its behavior
   */
  private void createNode(int id, long timestamp) {
    ClientNode client = new ClientNode(id, network, timestamp);
    client.setServiceAddress(frontend);
    simulation.toNetworkAddNode(network, client);
    client.start();
  }

}
