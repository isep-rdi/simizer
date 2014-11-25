package simizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import simizer.event.Channel;
import simizer.event.EventDispatcher;
import simizer.event.IEventProducer;
import simizer.network.Network;

/**
 * Utility class for setting up and starting a simulation.
 *
 * @author isep
 */
public class Simulation {

  private final Set<Network> networks = new HashSet<>();
  private final Map<Integer, Node> nodes = new HashMap<>();

  private final Channel eventChannel = new Channel();
  
  private final long endSim;

  /**
   * Configures the end time of the simulations
   *
   * @param endSim
   */
  public Simulation(long endSim) {
    this.endSim = endSim;
  }

  /**
   * Returns the timestamp when the simulation should end.
   *
   * @return the timestamp when the simulation should end
   */
  public long getEndTimestamp() {
    return endSim;
  }

  /**
   * Loads entities from the specified configuration file.
   *
   * @param nodeFilePath
   */
  public void setupSimEntities(String nodeFilePath) {}

  public void runSim() throws Exception {
    if (eventChannel == null) {
      throw new Exception("Event Channel not ready!");
    }

    for (Node node : nodes.values()) {
      node.start();
    }

    EventDispatcher ed = new EventDispatcher(eventChannel);
    ed.run();
  }

  public void addNetwork(Network network) {
    if (networks.add(network)) {
      network.setChannel(eventChannel);
    }
  }

  public void addNode(Node node) {
    int id = node.getId();
    if (!nodes.containsKey(id)) {
      nodes.put(id, node);

      if (node instanceof IEventProducer) {
        ((IEventProducer) node).setChannel(eventChannel);
      }
    }
  }

  /**
   * Adds the specified node to the simulation and specified network.
   * <p>
   * If {@code node} or {@code network} has not yet been added to the
   * simulation, they will first be added to the simulation before creating the
   * association.
   *
   * @param network the network where this node should submit its events
   * @param node the node being added to the simulation and network
   */
  public void toNetworkAddNode(Network network, Node node) {
    // make sure that they have both been added
    addNetwork(network);
    addNode(node);

    // create the association between the two
    network.putNode(node);
    node.setNetwork(network);
  }

  public void toNetworkAddNodes(Network network, Node ... nodes) {
    for (Node node : nodes) {
      toNetworkAddNode(network, node);
    }
  }
}
