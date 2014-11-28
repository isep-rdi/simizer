package simizer.requests;

import java.util.ArrayList;
import java.util.List;
import simizer.network.Network;
import simizer.utils.Vector;

public class Request {

  /** The next ID that should be assigned to a {@code Requst}. */
  private static Long nextID = 1L;

  private final Long id;
  private int node = -1;
  
  private List<Integer> rscList;

  /**
   * The ID of the {@code Application} that should handle the {@code Request}.
   */
  protected Integer applicationId;

  /** The action that the {@code Application} should perform. */
  protected String action;

  /** The parameter string to pass to the handler. */
  protected String params;

  private long nbInstructions;  // @deprecated
  long procTime;  // @deprecated

  /** The timestamp when the client starts sending the {@code Request}. */
  private long clientStartTimestamp;
  
  /** The timestamp when the client receives the response. */
  private long clientEndTimestamp;

  /**
   * The timestamp when the server finishes processing the {@code Request}.
   * <p>
   * This is the timestamp when it hands the response to the {@link Network} to
   * be sent back to the client.  It is not the end of the request-response
   * cycle because the response still needs to reach the client.
   */
  private long serverFinishTimestamp;

  /** The delay that comes as a result of {@code Network} delays. */
  private long networkDelay = 0;

  /** The delay from the load balancing code (measured in nanoseconds). */
  private long loadBalancingDelayNS = 0;

  /** The cost associated with the {@code Request}. */
  private double cost = 0.0;

  /** The number of errors that have occurred for this {@code Request}. */
  private int errorCount = 0;

  /** The size of the {@code Request}.  This value is mostly ignored. */
  private int size;

  /**
   * Performs internal initialization of the {@code Request}.
   * <p>
   * This involves setting the ID of the {@code Request} if one is needed.
   * Since templates are not sent through the system, they don't need an ID.
   */
  private Request(boolean isTemplate) {
    if (isTemplate) {
      this.id = null;
    } else {
      this.id = nextID++;
    }
  }

  public Request(Integer applicationId, String action, String parameters,
      boolean isTemplate) {

    this(isTemplate);

    // set the configurable properties
    this.applicationId = applicationId;
    this.action = action;
    this.params = parameters;

    // set default values
    this.clientStartTimestamp = -1L;
    this.procTime = 0;  // unused
    this.nbInstructions = 0;  // unused

    // load necessary information into supplementary data structures
    this.rscList = parseResources(parameters);
  }

  public Request(Integer applicationId, String action, String parameters) {
    this(applicationId, action, parameters, false);
  }

  /**
   * Copy constructor, to refacto (rewrite @link{RequestFactory})
   *
   * @param r
   */
  public Request(Request r) {
    this(false);  // get an ID for the Request
    
    this.clientStartTimestamp = -1;
    this.node = 0;
    this.cost = r.cost;
    this.params = r.params;
    this.procTime = r.procTime;
    this.rscList = r.rscList;
    this.serverFinishTimestamp = 0;
    this.action = r.action;
    this.applicationId = r.applicationId;
    this.errorCount = 0;

    this.nbInstructions = r.nbInstructions;
  }

  public Long getId() {
    return this.id;
  }

  /**
   * Returns the timestamp when the client first sent the {@code Request}.
   * <p>
   * When a client first hands a {@code Request} to the {@link Network} to be
   * sent, this value should be set to the current timestamp.  It can therefore
   * be used to measure the round-trip time of the entire request.
   * 
   * @return the timestamp when the client first sent the {@code Request}
   */
  public long getClientStartTimestamp() {
    return clientStartTimestamp;
  }

  /**
   * Sets the timestamp when the client first sent the {@code Request}.
   * <p>
   * This value should only be set once per {@code Request}.
   * <p>
   * For a more in-depth description of the value, see {@link
   * #getClientStartTimestamp()}.
   *
   * @param clientStartTimestamp the timestamp when the client first sent the
   *            {@code Request}
   */
  public void setClientStartTimestamp(long clientStartTimestamp) {
    this.clientStartTimestamp = clientStartTimestamp;
  }

  /**
  * Returns the timestamp when the client received the response.
  * <p>
  * When a client receives a response from the server, this value is set to the
  * current timestamp.
  *
  * @return the timestamp when the client received the response
  */
  public long getClientEndTimestamp() {
    return clientEndTimestamp;
  }

  /**
   * Sets the timestamp when the client receives the response.
   * <p>
   * This value should only be set once per {@code Request}.
   *
   * @param clientEndTimestamp the timestamp when the client received the
   *            response
   */
  public void setClientEndTimestamp(long clientEndTimestamp) {
    this.clientEndTimestamp = clientEndTimestamp;
  }

  /**
   * Returns the timestamp when the server finished processing the {@code
   * Request}.
   *
   * @return the timestamp when the server finished processing the {@code
   *             Request}
   */
  public long getServerFinishTimestamp() {
    return serverFinishTimestamp;
  }

  /**
   * Sets the timestamp when the server finishes processing the {@code Request}.
   *
   * @param serverFinishTimestamp the timestamp when the server finishes processing
   *            the {@code Request}
   */
  public void setServerFinishTimestamp(long serverFinishTimestamp) {
    this.serverFinishTimestamp = serverFinishTimestamp;
  }

  /**
   * Returns the current delay imposed by {@code Network}s.
   *
   * @return the current delay imposed by {@code Network}s
   */
  public long getNetworkDelay() {
    return networkDelay;
  }

  /**
   * Adds the specified value to the total delay from {@code Network}s.
   *
   * @param delay the amount by which the network delay should be increased
   */
  public void addNetworkDelay(long delay) {
    networkDelay += delay;
  }

  /**
   * Sets the load balancing delay.
   * <p>
   * In order to accurately measure the time taken by the various load balancing
   * algorithms, this value is measured as accurately as possible.  It is
   * measured in nanoseconds to provide enough granularity.
   *
   * @param nanoseconds the amount of the delay, measured in nanoseconds
   */
  public void setLoadBalancingDelayNS(long nanoseconds) {
    this.loadBalancingDelayNS = nanoseconds;
  }

  /**
   * Sets the cost associated with this {@code Request}.
   *
   * @param cost the cost to associated with this {@code Request}
   */
  public void setCost(double cost) {
    this.cost = cost;
  }

  /**
   * Returns the cost associated with this {@code Request}.
   *
   * @return the cost associated with this {@code Request}
   */
  public double getCost() {
    return this.cost;
  }

  /**
   * Returns the number of errors that have occurred.
   *
   * @return the number of errors that have occurred
   */
  public int getErrorCount() {
    return this.errorCount;
  }

  /**
   * Reports errors for this {@code Request}.
   * <p>
   * The {@code Request} object keeps a running tally of the number of errors
   * that have occurred.  Each time an errorCount occurred, it should be
   * reported using this method.
   *
   * @param count the number of errors to report.  This value is added to the
   *            running total of errors that have occurred.
   */
  public void reportErrors(int count) {
    this.errorCount += count;
  }

  /**
   * Returns the size of the {@code Request}.
   *
   * @return the size of the {@code Request}
   */
  public int getSize() {
    return this.size;
  }

  @Override
  public String toString() {
    return (id
            + ";" + clientStartTimestamp
            + ";" + params
            + ";" + serverFinishTimestamp
            + ";" + networkDelay
            + ";" + node
            + ";" + loadBalancingDelayNS
            + ";" + cost
            + ";" + errorCount + "r");
  }

  public String display() {
    return (id + ","
            + params.replaceAll("&|=", ",")
            + "," + node
            + "," + cost);
  }

  public Integer getApplicationId() {
    return this.applicationId;
  }

  public String getAction() {
    return this.action;
  }

  public String getParameters() {
    return this.params;
  }

  // I don't think this has what I perceived to be the intended behavior.  It
  // makes it non-overiddable, but it doesn't prevent the list from being
  // changed.
  public final List<Integer> getResources() {
    return this.rscList;
  }

  public String[] getParamsStr() {

    String[] tmp = params.split("&|=");
    String[] result = new String[tmp.length / 2];
    int y = 0;
    for (int i = 1; i < tmp.length; i += 2) {
      result[y++] = tmp[i];
    }
    return result;
  }

  public int[] getParams() {
    String[] s = params.split(",");
    int[] res = new int[s.length];
    for (int i = 0; i < s.length; i++) {
      res[i] = Integer.parseInt(s[i]);
    }
    return res;
  }

  public void setNode(int nId) {
    this.node = nId;
  }

  /** @deprecated */
  public long getProcTime() {
    return this.procTime;
  }

  private static List<Integer> parseResources(String params) {
    List<Integer> rList = new ArrayList<>();
    String[] tokens = params.split("&|=");
    for (int i = 1; i < tokens.length; i += 2) {
      rList.add(Integer.parseInt(tokens[i]));
    }
    return rList;
  }

  public Integer getNodeId() {
    return this.node;
  }

  /** @deprecated */
  public long getNbInst() {
    return this.nbInstructions;
  }

  /** @deprecated */
  public void setNbInst(long l) {
    this.nbInstructions = l;
  }

  /**
   *
   * @param i
   * @deprecated The ID should be set when the {@code Request} is created.
   */
  @Deprecated
  public void setAppId(int i) {
    this.applicationId = i;
  }

  // new type of request for request factory

  /**
   * Only used in Cawa policies (historical) Should be deleted
   *
   * @deprecated
   * @param id
   * @param artime
   * @param node
   * @param cost
   * @param params
   */
  public Request(Long id, long artime, int node, float cost, String params) {

    this.id = id;
    this.clientStartTimestamp = artime;
    this.node = node;
    this.cost = cost;
    this.params = params;

  }

  /** @deprecated @param params */
  @Deprecated
  public Request(String params) {
    this.id = null;
    this.params = params;
  }

  /** @deprecated @param req @return */
  public static Request vectorToRequest(double[] req) {
    String tmp = "";
    for (int i = 0; i < req.length; i++) {
      tmp = tmp + req[i] + ",";
    }

    Request request = new Request(tmp);
    return request;
  }

  /** @deprecated @return */
  public Vector requestToVector() {
    String[] str = this.params.split("&|=");
    double[] d = new double[str.length];

    for (int i = 0; i < str.length; i++) {
      d[i] = Double.parseDouble(str[i]);
    }
    Vector monVec = new Vector(d);
    return monVec;
  }

  /** @deprecated @return */
  public Vector requestToVectorH() {
    String[] str = this.params.split("&|=");
    double[] d = new double[str.length / 2];
    for (int i = 1; i < str.length; i += 2) {
      if (str[i].matches("^[0-9].*$")) {
        d[i / 2] = Double.parseDouble(str[i]);
      } else {
        d[i / 2] = Double.parseDouble(str[i].hashCode() + ".0");
      }
    }
    Vector monVec = new Vector(d);

    return monVec;
  }

}
