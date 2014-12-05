package fr.isep.simizer;

import fr.isep.simizer.event.Channel;
import fr.isep.simizer.event.EventDispatcher;
import fr.isep.simizer.event.IEventProducer;
import fr.isep.simizer.network.Network;
import fr.isep.simizer.nodes.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
   * Runs the simulation, returning after completion.
   * <p>
   * This method blocks until the {@code Simulation} has finished running.
   */
  public void runSim() {
    for (Node node : nodes.values()) {
      node.start();
    }

    EventDispatcher ed = new EventDispatcher(eventChannel);
    ed.run();
  }

  /**
   * Adds the specified {@code Network}.
   * <p>
   * {@link Network}s will not be added twice, so it is fine to call this method
   * multiple times with the same {@link Network}.
   *
   * @param network the {@link Network} to add to the {@code Simulation}
   */
  public void addNetwork(Network network) {
    if (networks.add(network)) {
      network.setChannel(eventChannel);
    }
  }

  /**
   * Adds the specified {@code Node}.
   * <p>
   * {@link Node}s will not be added twice, so it is fine to call this method
   * multiple times with the same {@link Node}.
   *
   * @param node the {@link Node} to add
   */
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

  /**
   * Adds the specified {@code Node}s to the {@code Network}.
   * <p>
   * This method calls {@link #toNetworkAddNode(Network, Node)} with each of the
   * specified {@link Node}s.
   *
   * @param network the {@link Network} where the {@link Node}s should be added
   * @param nodes a list of {@link Node}s to add to the {@code Simulation}
   */
  public void toNetworkAddNodes(Network network, Node ... nodes) {
    for (Node node : nodes) {
      toNetworkAddNode(network, node);
    }
  }
}
