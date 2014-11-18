package simizer;

import simizer.processor.Processor;
import java.util.List;
import simizer.event.Channel;
import simizer.event.Event;
import simizer.event.EventProducer;
import simizer.event.IEventProducer;
import simizer.network.Message;
import simizer.requests.Request;
import simizer.requests.RequestFinisher;
import simizer.storage.Cache;
import simizer.storage.Resource;
import simizer.storage.ResourceFactory;
import simizer.storage.StorageElement;

/**
 * Server node simulation class.
 *
 * @author slefebvr
 *
 */
public class ServerNode extends Node
    implements IEventProducer, RequestFinisher {

  static private ResourceFactory rf;
  static private long _hits = 0;

  static long getHits() {
    return _hits;
  }

  // delegate for IEventProducer
  private final EventProducer ep = new EventProducer();

  private Cache memory;
  private long memUtil = 0, memSize = 0;
  private int counter = 0;
  protected StorageElement disk;
  private Node frontendNode;
  private Processor processor = null;
  private double cost;

  public double getCost() {
    return this.cost;
  }

  public void setStorage(StorageElement se) {
    this.disk = se;
  }

  public ServerNode(int id, long memSize, int qSize, double cost,
        final StorageElement se, final Processor p) {

    super(id, null);
    this.memory = new Cache(memSize, 0);
    this.memSize = memSize;
    this.disk = se;
    this.processor = p;
    this.cost = cost;
    init();
  }

  public ServerNode(int id, long memSize, int nbProc, int qSize, double cost,
        double nbMips, final StorageElement se) {

    super(id, null);
    this.memory = new Cache(memSize, 0);
    this.memSize = memSize;
    this.disk = se;
    this.cost = cost;
  }

  private void init() {
    if (processor != null) {
      processor.setNodeInstance(this);
    }
  }

  public synchronized int getRequestCount() {
    //cleanup(artime);
    return counter;
  }

  public long getRequestAccessTime(Request r) {
    long accessTime = 0;
    for (Integer rId : r.getResources()) {
      if (memory.contains(rId)) {
        memory.updateCache(rId);
        _hits++;
        accessTime += memory.getDelay(rId);
      } else {
        // if ressource is not in memory
        // check if memory available
        // implement FIFO queue for memory eviction (subclass StorageElement);
        accessTime += disk.getDelay(rId);
      }
    }
    return accessTime;
  }

  public int getCapacity() {
    StorageElement se = getStorageElement();
    long total = 0, nb = 0;

    for (Resource r : se.getResources()) {
      total += r.size();
      nb++;
    }
    System.out.println(" total = " + total
        + " nb " + nb
        + " memSize " + this.memSize);
    long moy = total / nb;
    return (int) (this.memSize / moy);
  }

  public long getMemorySize() {
    return this.memSize;
  }

  public int getNbCores() {
    return this.processor.getNbCores();
  }

  public void store(Request r) {
    disk.write(r.getResources());
  }

  public boolean isstored(Request r) {
    boolean result = disk.contains(r.getResources());
    return result;
  }

  public StorageElement getStorageElement() {
    return disk;
  }

  public static void setRessourceFactory(ResourceFactory resFac) {
    rf = resFac;
  }

  // based on rough core 2 duo evaluation

  public static double getCoef(int nbReq, int nbCores) {

    final double base = 1.068, overhead = 0.184;
    final double coef = 1 + ((base / nbCores * nbReq) + overhead);

    if (coef < 1.0) {
      return (base);
    }

    return (coef);
  }

  public void setFrontendNode(final LBNode lbn) {
    this.frontendNode = lbn;
  }

  @Override
  public void onRequestEnded(long timestamp, Request r) {

    r.setFinishTime(timestamp);
    processor.executeNextRequest(timestamp);
    for (Integer rId : r.getResources()) {
      if (memory.contains(rId)) {
        memory.updateCache(rId);
      } else {
                // if ressource is not in memory
        // check if memory available
        // implement FIFO queue for memory eviction (subclass StorageElement);
        Resource res = disk.read(rId);
        //size check
        if (res != null) {
          memory.write(res);
        } else {
          r.setError(r.getError() + 1);
          System.out.println("Uh oh " + rId);
        }
      }
    }

    nw.send(this, frontendNode, r, timestamp);
    counter--;

  }

  @Override
  public void onMessageReceived(long timestamp, Message m) {
    Request r = m.getRequest();
    r.setNode(id);
    /**
     * @TODO Check list of resource: For each resource in request check if
     * present in cache or disk disk.getRessource and memory.getResource for(int
     * resId: m.getRequest().getResources()) { Resource r = disk.read(resId); }
     *
     * If resource not in cache or disk, send request to next node.
     * nw.getNodeList(); this.nw.getNode(id+1); this.memory.writeToCache(null);
     */
    List<Node> nodeList = nw.getNodeList();

//         for(Integer rId:r.getResources()) {
//            if (!memory.contains(rId) && !disk.contains(rId) ) {    
//                nw.getNode((id + 1) % nodeList.size());
//            }
//            
//        }
    processor.onRequestReception(timestamp, m.getRequest());
    counter++;

  }

  @Override
  public Channel getOutputChannel() {
    return ep.getOutputChannel();
  }

  @Override
  public void registerEvent(Event evt) {
    ep.registerEvent(evt);
  }

  @Override
  public boolean cancelEvent(Event evt) {
    return ep.cancelEvent(evt);
  }

  @Override
  public void setChannel(Channel c) {
    ep.setChannel(c);
  }

  public Cache getCache() {
    return this.memory;
  }

  public synchronized void setRequestCount(int counter) {
    this.counter = counter;
  }

  public Node getFrontendNode() {
    return this.frontendNode;
  }

  @Override
  public void onRequestReceived(Node orig, Request r) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void start() {}

}
