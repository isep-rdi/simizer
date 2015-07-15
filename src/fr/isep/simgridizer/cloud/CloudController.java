package fr.isep.simgridizer.cloud;

import java.util.ArrayList;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;

import fr.isep.simgridizer.app.Application;
import fr.isep.simizer.requests.Request;

public abstract class CloudController extends Application {
	
	public static final Integer APP_ID = 3;
	private int hypervisorId;
	protected ArrayList<Host> managedHypervisors;
	
	
	public CloudController(long memorySize, Host host) throws HostNotFoundException {
		super(APP_ID, memorySize, host);
	}
	@Override
	public void init() {
		
	}
	
	
	@Override
	public void handle(String orig, Request request) {
		Host hypervisor;
		switch (request.getAction()) {
		case "register":
			
			try {
				hypervisor = Host.getByName(request.getParameter("name"));
				managedHypervisors.add(hypervisor);
				Request config = new Request(MonitoringAgent.MONITORING_AGENT, "configure", orig, false);
				sendOneWay(hypervisor.getName(), config);
				
			} catch (HostNotFoundException | NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "unregister":
		    try {
				hypervisor = Host.getByName(request.getParameter("name"));
				 managedHypervisors.remove(hypervisor);
			} catch (HostNotFoundException | NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		    
		    break;
		case "deploy":
			String imageId 	  = request.getParameter("imageId"),
				   templateId = request.getParameter("templateId"),
				   tenantId   = request.getParameter("tenantId");
			Integer nbInstances = Integer.parseInt(request.getParameter("nbInstances"));
			
			place(tenantId, imageId, templateId, nbInstances);
			break;
		case "undeploy":
			String instanceName = request.getParameter("instanceName");
			
			break;
		
		default: break; //ignores unknown messages.
	}
		
	}
	public abstract void place(String tenantId, String imageId, String templateId, Integer nbInstances);
}
