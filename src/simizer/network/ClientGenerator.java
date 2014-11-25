package simizer.network;

import java.util.Iterator;
import java.util.List;
import simizer.ClientNode;
import simizer.Node;
import simizer.event.ArrivalEvent;
import simizer.laws.Law;

/**
 *
 * @author slefebvr for ISEP
 */
public class ClientGenerator {

  private final Law arrivalLaw;
  private final Node frontend;
  private final int interval;
  private final Network network;
  private final long endSim;
  private int lastId = 0;
  private int maxUsers = 0;

  public ClientGenerator(Network nw, Law arrivalLaw, int interval, Node frontend, long endSim, int maxUsers) {
    this.arrivalLaw = arrivalLaw;
    this.interval = interval;
    this.frontend = frontend;
    this.network = nw;
    this.maxUsers = maxUsers;
    this.endSim = endSim;
    nw.registerEvent(new ArrivalEvent(0L, (long) arrivalLaw.nextValue(), this));
  }

  private Long reinitNodes(List<Node> nodes, long number, final long timestamp) {
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
   * This method is called on every interval; It creates new nodes if necessary,
   * and reuses ended nodes to preserve memory.
   *
   * @param event : ArrivalEvent
   */
  public void onArrivalEvent(long timestamp, ArrivalEvent event) {
    List<Node> nodeList = network.getNodeList();
    int nbNodes = nodeList.size();
    if (timestamp < endSim) {
      long nodesToCreate = event.getData();

      // reuse any that are possible
      nodesToCreate = reinitNodes(nodeList, nodesToCreate, timestamp);

      // make sure that we don't exceed maxUsers
      nodesToCreate = Math.min(maxUsers - nodeList.size(), nodesToCreate);

      while (nodesToCreate > 0) {
        createNode(lastId++, timestamp);
        nodesToCreate--;
      }

      network.registerEvent(
          new ArrivalEvent(timestamp + interval, (long) arrivalLaw.nextValue(), this));
    }
  }

  private void createNode(int id, long timestamp) {
    ClientNode client = new ClientNode(id, network, timestamp);
    client.setServiceAddress(frontend);
    network.putNode(client);
    client.start();
  }

}
