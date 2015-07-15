package fr.isep.simgridizer.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.simgrid.msg.File;
import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.VM;

import fr.isep.simgridizer.app.Application;
import fr.isep.simgridizer.cloud.exceptions.ImageNotFoundException;
import fr.isep.simizer.requests.Request;
/**
 * Handles vms on a physical host
 * 
 * 
 * @author slefebvr
 *
 */
public class Hypervisor extends Application {
	public static final String MOVE = "move";
	public static final String DEPLOY = "deploy";
	public static final String UNDEPLOY = "undeploy";

	private static final Random RND = new Random(System.currentTimeMillis());
	private double ramOver, cpuOver; // Over - subscription rates
	private String imageStore, imagePath, diskPath;
	private int migNetBw; 
	
	private HashMap<String, VmImage>  imageCache = new HashMap<>();
	private HashMap<String, VM> vmMap = new HashMap<>();
	private MonitoringAgent ma;
	
	public VM [] getVms() {
		return vmMap.values().toArray(new VM[] {});
	}
	public Hypervisor(Integer id, long memorySize, Host host) throws HostNotFoundException {
		super(id,memorySize,host);
		
	}

	/**
	 * This starts a monitoring agent on the host.
	 * Reads the configuration file for overcommitment values.
	 * 
	 */
	@Override
	public void init() {
		ramOver= 1.5;
		cpuOver= 16.0;
		try {
			this.ma = new MonitoringAgent(MonitoringAgent.MONITORING_AGENT, 128, this.getHost(), this);
			this.ma.start();
		} catch (HostNotFoundException e) {
			Msg.info("This is very bad if this happens");
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void handle(String orig, Request request) {
		switch (request.getAction()) {
		
			case DEPLOY:
				String imageId = request.getParameter("imageId"),
					   templateId = request.getParameter("templateId");
				String address = deploy(imageId, new VmTemplate(1, 1024, 10));
				
			break;
			case UNDEPLOY:
				String instanceName = request.getParameter("instanceName");
				undeploy(instanceName);
			break;
			case MOVE:
				String hostName = request.getParameter("targetHost");
				String targetVm = request.getParameter("targetVm");
				
				try {
					Host target = Host.getByName(hostName);
					VM vm = vmMap.get(targetVm);
					vm.migrate(target);
					
				} catch (HostNotFoundException | NullPointerException e) {
					e.printStackTrace();
					
				} catch (HostFailureException e) {
					Msg.info("failed to migrate");
					request.set("status", "failed");
				}
				break;
				default: break; //ignores unknown messages.
		}
		
	}
	
	/**
	 * Stops the vm instance given in parameter
	 * @param instanceName
	 */
	public void undeploy(String instanceName) {
		
		VM  vm = vmMap.get(instanceName);
		vm.shutdown();
		vmMap.remove(instanceName);
		
	}
	/**
	 * Stops the hypervisor	
	 */
	public void stop() {
		Msg.info("Stopping vms on " + this.getHost().getName());
		super.stop();
	}
	/**
	 * Tries to deploy locally the specified VM image.
	 * Checks in local cache if image is available, otherwise copies it from storage system.
	 * Throws exception when vm too big.
	 * @param imageId
	 * @return A mailbox address if the deployment is successful, null if it failed 
	 */
	public String deploy(String imageId, VmTemplate vt) {
		VmImage vi = null;
		//0. Check if template fits on host or left to scheduler ?
		
		//1. retrieve image
		if(!imageCache.containsKey(imageId)) {
			try {
				 vi = retrieveImage(imageId);
				imageCache.put(vi.getId(), vi);
			} catch (ImageNotFoundException e) {
				e.printStackTrace();
			}
		}
		//3. Generate and start 
		if(vi==null)
			return null;

		return	startNewVm(vi,vt);
	}

	public VmImage retrieveImage(String imageId) throws ImageNotFoundException {
		/*
		 * File imgFile = new File(imageStore, imagePath + imageId);
		 
		int nbRead=1024*1024*1024;
		long actualRead=nbRead;
		while(nbRead==actualRead) {
			actualRead = imgFile.read(nbRead, nbRead);
		}*/
		return VmImageFactory.getVmImage(imageId);
	}

	private String startNewVm(VmImage vmImage, VmTemplate vt) {
		
		String vmName = this.getHost().getName() + vmImage.getId() + RND.nextInt();
		VM vm = vmImage.generateVm(this.getHost(),vmName , vt, migNetBw, diskPath);
		vmMap.put(vmName,vm);
		return vmName;
		
	}

}
