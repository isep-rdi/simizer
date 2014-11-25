package simizer.requests;

import java.util.ArrayList;
import java.util.List;
import simizer.utils.Vector;

public class Request {

  private long id;
  private int node = -1;
  private long nbInstructions;
  protected String params;
  private List<Integer> rscList;
  protected int typeId;
  long procTime;

  /** The timestamp when the client starts sending the {@code Request}. */
  private long clientStartTimestamp;

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

  protected String type;
  /** The cost associated with the {@code Request}. */
  private double cost = 0.0;

  int error = 0;

  private int size;

  public Request(long id, int typeId, long artime, String params, long procTime,
      String type, long nbInst) {

    this.id = id;
    this.clientStartTimestamp = artime;
    this.type = type;
    this.typeId = typeId;
    this.params = params;
    this.procTime = procTime;
    this.rscList = parseResources(params);
    this.nbInstructions = nbInst;
  }

  /**
   * Copy constructor, to refacto (rewrite @link{RequestFactory})
   *
   * @param r
   */
  public Request(Request r) {
    this.id = -1;
    this.clientStartTimestamp = -1;
    this.node = 0;
    this.cost = r.cost;
    this.params = r.params;
    this.procTime = r.procTime;
    this.rscList = r.rscList;
    this.serverFinishTimestamp = 0;
    this.type = r.type;
    this.typeId = r.typeId;
    this.error = 0;

    this.nbInstructions = r.nbInstructions;
  }

  public long getId() {
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

  public String getParameters() {
    return this.params;
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
            + ";" + error + "r");
  }

  public String display() {
    return (id + ","
            + params.replaceAll("&|=", ",")
            + "," + node
            + "," + cost);
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

  public int getError() {
    return this.error;
  }

  public void setError(int count) {
    this.error = count;
  }

  public long getNbInst() {
    return this.nbInstructions;
  }

  public void setNbInst(long l) {
    this.nbInstructions = l;
  }

  public String getRequestType() {
    return this.type;
  }

  public int getAppId() {
    return this.typeId;
  }

  public int getSize() {
    return this.size;
  }

  public String getType() {
    return this.type;
  }

  public void setAppId(int i) {
    this.typeId = i;
  }





  /** @deprecated @return */
  public int getTypeId() {
    return this.typeId;
  }

  // new type of request for request factory

  /**
   * @deprecated @param id
   * @param typeId
   * @param artime
   * @param params
   * @param procTime
   * @param type
   */
  public Request(long id, int typeId, long artime, String params, long procTime, String type) {
    this.id = id;
    this.clientStartTimestamp = artime;
    this.type = type;
    this.typeId = typeId;
    this.params = params;
    this.procTime = procTime;
    this.rscList = parseResources(params);
  }

  /**
   * @deprecated @param id
   * @param params
   * @param procTime
   * @param cmTime
   */
  public Request(long id, String params, long procTime, long cmTime) {
    this.id = id;
    this.params = params;
    this.procTime = procTime;
    this.serverFinishTimestamp = -1;
  }

  /**
   * @deprecated @param id
   * @param procTime
   * @param arTime
   * @param node
   */
  public Request(long id, long procTime, long arTime, int node) {
    this.id = id;
    this.node = node;
    this.clientStartTimestamp = arTime;
    this.procTime = procTime;
    this.serverFinishTimestamp = -1;

  }

  /**
   * @deprecated @param id
   * @param artime
   * @param procTime
   * @param node
   * @param cost
   * @param params
   */
  public Request(long id, long artime, long procTime, int node, float cost, String params) {

    this.id = id;
    this.clientStartTimestamp = artime;
    this.node = node;
    this.procTime = procTime;
    this.cost = cost;
    this.params = params;
    this.rscList = parseResources(params);
    this.serverFinishTimestamp = -1;
  }

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
  public Request(long id, long artime, int node, float cost, String params) {

    this.id = id;
    this.clientStartTimestamp = artime;
    this.node = node;
    this.cost = cost;
    this.params = params;

  }

  /** @deprecated */
  @Deprecated
  public void setId(long id) {
    this.id = id;
  }

  /** @deprecated @param params */
  @Deprecated
  public Request(String params) {
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
