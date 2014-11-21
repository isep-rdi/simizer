package simizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import simizer.event.Channel;
import simizer.event.EventDispatcher;
import simizer.laws.GaussianLaw;
import simizer.laws.Law;
import simizer.network.ClientGenerator;
import simizer.network.Network;
import simizer.policy.Policy;
import simizer.requests.Request;
import simizer.requests.RequestFactory;
import simizer.storage.ResourceFactory;
import simizer.storage.StorageElement;
import simizer.utils.SimizerUtils;

/**
 *
 * @author Sylvain Lefebvre
 */
public class Simizer {

  private static int NB_REQ = 100;
  private static int interval;

  static int counter = 0;
  static int requestCount = 0;
  static long[] storetime = new long[11];
  static long startTime = 0;
  static long endTime = 0;

  public static void main(String[] args) {
    if (args.length >= 1) {
      handleCommand(args[0], Arrays.copyOfRange(args, 1, args.length));
    } else {
      printUsage("main");
    }
  }

  public static void printUsage(String name) {
    try {
      InputStream stream = ClassLoader.getSystemResourceAsStream(name + ".txt");

      byte[] buffer = new byte[1024];
      while (true) {
        int count = stream.read(buffer);
        if (count <= 0) break;
        System.out.write(buffer, 0, count);
      }

      stream.close();
    } catch (Exception e) {
      System.err.println("Could not print usage information.");
    }
  }

  private static void handleCommand(String command, String[] args) {
    switch (command) {
      case "generate":
        if (args.length < 5) {
          printUsage("generate");
        } else {
          generate(args);
        }
        break;

      case "run":
        if (args.length < 5) {
          printUsage("run");
        } else {
          run(args);
        }
        break;

      case "clientSim":
        if (args.length < 7) {
          printUsage("clientSim");
        } else {
          clientSim(args);
        }
    }
  }

  public static Queue<Request> getRequestQueue(String requestfile, String requestDescFile) {

    String[] reqTab = SimizerUtils.readRequestsFile(requestfile);
    RequestFactory rf = null;
    try {
      rf = new RequestFactory(RequestFactory.loadRequests(requestDescFile));
    } catch (IOException ex) {
      Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }

    Queue<Request> rqueue = new ArrayBlockingQueue<Request>(reqTab.length);
    for (int i = 0; i < reqTab.length; i++) {
      //System.out.println(reqTab[i]);
      String[] descTab = reqTab[i].split(";");
      rqueue.add(rf.getRequest(Long.parseLong(descTab[1]),
              Integer.parseInt(descTab[2])));
    }

    return rqueue;

  }
  // Missing resource description

  private static void run(String[] args) {
    String nodeFile = args[0]; // fichier de description des noeuds
    String reqDesc = args[1]; // description des requêtes;
    String workload = args[2]; // description de la workload
    String resFile = args[3]; // desc des ressources
    String policyName = args[4]; // lb policy classname
    // run ./5nodes.json ./reqDescription_v1.csv ./workload_gene_v1.csv ./test Simizer.policies.CawaDyn
    run(nodeFile, reqDesc, workload, resFile, policyName);
  }

  public static void run(String nodeFile, String reqDesc, String wlFile, String resFile, String policyName) {

    //1. charger la description des requêtes
    Queue<Request> rqueue = getRequestQueue(wlFile, reqDesc);

        //2. charger les storage element
    //int NB_RES = 150;
    ResourceFactory resfac = SimizerUtils.getRessourceFactory(resFile);

        //@TODO create a storage element for each node with different ressources (RR fashion)
    StorageElement.setFactory(resfac);
    final StorageElement se = new StorageElement(resfac.getMax() * resfac.getResourceSize(), 6L);
    se.write(resfac.getStartList());

    resfac.getResourceSize();
    //3. charger les noeuds
    final Map<Integer, Node> nodeMap = new TreeMap<Integer, Node>();
        //Network setup
    // on cree 2 reseaux:
    //Internet
    Network internet = new Network(nodeMap, new GaussianLaw(75));
    //LAN
    Network lan = new Network(nodeMap, new GaussianLaw(5));

    // policy & lb setup :
    Policy pol = null;
    try {
      pol = (Policy) Class.forName(policyName).newInstance();
      System.out.println(pol.toString());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
      Logger.getLogger(SimizerUtils.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }

    LBNode lbn = new LBNode(-1, pol);

    List<ServerNode> nodeList = SimizerUtils.decodeNodes(SimizerUtils.readFile(nodeFile));

    nodeList.size();
    final Channel c = new Channel();
    for (ServerNode n : nodeList) {

      n.setFrontendNode(lbn);
      n.setStorage(se);
      n.setNetwork(lan);
      n.setChannel(c);
      nodeMap.put(n.getId(), n);

    }

    pol.initialize(nodeList, lbn);

    nodeMap.put(-1, lbn);

    internet.setChannel(c);
    lan.setChannel(c);

    lbn.setInsideNetwork(lan);
    lbn.setOutsideNetwork(internet);

    System.out.println("Thread starting for " + rqueue.size());
        //4.Envoi des requetes

        // total number of events to be processed by all threads.
    // this is necessary to know when to stop the threads.
    int totalEvt = rqueue.size() * 4;
    while (!rqueue.isEmpty()) {
      Request r = rqueue.poll();
      internet.send(null, lbn, r, r.getArTime());//Node source, Node dest, Request r, long timestamp
    }

    int nbCores = 1;// Runtime.getRuntime().availableProcessors() +1;

    ExecutorService es = Executors.newFixedThreadPool(nbCores);
    EventDispatcher[] edArray = new EventDispatcher[nbCores];

    for (int i = 0; i < nbCores; i++) {
      edArray[i] = new EventDispatcher(c);
      es.submit(edArray[i]);
    }

    // waiting loop for ending the processing.
    int sum = 0;
    while (edArray[0].getChannel().size() > 0) {
      sum = 0;
      for (int i = 0; i < edArray.length; i++) {
        sum += edArray[i].getEventCount();
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    System.out.print("PRINTING TIME: ");
    for (int i = 1; i < 11; i++) {
      System.out.print(Simizer.storetime[i] + ";");
    }
    System.out.println("");

    System.out.println("Stopping..." + ServerNode.getHits());
    for (int i = 0; i < edArray.length; i++) {
      edArray[i].stop();
    }
    pol.printAdditionnalStats();
    es.shutdown();
  }

  private static void generate(String[] args) {
    String lawConfFile = args[0];
    String rDescFile = args[1];
    int nbUsers = Integer.parseInt(args[2]);
    int maxReq = Integer.parseInt(args[3]);
    int interval = Integer.parseInt(args[4]);
    // generate wl.conf ./reqDescription_v2.csv 100 1000 30
    generate(lawConfFile, rDescFile, nbUsers, maxReq, interval);
  }

  // define a class to aid in the generation of requests
  static class SimpleClient implements Comparable<SimpleClient> {
    public final Integer id;
    public final long timestamp;

    public SimpleClient(Integer id, long timestamp) {
      this.id = id;
      this.timestamp = timestamp;
    }

    @Override
    public int compareTo(SimpleClient second) {
      return Long.compare(this.timestamp, second.timestamp);
    }
  }

  static public void generate(String lawConf, String rDescFile, int nbUsers, int maxReq, int interval) {

    Map<String, Law> lawMap = loadLaws(lawConf);
    RequestFactory rf = null;
    try {
      rf = new RequestFactory(RequestFactory.loadRequests(rDescFile));
    } catch (IOException ex) {
      Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }


    PriorityQueue<SimpleClient> clients = new PriorityQueue<>(nbUsers);

    // User vs its current time
    int requests = 0;
    long intervalTimestamp = 0;

    while (true) {
      // we have processed all of the requests
      if (requests == maxReq) {
        break;
      }

      SimpleClient next = clients.peek();

      // If the clients list is empty, it means that we haven't created any
      // clients yet.  If this is the case, simulate having an event that will
      // trigger client creation.  Note that if the law doesn't create any
      // clients, the code will try again at the next interval.  If the law
      // always returns zero, then this will create an infinite loop.
      if (clients.isEmpty()) {
        next = new SimpleClient(0, intervalTimestamp);
      }

      // there is nothing left to process
      if (next == null) {
        break;
      }

      // if it's time to create clients AND we haven't created all the clients
      if (next.timestamp >= intervalTimestamp && clients.size() < nbUsers) {
        addSimpleClients(clients, lawMap.get("arrivalLaw"), intervalTimestamp, nbUsers);
        intervalTimestamp += interval;
        continue;  // we could have added a client with an earlier timestamp
      }

      clients.poll();  // remove the element now that we know it's correct
      Request r = rf.getRequest(next.timestamp, lawMap.get("requestLaw").nextValue());
      System.out.println(r.getId() + ";" + r.getArTime() + ";" + r.getTypeId() + ";" + next.id);

      clients.offer(new SimpleClient(next.id, next.timestamp + lawMap.get("thinkTimeLaw").nextValue()));
      requests++;
    }
  }

  private static void addSimpleClients(PriorityQueue<SimpleClient> clients,
      Law law, long timestamp, int maximumClientCount) {

    // make sure that we don't exceed the maximum client count
    int current = clients.size();
    int count = Math.min(law.nextValue() + current, maximumClientCount);

    for (int i = current; i < count; i++) {
      clients.offer(new SimpleClient(i, timestamp));
    }
  }

  private static void clientSim(String[] args) {
    String lawConfFile = args[0];
    String reqDesc = args[1];
    String resFile = args[2];
    String nodeFile = args[3];
    String policyName = args[4];
    long endSim = Long.parseLong(args[5]);
    int maxUsers = Integer.parseInt(args[6]);
    //clientSim wl.conf  ./reqDescription_image_v2.csv ./ressources.json ./10nodes.json simizer.policies.RoundRobin 180000 15
    runClientSim(lawConfFile, reqDesc, resFile, nodeFile, policyName, endSim, maxUsers);
  }

  /**
   * Sets up the clients, ressources and nodes for client based simulation.
   *
   * @param lawConfFile
   * @param rDescFile
   * @param resFile
   * @param policy
   * @param endSim
   * @param maxUsers
   */
  private static void runClientSim(String lawConfFile, String rDescFile, String resFile, String nodeFile, String policyName, long endSim, int maxUsers) {

    Map<String, Law> lawMap = loadLaws(lawConfFile);

    ResourceFactory resfac = SimizerUtils.getRessourceFactory(resFile);

    StorageElement.setFactory(resfac);

    RequestFactory rf = null;
    try {
      rf = new RequestFactory(RequestFactory.loadRequests(rDescFile));
    } catch (IOException ex) {
      Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }
    ClientNode.configureRequestFactory(rf);
    ClientNode.configureLaws(
            lawMap.get("requestLaw"), lawMap.get("thinkTimeLaw"), lawMap.get("durationLaw"));
    Law internetLatency = new GaussianLaw(20);
    internetLatency.setParam(10.0);

    Network internet = new Network(internetLatency);
    //LAN
    Law lanLatency = new GaussianLaw(10);
    lanLatency.setParam(5.0);
    Network lan = new Network(lanLatency);
    // policy & lb setup :
    Policy pol = null;
    try {
      pol = (Policy) Class.forName(policyName).newInstance();
      System.out.println(pol.toString());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
      Logger.getLogger(SimizerUtils.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }
    Channel c = new Channel();

    LBNode lbn = new LBNode(-1, pol);

    List<ServerNode> nodeList = SimizerUtils.decodeNodes(SimizerUtils.readFile(nodeFile));
    int nbResPerNode = 1 + (resfac.getResourceNb() / nodeList.size());
    for (ServerNode n : nodeList) {
      n.setFrontendNode(lbn);
//            n.getCapacity();
      StorageElement se = new StorageElement(
              ((long) resfac.getMax()) * resfac.getResourceSize(), 4L);
      se.setPerMBReadDelay(2.0);
      //se.write(resfac.getStartList(nbResPerNode));
      se.write(resfac.getStartList());
      n.setStorage(se);

      n.setNetwork(lan);
      n.setChannel(c);
      lan.putNode(n);

    }
    pol.initialize(nodeList, lbn);

    internet.setChannel(c);

    lan.setChannel(c);

    ClientGenerator cg = new ClientGenerator(internet, lawMap.get("arrivalLaw"), interval, lbn, endSim, maxUsers);
    lbn.setInsideNetwork(lan);
    lbn.setOutsideNetwork(internet);

    EventDispatcher ed = new EventDispatcher(c);
    Thread simThread = new Thread(ed);
    System.out.println("Starting simulation for " + endSim + " milisecs " + ed.getChannel().size());
    try {

      simThread.start();
      simThread.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
    }

    System.out.println("Stopping..." + ed.getEventCount() + " " + ServerNode.getHits());
    pol.printAdditionnalStats();

  }

  static public Map<String, Law> loadLaws(String lawConfFile) {
    Properties p = new Properties();
    Map<String, Law> lawMap = new HashMap<String, Law>();
    try {
      FileInputStream fis = new FileInputStream(lawConfFile);

      p.load(new FileInputStream(lawConfFile));
      /**
       * @TODO : yerk! dirty config loading, to be removed
       */
      interval = Integer.parseInt(p.getProperty("interval"));
      lawMap.put("arrivalLaw", Law.loadLaw(p.getProperty("arrivalLaw")));
      lawMap.put("requestLaw", Law.loadLaw(p.getProperty("requestLaw")));
      lawMap.put("thinkTimeLaw", Law.loadLaw(p.getProperty("thinkTimeLaw")));
      lawMap.put("durationLaw", Law.loadLaw(p.getProperty("durationLaw")));
      fis.close();
    } catch (Exception ex) {
      System.out.println("Configuration Issue, failed to load laws or config file");
      Logger.getLogger(Simizer.class.getName()).log(Level.SEVERE, null, ex);
    }
    return (lawMap);
  }
}
