package fr.isep.simgridizer.testing;

import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;



public class IsepCloud {
	public static void main(String[] args) throws MsgException {       
	    
		//Msg.nativeInit();
		Msg.init(args); 
	    int hostNB=2;
	    if (args.length < 1) {
	    	Msg.info("Usage	 : Cloud platform_file");
	    	Msg.info("Usage  : Cloud platform.xml");
	    	System.exit(1);
	    }
	    /* Construct the platform */
		Msg.createEnvironment(args[0]);
		Host[] hosts = Host.all();
		
		if (hosts.length < hostNB+1) {
			Msg.info("I need at least "+ (hostNB+1) +"  hosts in the platform file, but " + args[0] + " contains only " + hosts.length + " hosts");
			System.exit(42);
		}
		Msg.info("Start "+ hostNB +"  hosts");
		for(int i=0; i<hosts.length; i++) {
			System.out.println(hosts[i].getName() + " " + hosts[i].getCoreNumber() + " " + hosts[i].getProperty("ramsize"));
		}
		
    }
}
