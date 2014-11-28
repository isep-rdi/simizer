package simizer.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simizer.app.Application;
import simizer.event.*;
import simizer.network.Message;
import simizer.network.MessageReceiver;
import simizer.network.Network;
import simizer.processor.ProcessingUnit;
import simizer.processor.tasks.*;
import simizer.requests.Request;
import simizer.storage.Cache;
import simizer.storage.IOType;
import simizer.storage.Resource;
import simizer.storage.StorageElement;

/**
 * This class models a virtual Machine. It can host several @see Application
 * through the use of the Deploy method. Provides interface for the Applications
 * to read() write() and execute(). Handles TaskSession creations
 *
 * @TODO: add a started state check
 * @see TaskSession
 *
 * @author slefebvr
 */
public class VM extends Node implements IEventProducer {

  /** Used to conform to the {@code IEventProducer} protocol. */
  private final EventProducer eventProducer = new EventProducer();

  /** The default cost associated with a {@code VM} (in cents). */
  public static final double DEFAULT_COST = 15.0;

  /** The default amount of memory available on a {@code VM} (in bytes). */
  public static final long DEFAULT_MEMORY_SIZE = StorageElement.GIGABYTE;

  /**
   * Stores the active number of requests.
   * <p>
   * Requests are started when they are received by the system.  They are marked
   * as finished when the response is sent back to the client.
   */
  private int activeRequestCount = 0;

  protected double hCost = 0.0;
  
  /** The amount of memory (RAM) in use by the {@code Application}s. */
  protected long memoryUsed;

  /**
   * The total amount of memory (RAM) available on the {@code VM}.
   * <p>
   * In future versions, this behavior will be changed to use the {@link
   * IOController} class.  As of now, the value is tracked but not enforced.
   */
  protected long memorySize;

  /** The processor for this {@code VM}. */
  private ProcessingUnit processor;

  /** The long-term storage (hard disk) for this {@code VM}. */
  private StorageElement disk;

  /** The temporary but faster {@code Cache} for this {@code VM}. */
  private Cache cache;

  private Map<Integer, Application> idToApp = new HashMap<>();
  private TaskSession currentTaskSession = null;

  /** Whether or not the {@code VM} has been started. */
  private boolean started = false;

  // TODO: Need to count the active number of requests.

  public VM(ProcessingUnit processor, StorageElement disk, long memory,
      double hCost) {

    super();

    this.processor = (processor != null)
            ? processor
            : new ProcessingUnit(1, 1000, null);
    this.disk = (disk != null)
            ? disk
            : new StorageElement(1_000_000, 10);
    this.cache = new Cache(memory, 1);
    this.memorySize = memory;
    this.hCost = hCost;

    // give the processor a reference to this VM
    this.processor.setNodeInstance(this);
  }

  /**
   * Default constructor, with default processor and default cost.
   */
  public VM() {
    this(null, null, DEFAULT_MEMORY_SIZE, DEFAULT_COST);
  }

  public ProcessingUnit getProcessingUnit() {
    return this.processor;
  }

  public long getClock() {
    return this.clock;
  }

  public double getCost() {
    return hCost;
  }

  public int getRequestCount() {
    return activeRequestCount;
  }

  /**
   * Deploys the specified {@code Application} on this {@code VM}.
   * <p>
   * If the {@code VM} has already been started, then the {@link Application}
   * will be started when it is deployed.  Otherwise, the {@link Application}
   * will be started when the {@code VM} is started.
   *
   * @param application the {@link Application} to deploy
   */
  public void deploy(Application application) {
    application.setVM(this);
    idToApp.put(application.getId(), application);
    memoryUsed += application.getMemorySize();
    if (started) {
      application.init();
    }
  }

  private void startApplication(Application application) {}

  /**
   * Starts the {@code VM} (by starting each {@code Application}).
   * <p>
   * The {@link Application}s are started in an arbitrary order.
   * <p>
   * The {@link Application}s are started by calling their {@link
   * Application#init()} method.
   */
  @Override
  public void start() {
    initTaskSession();
    for (Application app : idToApp.values()) {
      app.init();
    }
    started = true;
    executeTaskSession();
  }

  /**
   * Handles {@code Request}s that are received by the {@code VM}.
   * <p>
   * It creates a new {@link TaskSession}, identifies the target application of
   * the {@link Request}, passes execution to the application's {@link
   * Application#handle(simizer.Node, simizer.requests.Request)} method, and
   * then starts execution of the {@link TaskSession}.
   *
   * @param source the source {@link Node} of the {@link Request}
   * @param request the {@link Request} that was sent
   */
  @Override
  public void onRequestReceived(Node source, Request request) {
    activeRequestCount++;
    
    initTaskSession();
    Application app = idToApp.get(request.getAppId());

    if (app == null) {
      request.reportErrors(1);
      this.sendResponse(request, source);
    } else {
      app.handle(source, request);
    }
    executeTaskSession();
  }

  /**
   * Sends the specified {@code Request} to the specified {@code Node}.
   * <p>
   * This method differs from {@link Network#send(simizer.Node,
   * simizer.network.MessageReceiver, simizer.requests.Request, long)} in that
   * it won't use the {@link Network} if the {@code destination} is this {@code
   * VM}.  This saves time because it is not necessary to send the {@link
   * Message} over the {@link Network}.
   *
   * @see Network#send(simizer.Node, simizer.network.MessageReceiver, simizer.requests.Request, long)
   * 
   * @param destination the destination of the {@link Request}
   * @param request the {@link Request} to send
   * @param timestamp the timestamp when the {@link Request} should be sent
   */
  public void send(MessageReceiver destination, Request request, long timestamp) {
    if (destination == this) {
      Message message = new Message(this, this, request);

      this.registerEvent(
          new MessageReceivedEvent(timestamp, message, this));
    } else {
      getNetwork().send(this, destination, request, timestamp);
    }
  }

  /**
   * Computes the data access time for the given {@code DiskTask}.
   *
   * @param task the {@link DiskTask} for which to calculate the length
   * @return the data access time
   */
  public long getTaskLength(DiskTask task) {
    int resourceId = task.getResource().getId();

    switch (task.getType()) {
      case READ:
        if (cache.contains(resourceId)) {
          return cache.getReadDelay(resourceId, task.getSize());
        } else {
          return disk.getReadDelay(resourceId, task.getSize());
        }

      case WRITE:
      case MODIFY:
        return disk.getWriteDelay(resourceId, task.getSize());

      // This should never happen, unless changes are made and those changes
      // aren't reflected here.  If that is the case, this will help catch the
      // bug.
      default:
        throw new RuntimeException("Invalid task type.");
    }
  }

  /**
   * Commits the action for the specified {@code DiskTask} to disk.
   * <p>
   * After the "delay" for the {@link DiskTask} has finished, this method is
   * responsible for committing the operation to the permanent storage.  The
   * changes are not available for reading until they have been committed.
   * 
   * @param task the {@link DiskTask} to commit
   */
  public void commitDiskTask(DiskTask task) {
    Resource res = task.getResource();

    switch (task.getType()) {
      case READ:
        cache.write(res);
        break;
      case WRITE:
        cache.write(res);
        disk.write(res);
        break;
      case MODIFY:
        cache.modify(res);
        disk.modify(res);
        break;
    }
  }

  public void commitSendTask(SendTask task) {
    
  }

  public int getMaximumActiveRequestsCount() {
    long size = 0;
    long count = 0;

    for (Resource r : disk.getResources()) {
      size += r.size();
      count++;
    }

    long average = size / count;
    return (int) (this.memorySize / average);
  }

  /**
   * Initializes a new {@code TaskSession} to schedule {@code Task}s.
   * <p>
   * A new {@link TaskSession} is created for each {@link Request} that is
   * handled.  In addition, a new {@link TaskSession} is created for the
   * initialization phase of each {@link Application}.
   */
  private void initTaskSession() {
    if (currentTaskSession == null) {
      currentTaskSession = new TaskSession(0);
    }
  }

  /**
   * Runs the current {@code TaskSession}.
   * <p>
   * When a request or initialization operation has finished running, the
   * associated {@code TaskSession} is given to the {@link TaskProcessor} to be
   * executed.
   */
  private void executeTaskSession() {
    getProcessingUnit().scheduleTask(currentTaskSession, clock);
    currentTaskSession = null;
  }

  /**
   * Called when the {@code TaskSession} has ended.
   *
   * @param taskSession the {@link TaskSession} that ended
   * @param timestamp the timestamp when the {@link TaskSession} ended
   */
  public void endTaskSession(TaskSession taskSession, long timestamp) {
    this.clock = timestamp;
  }

  /**
   * Adds a read {@code Task} to the current {@code TaskSession}.
   *
   * @param resourceId the ID of the resource to read
   * @param size the number of bytes to read.  This parameter does not need to
   *            be equal to the size of the file.  If it is less than the size
   *            of the file, it implies that only part of the file is being
   *            read.
   * @return the {@link Resource} if the operation is successful, null if it
   *         fails
   */
  public Resource read(int resourceId, int size) {
    Resource res = disk.read(resourceId);

    if (res != null) {
      currentTaskSession.addTask(new DiskTask(this, size, res, IOType.READ));
    }

    return res;
  }

  /**
   * Adds a read {@code Task} to the current {@code TaskSession}.
   * <p>
   * This method simulates reading the entire {@link Resource} from the disk.
   * 
   * @param resourceId the ID of the resource to read
   * @return the {@link Resource} if the operation is successful, null if it
   *         fails
   */
  public Resource read(int resourceId) {
    Resource res = disk.read(resourceId);
    if (res != null) {
      return read(resourceId, (int) res.size());
    }
    return res;
  }

  /**
   * Adds a write {@code Task} to the current {@code TaskSession}.
   *
   * If the Resource already exists on the local disk, it is modified.
   *
   * @see Resource
   * @see IOType
   * @param res
   * @param sz
   * @return 0 if created, -1 if failure to write (eg. not enough space).
   */
  public int write(Resource res, int sz) {
    if (disk.getUsedSpace() + sz > disk.getCapacity()) {
      return -1;
    }

    currentTaskSession.addTask(
            new DiskTask(this, sz, res, IOType.WRITE));
    return 0;
  }

  /**
   * Adds a MODIFY DiskTask to the current task Session.
   *
   * @param resourceId
   * @param sz Size after modification
   * @return Version number of the resource, 0 if created, -1 if failure to
   * write.
   */
  public int modify(int resourceId, int sz) {
    if (!disk.contains(resourceId)) {
      return -1;
    }
    Resource res = disk.read(sz);
    if (disk.getUsedSpace() + (sz - res.size()) > disk.getCapacity()) {
      return -1;
    }

    currentTaskSession.addTask(
            new DiskTask(this, sz, res, IOType.MODIFY));
    return res.getVersion() + 1;
  }

  /**
   * Adds a processing {@code Task} to the current {@code TaskSession}.
   *
   * @param nbInstructions
   * @param memSize
   * @param res
   * @return 0 if success
   */
  public int execute(long nbInstructions, int memSize, List<Resource> res) {
    currentTaskSession.addTask(new ProcTask(nbInstructions, memSize));
    return 0;
  }

  /**
   * Starts a requesting session, Adds a send task to current task session, this
   * request waits for an answer before continuing to the next task in the
   * session Request handling should return after calling this method.
   *
   * @return
   * @see Application.sendRequest
   * @param dest
   * @param req
   */
  public boolean sendRequest(Node dest, Request req) {
    //req.setClientStartTimestamp(clock);
    if (getNetwork().getNode(dest.getId()) != null) {
      currentTaskSession.addTask(new SendTask(req, dest, this));
      // currentTaskSession.complete();
      return true;
    }
    return false;
  }

  /**
   * Sends back a response to a request. Completes the current tasks session.
   * Request handling should return after calling this method.
   *
   * @return
   * @see Application
   * @param dest
   * @param req
   */
  public boolean sendResponse(Request req, Node dest) {
    if (getNetwork().getNode(dest.getId()) == null || req.getClientStartTimestamp() < 0) {
      return false;
    }

    // target node exists and request is valid
    currentTaskSession.addTask(new SendTask(req, dest, this));

    return true;
  }





  @Override
  public Channel getOutputChannel() {
    return eventProducer.getOutputChannel();
  }

  @Override
  public void registerEvent(Event event) {
    eventProducer.registerEvent(event);
  }

  @Override
  public boolean cancelEvent(Event event) {
    return eventProducer.cancelEvent(event);
  }

  @Override
  public void setChannel(Channel channel) {
    eventProducer.setChannel(channel);
    processor.setChannel(channel);
  }

}
