package fr.isep.simgridizer.cloud;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.simgrid.msg.Host;
import org.simgrid.msg.HostNotFoundException;
import org.simgrid.msg.VM;

import fr.isep.simgridizer.app.AppTemplate;
import fr.isep.simgridizer.app.Application;

public class VmImage {
	private final long size;
	private String id;
	private List<String> appList = new ArrayList<String>();
	
	public VmImage(String id, long size) {
		this.id 	= id;
		this.size 	= size;
	}
	public long getSize() {
		return size;
	}
	public String getId() {
		return id;
	}
	
	public void addApplication(String appName) {
		appList.add(appName);
	}
	public List<String> getAppList() {
		return appList;
	}
	
	public VM generateVm(Host host, String name, VmTemplate vmTemplate,int migNetBW, String diskPath) {
		
		 VM instance = new VM(host, name, 
				 			vmTemplate.getNbCore(), 
				 			(int) vmTemplate.getRamSize(), 
				 			vmTemplate.getNetBw(), 
				 			diskPath, 
				 			(int) size, 
				 			migNetBW, 0);
		 
		 instance.start();
		 
		 for(String appName: appList) {
			 try {
				// maybe change this for a factory, and using config in the Applications
				AppTemplate at = AppTemplate.getTemplate(appName);
				Class<Application> appClass =  (Class<Application>) Class.forName(appName);
				
				Application app = (Application) appClass.getConstructor(Integer.class, long.class, Host.class)
														.newInstance(at.getId(),at.getMemorySize(),instance);
				
				app.start();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HostNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 return instance;
	}
}
