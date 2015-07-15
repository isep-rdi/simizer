package fr.isep.simgridizer.testing;

import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
import org.simgrid.msg.MsgException;

import fr.isep.simgridizer.app.AppTemplate;
import fr.isep.simgridizer.cloud.Hypervisor;
import fr.isep.simgridizer.cloud.VmTemplate;


public class RunTest {
	
	public static void main(String[] args) throws MsgException {       
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
		AppTemplate.initTemplates("");
		Msg.info("Start "+ hostNB +"  hosts");
		Hypervisor hp =  new Hypervisor(2, 1000, hosts[1]);
		hp.start();
		
		String name = hp.deploy("pong_vm", new VmTemplate(1, 1024, 10));
		Msg.info("Deployed vm " + name);
		Msg.info("Server started ");
		Client cli = new Client(hosts[0], "client",name, 1, 10);
		cli.start();
		//hp.stop();
		/* Execute the simulation */
		Msg.run();
		
		
    }

}
