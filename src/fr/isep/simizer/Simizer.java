package fr.isep.simizer;

import fr.isep.simizer.laws.Law;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        if (args.length < 5 || true) {
          printUsage("run");
        } else {
          run(args);
        }
        break;

      case "clientSim":
        if (args.length < 7 || true) {
          printUsage("clientSim");
        } else {
          clientSim(args);
        }
    }
  }

  // Missing resource description

  private static void run(String[] args) {}

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
    RequestFactory rf = new RequestFactory();
    try {
      rf.loadTemplates(rDescFile);
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
      System.out.println(r.getId() + ";" + r.getClientStartTimestamp() + ";" + r.getApplicationId() + ";" + next.id);

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

  private static void clientSim(String[] args) {}

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
