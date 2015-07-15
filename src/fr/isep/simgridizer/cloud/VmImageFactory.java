package fr.isep.simgridizer.cloud;


/**
 * Manages vm images
 * @author slefebvr
 *
 */
public class VmImageFactory {

	public static VmImage getVmImage(String imageId) {
		VmImage vmi =  new VmImage("pong_vm",100000);
		vmi.addApplication("fr.isep.simgridizer.testing.PongApp");
		
		return vmi;
		
		
	}

}
