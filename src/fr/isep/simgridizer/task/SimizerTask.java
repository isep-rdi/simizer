package fr.isep.simgridizer.task;

import org.simgrid.msg.Task;

import fr.isep.simizer.requests.Request;

/**
 * Wrapper around simizer request as SimGrid Task
 * @author slefebvr
 *
 */
public class SimizerTask extends Task {
	private final Request request;
	
	
	
	public SimizerTask(Request req) {
		super(req.getAction(), 0.0,req.getSize());
		
		this.request = req;
		
	}
	
	public Request getRequest() {
		return request;
	}

}
