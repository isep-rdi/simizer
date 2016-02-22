package fr.isep.simizer.requests;

import fr.isep.simizer.network.Network;
import fr.isep.simizer.utils.Vector;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a request (and later response) that is sent during the simulation.
 * <p>
 * There basic format for requests is that they are defined by three non-mutable
 * parameters: application, action, and query.  The application can be thought
 * of as the hostname in a URL.  The action is the path component.  And the
 * query is the part after the "?" in the URL.
 */
public class Request {

  /** The next ID that should be assigned to a {@code Requst}. */
  private static Long nextID = 1L;

  private final Long id;
  private int node = -1;

  /**
   * The ID of the {@code Application} that should handle the {@code Request}.
   */
  protected Integer applicationId;
  
  /**
   * The ID of the {@code Application} that sent the {@code Request}
   */
  protected Integer senderId;

  /** The action that the {@code Application} should perform. */
  protected String action;

  /** The query string to pass to the handler. */
  protected String query;

  /** The parsed parameters from the query string. */
  protected Map<String, String> parameters = null;

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

  /** Stores fields used by custom applications. */
  private final Map<String, Object> customFields = new HashMap<>();

  /** The number of errors that have occurred for this {@code Request}. */
  private int errorCount = 0;

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

  /**
   * Initializes a new {@code Request}.
   *
   * @param applicationId the ID of the {@link Application} where the {@code
   *            Request} should be sent
   * @param action the action that the server should perform for this {@code
   *            Request}
   * @param query the additional parameters that define how the action should be
   *            taken
   * @param isTemplate whether or not this is a {@code Request} template
   */
  public Request(Integer applicationId, String action, String query,
      boolean isTemplate) {

    this(isTemplate);

    // set the configurable properties
    this.applicationId = applicationId;
    this.action = action;
    this.query = query;
    this.parameters = parseQuery(query);

    // set default values
    this.clientStartTimestamp = -1L;
    this.procTime = 0;  // unused
    this.nbInstructions = 0;  // unused
  }

  /**
   * Initializes a {@code Request} that is <strong>not</strong> a template.
   *
   * @param applicationId the ID of the {@link Application} where the {@code
   *            Request} should be sent
   * @param action the action that the server should perform for this {@code
   *            Request}
   * @param query the additional parameters that define how the action should be
   *            taken
   */
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
    this.set("cost", r.get("cost"));
    this.query = r.query;
    this.parameters = r.parameters;
    this.procTime = r.procTime;
    this.serverFinishTimestamp = 0;
    this.action = r.action;
    this.applicationId = r.applicationId;
    this.errorCount = 0;

    this.nbInstructions = r.nbInstructions;
  }

  /**
   * Returns the ID of the {@code Request}.
   *
   * @return the ID of the {@code Request}
   */
  public Long getId() {
    return this.id;
  }

  /**
   * Sets the value for a custom field.
   * <p>
   * This will create the custom field if it has not yet been created, and it
   * will also replace any existing value.
   *
   * @param name the name of the custom field
   * @param value the value for the custom field
   */
  public final void set(String name, Object value) {
    customFields.put(name, value);
  }

  /**
   * Returns the value for the specified custom field.
   *
   * @param name the name for the custom field
   * @return the value for this custom field, or null if it is not set
   */
  public final Object get(String name) {
    return customFields.get(name);
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

  @Override
  public String toString() {
    return (id
            + ";" + clientStartTimestamp
            + ";" + query
            + ";" + serverFinishTimestamp
            + ";" + networkDelay
            + ";" + node
            + ";" + get("loadBalancingDelay")  // for backwards compatibility
            + ";" + get("cost")  // for backwards compatibility
            + ";" + errorCount + "r");
  }

  /** @deprecated @return */
  public String display() {
    return (id + ","
            + query.replaceAll("&|=", ",")
            + "," + node
            + "," + get("cost"));
  }

  /**
   * Returns the {@code Application} ID associated with this {@code Request}.
   *
   * @return the {@code Application} ID associated with this {@code Request}
   */
  public Integer getApplicationId() {
    return this.applicationId;
  }

  /**
   * Returns the action associated with this {@code Request}.
   *
   * @return the action associated with this {@code Request}
   */
  public String getAction() {
    return this.action;
  }

  /**
   * Returns the query string associated with this {@code Request}.
   * 
   * @return the query string associated with this {@code Request}
   */
  public String getQuery() {
    return this.query;
  }

  /**
   * Returns a specific parameter from the query string.
   *
   * @param key the key of the parameter to return
   * @return the value, or null if the parameter is not set
   */
  public String getParameter(String key) {
    return this.parameters.get(key);
  }

  /**
   * Returns the list of resources associated with this {@code Request}.
   * <p>
   * This is in place as a convenience method, and it is also in place to
   * maintain backwards compatibility with older code.  It converts the
   * "resources" parameter to a list of Integers.  The value of the field should
   * be a list of Integers separated with underscores.
   *
   * @return a {@link List} with the IDs from the "resources" parameter
   */
  public List<Integer> getResources() {
    String value = getParameter("resources");
    String[] resources = value.split("_");

    List<Integer> result = new LinkedList<>();
    for (String resource : resources) {
      result.add(Integer.parseInt(resource));
    }
    return result;
  }

  /**
   * Parses a standard URL query into a map.
   * <p>
   * This allows for the most customization, and it supports all the
   * functionality already implemented in the examples framework.
   *
   * @param query the query string to parse
   * @return a map with the result, or null if an error occurred
   */
  public static Map<String, String> parseQuery(String query) {
    try {
      Map<String, String> map = new HashMap<>();
      String[] parameters = query.split("&");
      for (String parameter : parameters) {
        String[] kv = parameter.split("=");
        String key = URLDecoder.decode(kv[0], "UTF-8");
        String value = null;
        if (kv.length > 1) {
          value = URLDecoder.decode(kv[1], "UTF-8");
        }
        map.put(key, value);
      }
      return map;
    } catch (UnsupportedEncodingException ex) {
      System.err.println("Error parsing query \"" + query + "\": "
              + ex.getMessage());
      return null;
    }
  }
  
  /** 
   * Changes the target application id, which useful or request/responses
   * protocols.
   * @param appId
   */
  public void setAppId(Integer appId) {
	  this.applicationId = appId;
  }
  
  public void setSenderId(Integer appId) {
	  this.senderId = appId;
  }

  public Integer getSenderId() {
	  return this.senderId;
  }
  /** @deprecated @param nId */
  public void setNode(int nId) {
    this.node = nId;
  }

  /** @deprecated @return */
  public long getProcTime() {
    return this.procTime;
  }

  /** @deprecated @return */
  public Integer getNodeId() {
    return this.node;
  }

  /** @deprecated @return */
  public long getNbInst() {
    return this.nbInstructions;
  }

  /** @deprecated @param l */
  public void setNbInst(long l) {
    this.nbInstructions = l;
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
  public Request(Long id, long artime, int node, double cost, String params) {

    this.id = id;
    this.clientStartTimestamp = artime;
    this.node = node;
    set("cost", cost);
    this.query = params;

  }

  /** @deprecated @param params */
  @Deprecated
  public Request(String params) {
    this.id = null;
    this.query = params;
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
    String[] str = this.query.split("&|=");
    double[] d = new double[str.length];

    for (int i = 0; i < str.length; i++) {
      d[i] = Double.parseDouble(str[i]);
    }
    Vector monVec = new Vector(d);
    return monVec;
  }

  /** @deprecated @return */
  public Vector requestToVectorH() {
    String[] str = this.query.split("&|=");
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
