package fr.isep.simgridizer.app;

import java.util.Properties;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;
import org.simgrid.msg.TaskCancelledException;
import org.simgrid.msg.TimeoutException;
import org.simgrid.msg.TransferFailureException;

import fr.isep.simgridizer.task.SimizerTask;
import fr.isep.simizer.nodes.Node;
import fr.isep.simizer.requests.Request;

/**
 * Represents a running process on VM.
 * <p>
 * Implement the {@link #handle(Node, Request)} method for application-specific
 * behavior.
 *
 * @author Sylvain Lefebvre
 */
public abstract class Application extends Process {

	/** The ID of the {@code Application}. */
	private Integer id;

	/** The amount of memory used by this {@code Application}. */
	private long memorySize;

	/** Contains user-specific properties for the {@code Application}. */
	protected Properties config = new Properties();

	private boolean running;
	
	/**
	 * Initializes a new instance of the class.
	 *
	 * @param id
	 *            the ID of the application
	 * @param memorySize
	 *            the memory footprint for the application
	 * @throws HostNotFoundException 
	 */
	public Application(Integer id, long memorySize, Host host) throws HostNotFoundException {
		super(host,id.toString());
		this.id = id;
		this.memorySize = memorySize;
		
	}

	/**
	 * Initializes an {@code Application} from a template.
	 * <p>
	 * The configuration details of the application are copied (ID, memorySize,
	 * and custom-defined properties), but the {@link VM} reference is not. The
	 * purpose of this constructor is to be able to easily deploy the same
	 * application on multiple machines.
	 *
	 * @param template
	 *            the {@code Application} that should serve as a template when
	 *            creating this one
	 */
	public Application(Application template, Host host) {
		super(host, template.id.toString());
		this.id = template.id;
		this.memorySize = template.memorySize;
		// TODO: I don't think we want to copy this as a reference.
		this.config = template.config;
		
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
	public long getMemorySize() {
		return memorySize;
	}

	/**
	 * Sets a configuration option for the {@code Application}.
	 *
	 * @param key
	 *            the key of the property
	 * @param value
	 *            the value for the property
	 */
	public void setConfig(String key, String value) {
		config.setProperty(key, value);
	}

	

	/**
	 * Call this to execute code
	 * 
	 * @param nbFlops
	 */
	public void execute(double nbFlops) {

		Task task = new Task(this.getId().toString(), nbFlops, 0.0);
		try {
			task.execute();
		} catch (HostFailureException e) {

			e.printStackTrace();
		} catch (TaskCancelledException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Creates a resquest and sends it, waiting synchronously for a response.
	 * <p>
	 * Using this method waits for an answer, received in the form of a Request
	 * before continuing to the next task in the session (With simgrid it
	 * should. Waits to receive on a specific mailbox.
	 * 
	 * @param destination
	 *            where the {@link Request} should be sent
	 * @param request
	 *            the {@link Request} to send
	 * @return whether or not the {@link Request} could be sent to the specified
	 *         {@link Node}
	 */
	public Request sendRequest(String destination, Request request) {

		SimizerTask sending = new SimizerTask(request);
		boolean success = true;
		try {
			System.out.println(destination +":"+ request.getApplicationId());
			sending.send(destination +":"+ request.getApplicationId());
		} catch (TransferFailureException | HostFailureException
				| TimeoutException e) {
			
			System.out.println("FAILED " + e.getMessage());
			success = false;
		}

		if (!success)
			return null;

		SimizerTask response = null;

		try {
			response = (SimizerTask) SimizerTask.receive(this.getHost().getName() + ":"
					+ this.getId().toString() + ":" + request.getId());
		} catch (TransferFailureException | HostFailureException
				| TimeoutException e) {

			e.printStackTrace();
			success = false;
		}

		if (!success)
			return null;

		return response.getRequest();
	}

	/**
	 * Sends back a response to a request.
	 * <p>
	 * Completes the current {@link TaskSession}. Request handling should return
	 * after calling this method. (This needs to be updated with the actual
	 * behavior of the application.)
	 *
	 * @param request
	 *            the {@link Request} (or rather, response) to send
	 * @param destination
	 *            the {@link Node} where the {@link Request} should be sent
	 * @return returns false if the {@link Request} cannot be sent to the
	 *         specified {@link Node} OR if the {@link Request} is not in its
	 *         "response" state. (Meaning that the {@link Request} should have
	 *         already been sent by the client.)
	 */
	public boolean sendResponse(String orig, Request request) {
		boolean result = true;
		String mailbox = orig + ":"
				+ request.getApplicationId().toString() + ":" + request.getId();

		SimizerTask response = new SimizerTask(request);
		try {
			response.send(mailbox);
		} catch (TransferFailureException | HostFailureException
				| TimeoutException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * Sends a {@code Request} without waiting for a response.
	 * <p>
	 * This needs to be updated once the intended behavior is determined and
	 * implemented. However, given the implementation of this method, I suspect
	 * that the intended behavior was never fully implemented.
	 * 
	 * @param node
	 *            the {@link Node} where the {@link Request} should be sent
	 * @param request
	 *            the {@link Request} to send
	 * @return whether or not {@code request} could be sent to {@code node}
	 * 
	 */
	public boolean sendOneWay(String target, Request request) {
		boolean result = true;

		String mailbox = target + ":"	+ request.getApplicationId().toString();
		SimizerTask toSend = new SimizerTask(request);
		Msg.info("Sending to:" + mailbox);
		toSend.isend(mailbox);
		
		return result;
	}
	protected boolean getRunning() {
		return this.running;
	}
	public void main(String[] args) {
		this.running = true;
		init();
		String mailbox = this.getHost().getName() + ":" + getId().toString();
		Msg.info("Starting on " + this.getHost().getName() + " listening to " + mailbox);

		while (running) {
			SimizerTask task;
			try {
				task = (SimizerTask) Task.receive(mailbox);
				Host orig = task.getSource();
				Request req = task.getRequest();
				
				if (req.getAction().equals("stop")) {
					stop();
				}
				else {
					handle(orig.getName(), req);
				}
			} catch (MsgException e) {
				Msg.debug("Received failed. I'm done. See you!");
				break;
			}

		}
		
	
	}
	

	public void stop() {
		this.running = false;
		
	}

	/**
	 * Performs initialization logic when the {@code Application} starts
	 * running.
	 * <p>
	 * Subclasses must override this method to provide application-specific
	 * logic.
	 *
	 * @param scheduler
	 *            the {@link TaskScheduler} where the actions should be put
	 */
	public abstract void init();

	/**
	 * Handles requests from clients.
	 * <p>
	 * Subclasses must override this method with their application-specific
	 * logic. The method is used to handle requests sent from clients to the
	 * server.
	 *
	 * @param scheduler
	 *            the {@link TaskScheduler} where the actions should be put
	 * @param orig
	 *            the {@link Node} that sent the {@link Request}. This is the
	 *            {@link Node} where the server should send its response.
	 * @param request
	 *            the {@link Request} sent by the client
	 */
	public abstract void handle(String orig, Request request);


	

}
