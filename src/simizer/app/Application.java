package simizer.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import simizer.nodes.Node;
import simizer.nodes.VM;
import simizer.requests.Request;
import simizer.storage.Resource;

/**
 * Represents a running process on VM.
 * <p>
 * Implement the {@link #handle(simizer.Node, simizer.requests.Request)} method
 * for application-specific behavior.
 *
 * @author Sylvain Lefebvre
 */
public abstract class Application {

  /** The ID of the {@code Application}. */
  private final Integer id;

  /** The amount of memory used by this {@code Application}. */
  
  private final int memorySize;

  /**
   * The {@code VM} on which this {@code Application} is being run.
   * <p>
   * This reference can be used to allow the {@code Application} to make "system
   * calls," such as file operations, network operations, and processor
   * execution operations.
   */
  protected VM vm;

  /** Contains user-specific properties for the {@code Application}. */
  protected Properties config = new Properties();

  /** Holds a reference to all of the pending requests. */
  private final Map<Long, Node> pending = new HashMap<>();

  /**
   * Initializes a new instance of the class.
   *
   * @param id the ID of the application
   * @param memorySize the memory footprint for the application
   */
  public Application(Integer id, int memorySize) {
    this.id = id;
    this.memorySize = memorySize;
  }

  /**
   * Initializes an {@code Application} from a template.
   * <p>
   * The configuration details of the application are copied (ID, memorySize,
   * and custom-defined properties), but the {@link VM} reference is not.  The
   * purpose of this constructor is to be able to easily deploy the same
   * application on multiple machines.
   *
   * @param template the {@code Application} that should serve as a template
   *            when creating this one
   */
  public Application(Application template) {
    this.id = template.id;
    this.memorySize = template.memorySize;
    // TODO: I don't think we want to copy this as a reference.
    this.config = template.config;
    this.vm = null;
  }

  /**
   * Returns the ID of the {@code Application}.
   * 
   * @return the ID of the {@code Application}
   */
  public Integer getId() {
    return id;
  }

  /**
   * Returns the amount of memory used by the {@code Application}.
   *
   * @return the amount of memory used by the {@code Application}
   */
  public int getMemorySize() {
    return memorySize;
  }

  /**
   * Sets the {@code VM} associated with this {@code Application}.
   *
   * @param vm the {@code VM} to associate with this {@code Application}
   */
  public void setVM(VM vm) {
    this.vm = vm;
  }

  /**
   * Sets a configuration option for the {@code Application}.
   *
   * @param key the key of the property
   * @param value the value for the property
   */
  public void setConfig(String key, String value) {
    config.setProperty(key, value);
  }

  /**
   * Performs initialization logic when the {@code Application} starts running.
   * <p>
   * Subclasses must override this method to provide application-specific logic.
   */
  public abstract void init();

  /**
   * Handles requests from clients.
   * <p>
   * Subclasses must override this method with their application-specific logic.
   * The method is used to handle requests sent from clients to the server.
   *
   * @param origin the {@link Node} that sent the {@link Request}.  This is the
   *            {@link Node} where the server should send its response.
   * @param request the {@link Request} sent by the client
   */
  public abstract void handle(Node origin, Request request);

  protected int write(Resource res, int sz) {
    return vm.write(res, sz);
  }

  /**
   * Adds a MODIFY DiskTask to the current task Session.
   *
   * @param resourceId
   * @param sz Size after modification
   * @return Version number of the resource, 0 if created, -1 if failure to
   * write.
   */
  protected int modify(int resourceId, int sz) {
    return vm.modify(resourceId, sz);
  }

  /**
   * Adds a processing task to the current task session;
   *
   * @param nbInstructions
   * @param memSize
   * @param res
   * @return 0 if success
   */
  protected int execute(long nbInstructions, int memSize, List<Resource> res) {
    return vm.execute(nbInstructions, memSize, res);
  }

  protected Resource read(int resourceId) {
    return vm.read(resourceId);
  }

  protected Resource read(int resourceId, int sz) {
    return vm.read(resourceId, sz);
  }

  protected boolean sendRequest(Node dest, Request req) {
    pending.put(req.getId(), dest);
    return vm.sendRequest(dest, req);
  }

  protected boolean sendResponse(Node dest, Request req) {
    pending.remove(req.getId());
    return vm.sendResponse(req, dest);
  }

  protected boolean sendOneWay(Node n, Request request) {
    return sendRequest(n, request);
  }

}
