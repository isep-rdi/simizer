package fr.isep.simgridizer.task;


import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;
import org.simgrid.msg.Task;

import fr.isep.simgridizer.app.Application;
import fr.isep.simizer.requests.Request;

public class Server extends Process {
	
	private final Application app;
	
	
	public Server(Host host, Application app) {
		super(host, "server_" + app.getId().toString());
		this.app = app;
		app.setHost(this.host);
	}

	@Override
	public void main(String[] args) throws MsgException {
		System.out.println("Starting server. Mailbox  is: " + host.getName() + ":" + app.getId().toString() );
		while (true) {
			SimizerTask task;
			try {
				task = (SimizerTask) Task.receive(host.getName() + ":" + app.getId().toString());
				Host orig = task.getSource();
				
				Request req = task.getRequest();
				
				if (req.getAction().equals("stop")) break;

				Application handler = app.getInstance(orig.getName(), req);
				handler.start();
				
				
			} catch (MsgException e) {
				Msg.debug("Received failed. I'm done. See you!");
				break;
			}

		}
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
