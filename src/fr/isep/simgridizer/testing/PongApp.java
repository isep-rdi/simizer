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
		this(pongApp.getId(),pongApp.getMemorySize(),pongApp.host);
		this.req = req;
		this.orig = orig;
		
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

	@Override
	public Application getInstance(String orig,Request req) {
		
		PongApp instance = null;
			try {
				instance = new PongApp(this,  req, orig);
			} catch (HostNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return instance;
	}

}
