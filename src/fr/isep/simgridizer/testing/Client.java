package fr.isep.simgridizer.testing;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostFailureException;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.Msg;
import org.simgrid.msg.Process;

import fr.isep.simgridizer.app.Application;
import fr.isep.simizer.requests.Request;

public class Client extends Process {

	
	private int targetApp, nbReq;
	private String  targetAddr;
	private  Application dummyApp;

	public Client(Host host, String name, String targetAddr, int targetApp, int nbReq) {
		super(host,name, null);
		this.nbReq = nbReq;
		this.targetApp = targetApp;
		this.targetAddr = targetAddr;
		
		try {
			this.dummyApp = new Application( 1,1,host) {

				@Override
				public void init() {}

				@Override
				public void handle(String orig, Request request) {}
				
			};
		} catch (HostNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	@Override
	public void main(String [] args) {
		System.out.println("Started Client sending to:  " +  targetAddr + ":" + targetApp);
		int i = 0;
		while(i < nbReq) {
			Request toSend  = new Request(targetApp,
											"ping", 
											"test=1",
											false);
			double start = Msg.getClock();	      
			toSend.setClientStartTimestamp(Math.round(start));
			
			Request resp = dummyApp.sendRequest(targetAddr, toSend);
			double end = Msg.getClock() - start;
			
			resp.setClientEndTimestamp(Math.round(end));
			
			System.out.println("request " + i +
								" ended in " + 
								end + " : " + 
								resp.getErrorCount() + " : " + 
								resp.get("response"));
			i++;
		}
		
		Request stopRequest = new Request(targetApp,"stop", "",false);
		dummyApp.sendOneWay(targetAddr, stopRequest);
		
		Request stopHPRequest = new Request(2, "stop","",false);
		dummyApp.sendOneWay("host1", stopHPRequest);
		
		
	}

}
