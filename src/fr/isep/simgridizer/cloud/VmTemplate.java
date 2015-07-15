package fr.isep.simgridizer.cloud;

public class VmTemplate {
	
	private int nbCore, netBw;
	private long ramSize;
	
	public VmTemplate(int nbCore, long ramSize, int netBw) {
		this.nbCore = nbCore;
		this.ramSize = ramSize;
		this.netBw  = netBw;
	}

	public int getNbCore() {
		return nbCore;
	}

	public long getRamSize() {
		return ramSize;
	}

	public int getNetBw() {
		return netBw;
	}



}
