package fr.isep.simgridizer.testing;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;

import fr.isep.simgridizer.app.Application;
import fr.isep.simizer.requests.Request;

public class PongApp extends Application {
	
	public PongApp(Integer id,long memsize, Host host) throws HostNotFoundException {
		super(id,memsize,host);
	}
	public PongApp(PongApp pongApp, Request req, String orig) throws HostNotFoundException {
		this(pongApp.getId(),pongApp.getMemorySize(),pongApp.getHost());
	}

	@Override
	public void init() {
		System.out.println("Started ponging");
	}

	@Override
	public void handle(String orig, Request request) {
	
		if(request.getAction().equals("ping")) {
			request.set("response", "pong");
			this.sendResponse(orig, request);
		}

	}

	

}
