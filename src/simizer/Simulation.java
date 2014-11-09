package simizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import simizer.event.Channel;
import simizer.event.EventDispatcher2;
import simizer.event.IEventProducer;
import simizer.network.ClientGenerator;
import simizer.network.Network;

/**
 * Utility class for setting up and starting a simulation.
 *
 * @author isep
 */
public class Simulation {

  private final Map<String, Network> netMap = new HashMap<>();
  private final Map<Integer, Node> nodeMap = new HashMap<>();
  private final List<ClientNode> clientList = new LinkedList<>();

  private final Channel eventChannel = new Channel();
  
  private ClientGenerator cg;
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
   * Loads entities from the specified configuration file.
   *
   * @param nodeFilePath
   */
  public void setupSimEntities(String nodeFilePath) {}

  public void runSim() throws Exception {
    if (eventChannel == null) {
      throw new Exception("Event Channel not ready!");
    }

    for (Node n : nodeMap.values()) {
      /**
       * @TODO quick fix for now, must create a start() method in Node class.
       */

      if (n instanceof VM) {
        ((VM) n).startVM();
      }
    }

    Iterator<ClientNode> clientIt = clientList.listIterator();
    while (clientIt.hasNext()) {
      clientIt.next().startClient();
    }

    EventDispatcher2 ed = new EventDispatcher2(eventChannel);
    ed.run();
  }

  /**
   * Adds the given network to simulation entities
   *
   * @param name
   * @param net
   */
  public void addNetwork(String name, Network net) {
    net.setChannel(eventChannel);
    netMap.put(name, net);
  }

  /**
   * Adds the node to the simulation by adding it to the specified network. Sets
   * up the channel for producing events if needed.
   *
   * @param n
   * @param networkName
   */
  public void addNodeToNet(Node n, String networkName) {
    if (!nodeMap.containsKey(n.getId())) {
      nodeMap.put(n.getId(), n);
    }

    Network net = netMap.get(networkName);
    net.putNode(n);
    n.setNetwork(net);
    if (n instanceof IEventProducer) {
      ((IEventProducer) n).setChannel(eventChannel);
    }
  }

  /**
   *
   * @param cn
   */
  public void addClient(ClientNode cn) {
    if (!nodeMap.containsKey(cn.getId())) {
      nodeMap.put(cn.getId(), cn);
    }
    clientList.add(cn);
  }
}
