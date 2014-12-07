package fr.isep.simizer.nodes;

import fr.isep.simizer.app.Application;
import fr.isep.simizer.event.Channel;
import fr.isep.simizer.event.Event;
import fr.isep.simizer.event.EventProducer;
import fr.isep.simizer.event.IEventProducer;
import fr.isep.simizer.event.MessageReceivedEvent;
import fr.isep.simizer.network.Message;
import fr.isep.simizer.network.MessageReceiver;
import fr.isep.simizer.network.Network;
import fr.isep.simizer.processor.ProcessingUnit;
import fr.isep.simizer.processor.tasks.DiskTask;
import fr.isep.simizer.processor.tasks.ProcTask;
import fr.isep.simizer.processor.tasks.SendTask;
import fr.isep.simizer.processor.tasks.TaskSession;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.storage.Cache;
import fr.isep.simizer.storage.IOController;
import fr.isep.simizer.storage.IOType;
import fr.isep.simizer.storage.Resource;
import fr.isep.simizer.storage.StorageElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a virtual machine in the simulation.
 * <p>
 * These machines are used to host the application-specific {@link Application}
 * instances.  A single {@code VM} can host multiple {@link Application}s.  To
 * add a new instance to the {@code VM}, use the {@link
 * #deploy(Application)} method.
 * <p>
 * In addition, this class provides a series of methods (through the use of the
 * {@link TaskScheduler} class) that can be used to perform various file,
 * network, and processor operations while in the process of handling a {@link
 * Request}.
 *
 * TODO: add a started state check
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

  /** The hourly cost of the {@code VM}. */
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

  private final Map<Integer, Application> idToApp = new HashMap<>();

  /** Whether or not the {@code VM} has been started. */
  private boolean started = false;

  // TODO: Need to count the active number of requests.

  /**
   * Initializes a VM with the specified components.
   * <p>
   * If the object-based parameters are not specified, default versions of them
   * will be created.
   *
   * @param processor the {@link ProcessingUnit} for this {@code VM} to use
   * @param disk the {@link StorageElement} that represents the hard disk
   * @param memory the amount of memory in the system
   * @param hCost the hourly cost for using the system
   */
  public VM(ProcessingUnit processor, StorageElement disk, long memory,
      double hCost) {

    super();

    this.processor = (processor != null)
            ? processor
            : new ProcessingUnit(1, 1_000, null);
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

  /**
   * Returns the processor for this {@code VM}.
   * 
   * @return the processor for this {@code VM}
   */
  public ProcessingUnit getProcessingUnit() {
    return this.processor;
  }

  /**
   * Returns the most recent simulation timestamp known to this {@code VM}.
   *
   * @return the most recent simulation timestamp known to this {@code VM}
   */
  public long getClock() {
    return this.clock;
  }

  /**
   * Returns the hourly cost associated with this {@code VM}.
   * 
   * @return the hourly cost associated with this {@code VM}
   */
  public double getCost() {
    return hCost;
  }

  /**
   * Returns the number of active {@code Request}s being handled by this system.
   * <p>
   * This parameter is useful when implementing certain kinds of load balancing
   * systems.
   *
   * @return the number of active {@code Request}s being handled by this system
   */
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
    idToApp.put(application.getId(), application);
    memoryUsed += application.getMemorySize();
    if (started) {
      startApplication(application);
    }
  }

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
    for (Application app : idToApp.values()) {
      startApplication(app);
    }
    started = true;
  }

  /**
   * Starts an application, creating and scheduling the initialization tasks.
   * 
   * @param application the {@link Application} to start
   */
  private void startApplication(Application application) {
    TaskScheduler scheduler = this.new TaskScheduler();
    application.init(scheduler);
    getProcessingUnit().scheduleTask(scheduler.getTaskSession(), clock);
  }

  /**
   * Handles {@code Request}s that are received by the {@code VM}.
   * <p>
   * It creates a new {@link TaskSession}, identifies the target application of
   * the {@link Request}, passes execution to the application's {@link
   * Application#handle(Node, Request)} method, and then starts execution of the
   * {@link TaskSession}.
   *
   * @param source the source {@link Node} of the {@link Request}
   * @param request the {@link Request} that was sent
   */
  @Override
  public void onRequestReceived(Node source, Request request) {
    activeRequestCount++;
    
    Application app = idToApp.get(request.getApplicationId());

    TaskScheduler scheduler = this.new TaskScheduler();
    if (app == null) {
      request.reportErrors(1);
      scheduler.sendResponse(request, source);
    } else {
      app.handle(scheduler, source, request);
    }
    getProcessingUnit().scheduleTask(scheduler.getTaskSession(), clock);
  }

  /**
   * Sends the specified {@code Request} to the specified {@code Node}.
   * <p>
   * This method differs from {@link Network#send(Node, MessageReceiver,
   * Request, long)} in that it won't use the {@link Network} if the {@code
   * destination} is this {@code VM}.  This saves time because it is not
   * necessary to send the {@link Message} over the {@link Network}.
   *
   * @see Network#send(Node, MessageReceiver, Request, long)
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

  /**
   * Called when a {@code SendTask} is finished.
   * <p>
   * This provides an opportunity for the {@code VM} to perform any necessary
   * cleanup.
   *
   * @param task the {@link DiskTask} that was just finished
   */
  public void commitSendTask(SendTask task) {
    // TODO: Decrement the number of active requests.
  }

  /**
   * Returns the maximum expected number of resources simultaneously in memory.
   *
   * @return the maximum expected number of resources simultaneously in memory.
   *         That is to say that, given the size of the current resources, we
   *         expect to be able to store this number.
   */
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
   * Called when the {@code TaskSession} has ended.
   *
   * @param taskSession the {@link TaskSession} that ended
   * @param timestamp the timestamp when the {@link TaskSession} ended
   */
  public void endTaskSession(TaskSession taskSession, long timestamp) {
    this.clock = timestamp;
  }

  /**
   * Handles the scheduling of tasks to complete a request/operation.
   * <p>
   * As I was working with the framework, I found it slightly confusing to know
   * which methods would add an operation to the {@link TaskSession} and which
   * ones wouldn't.  Most of the ones here did, but it wasn't necessarily all
   * of them (for example, see {@link #send(Request, MessageReceiver, long)}).
   * To combat this problem, I added a class where <strong>all</strong> of the
   * methods add an operation with a {@link Task}.  This instance is passed to
   * the various handlers so that they can construct the {@link TaskSession}.
   * <p>
   * In addition, there are a fair number of methods throughout the application
   * that have very similar (or the same) names.  This will hopefully help to
   * distinguish some of them.  For example, when you use {@code
   * scheduler.read()}, it should be obvious that its doing the same thing as
   * the {@code read()} method, but with the assistance of the {@code
   * scheduler}.
   */
  public class TaskScheduler {
    private final TaskSession session;

    /**
     * Initializes an instance of {@code TaskScheduler} for this {@code VM}.
     */
    public TaskScheduler() {
      this.session = new TaskSession(0);
    }

    private TaskSession getTaskSession() {
      return session;
    }

    /**
     * Returns the {@code VM} for this {@code TaskScheduler}.
     *
     * @return the {@code VM} for this {@code TaskScheduler}
     */
    public VM getVM() {
      return VM.this;
    }

    /**
    * Adds a read {@code Task} to the {@code TaskSession}.
    *
    * @param resourceId the ID of the resource to read
    * @param size the number of bytes to read.  This parameter does not need to
    *            be equal to the size of the file.  If it is less than the size
    *            of the file, it implies that only part of the file is being
    *            read.
    * @return the {@link Resource} if the operation is successful, null if it
    *         fails
    */
    public Resource read(Integer resourceId, long size) {
      Resource res = disk.read(resourceId);
      if (res != null) {
        session.addTask(new DiskTask(VM.this, size, res, IOType.READ));
      }
      return res;
    }

    /**
    * Adds a read {@code Task} to the {@code TaskSession}.
    * <p>
    * This method simulates reading the entire {@link Resource} from the disk.
    *
    * @param resourceId the ID of the resource to read
    * @return the {@link Resource} if the operation is successful, null if it
    *         fails
    */
    public Resource read(Integer resourceId) {
     Resource res = disk.read(resourceId);
     if (res != null) {
       return read(resourceId, res.size());
     } else {
       return null;
     }
    }

    /**
     * Adds a write {@code Task} to the {@code TaskSession}.
     * <p>
     * If the Resource already exists on the local disk, it is modified.
     *
     * @param resource
     * @param size
     * @return 0 if created, -1 if failure to write (eg. not enough space).
     */
    public int write(Resource resource, long size) {
      if (disk.getUsedSpace() + size > disk.getCapacity()) {
        return -1;
      }

      session.addTask(new DiskTask(VM.this, size, resource, IOType.WRITE));
      return 0;
    }

    /**
     * Adds a modify task to the {@code TaskSession}.
     *
     * @param resourceId the ID of the {@link Resource} to modify
     * @param size the size after modification
     * @return the version number of the resource (this will be 0 if it was just
     *         created), or -1 to indicate some sort of failure
     */
    public int modify(Integer resourceId, long size) {
      if (!disk.contains(resourceId)) {
        return -1;
      }
      Resource res = disk.read(resourceId);
      if (disk.getUsedSpace() + (size - res.size()) > disk.getCapacity()) {
        return -1;
      }

      session.addTask(new DiskTask(VM.this, size, res, IOType.MODIFY));
      return res.getVersion() + 1;
    }

    /**
     * Adds a processing {@code Task} to the {@code TaskSession}.
     *
     * @param nbInstructions the number of instructions needed to complete the
     *            {@link ProcTask}
     * @param memSize the amount of memory needed to complete the {@link
     *            ProcTask}
     * @param resources the IDs of the {@link Resource}s needed to complete the
     *            {@link ProcTask}
     * @return zero, always
     */
    public int execute(long nbInstructions, long memSize, List<Resource> resources) {
      session.addTask(new ProcTask(nbInstructions, memSize));
      return 0;
    }

    /**
     * Starts a requesting session, adding a send task to {@code TaskSession}.
     * <p>
     * Using this method waits for an answer before continuing to the next task
     * in the session (sshhh... no it doesn't).  Request handling should return
     * after calling this method.  (This needs to be updated depending on what
     * the requirements are.)
     *
     * @param destination where the {@link Request} should be sent
     * @param request the {@link Request} to send
     * @return whether or not the {@link Request} could be sent to the specified
     *         {@link Node}
     */
    public boolean sendRequest(Node destination, Request request) {
      if (getNetwork().getNode(destination.getId()) != null) {
        session.addTask(new SendTask(request, destination, VM.this));
        return true;
      }
      return false;
    }

    /**
     * Sends back a response to a request.
     * <p>
     * Completes the current {@link TaskSession}.  Request handling should
     * return after calling this method.  (This needs to be updated with the
     * actual behavior of the application.)
     *
     * @param request the {@link Request} (or rather, response) to send
     * @param destination the {@link Node} where the {@link Request} should be
     *            sent
     * @return returns false if the {@link Request} cannot be sent to the
     *         specified {@link Node} OR if the {@link Request} is not in its
     *         "response" state.  (Meaning that the {@link Request} should have
     *         already been sent by the client.)
     */
    public boolean sendResponse(Request request, Node destination) {
      if (getNetwork().getNode(destination.getId()) == null || request.getClientStartTimestamp() < 0) {
        return false;
      }

      // target node exists and request is valid
      session.addTask(new SendTask(request, destination, VM.this));

      return true;
    }

    /**
     * Sends a {@code Request} without waiting for a response.
     * <p>
     * This needs to be updated once the intended behavior is determined and
     * implemented.  However, given the implementation of this method, I suspect
     * that the intended behavior was never fully implemented.
     * 
     * @param node the {@link Node} where the {@link Request} should be sent
     * @param request the {@link Request} to send
     * @return whether or not {@code request} could be sent to {@code node}
     */
    public boolean sendOneWay(Node node, Request request) {
      return sendRequest(node, request);
    }

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
