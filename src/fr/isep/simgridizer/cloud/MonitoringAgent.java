package fr.isep.simgridizer.cloud;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.VM;

import fr.isep.simgridizer.app.Application;
import fr.isep.simizer.requests.Request;

public class MonitoringAgent extends Application {
	public static final int MONITORING_AGENT = 10;
	
	public static final String REPORT_ACTION = "report";

	private final Hypervisor hv;
	private double reportFreq;
	private String targetCC;
	private boolean configured = false;

	public MonitoringAgent(Integer id, long memorySize, Host host, Hypervisor hv)
							throws HostNotFoundException {
		super(id, memorySize, host);
		this.hv = hv;
		this.reportFreq = 1.0;
	}

	@Override
	public void init() {		
	}

	/**
	 * Waits for an initial "configure" then  "start" requests from the hypervisor, or the CC 
	 * and frequency of reporting.
	 */
	@Override
	public void handle(String orig, Request request) {
		
		
		switch(request.getAction()) {
		case "configure":
			double freq = Double.parseDouble(request.getParameter("frequency"));
			String target = request.getParameter("cloudcontroller");
			configureMonitoring(target, freq);
			break;
		case "start":
			startReporting();
			break;
		}
		
	}

	private void startReporting() {
		while(!configured && getRunning()) {
			try {
				this.waitFor(reportFreq);
			} catch (HostFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while(configured && getRunning()) {
			generateAndSendInfo();
			try {
				this.waitFor(reportFreq);
			} catch (HostFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Reads metrics from host and vms and reports it to the configured cloud controller 
	 */
	private void generateAndSendInfo() {
		Host host = this.getHost();
		VM [] vms = hv.getVms();
		
		Request report = new Request(CloudController.APP_ID,"report","",false);
		
		JSONObject hostDesc= new JSONObject();
		hostDesc.put("name", host.getName());
		hostDesc.put("ramSize", host.getProperty("ramsize"));
		hostDesc.put("nbCore", host.getCoreNumber());
		hostDesc.put("speed", host.getSpeed());
		hostDesc.put("nbTasks", host.getLoad());
		
		long currentRam, currentCpu;
		
		JSONArray vmArray = new JSONArray();
		for(int i = 0; i<vms.length; i++) {
			VM vm = vms[i];
			JSONObject vmDesc = new JSONObject();
			vmDesc.put("name", vm.getName());
			vmDesc.put("nbCore", vm.getCoreNumber());
			vmDesc.put("speed", vm.getSpeed());
			vmDesc.put("nbTasks", vm.getLoad());
			vmArray.add(vmDesc);
			
		}
		report.set("host", hostDesc.toJSONString());
		report.set("vms", vmArray.toJSONString());
		sendOneWay(targetCC,report);
		
		
	
		
	}

	private synchronized  void configureMonitoring(String target, double freq) {
		this.configured  = true;
		this.targetCC = target;
		this.reportFreq = freq;
	}

	

	

		
	
}
