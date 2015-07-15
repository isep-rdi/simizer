package fr.isep.simgridizer.schedulers;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;

import fr.isep.simgridizer.cloud.CloudController;

public class FCFSScheduler extends CloudController {

	public FCFSScheduler(long memorySize, Host host) throws HostNotFoundException {
		super(memorySize, host);
		
	}

	@Override
	public void place(String tenantId, String imageId, String templateId,Integer nbInstances) {
		
		
	} 
	
}
