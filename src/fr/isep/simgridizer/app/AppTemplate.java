package fr.isep.simgridizer.app;

import java.util.HashMap;

public class AppTemplate {
	
	static final HashMap<String, AppTemplate> templates = new HashMap<>();
	
	final long memorySize;
	final Integer id;
	
	
	public AppTemplate(Integer id, long memorySize) {
		this.id = id;
		this.memorySize = memorySize;
	}
	
	public long getMemorySize() {
		return memorySize;
	}
	
	public Integer getId() {
		return id;
	}
	
	public static AppTemplate getTemplate(String appName) {
	// TODO Auto-generated method stub
		return templates.get(appName);
	}
	/**
	 * Reads the application description file, and loads descriptions 
	 * @param path
	 */
	public static void initTemplates(String path) {
		addTemplate("fr.isep.simgridizer.testing.PongApp", new AppTemplate(1,1024));
	}
	public static void addTemplate(String className, AppTemplate at) {
		templates.put(className, at);
	}
	
}
