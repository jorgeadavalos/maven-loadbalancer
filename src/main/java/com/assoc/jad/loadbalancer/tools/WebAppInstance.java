package com.assoc.jad.loadbalancer.tools;

public class WebAppInstance {
	private int ctr;
	private String instance;
	private String welcome;
	private boolean instanceUp;
	private boolean serverUp;
	//instance=http://localhost:18080, instanceUp=false, ping=4, welcome=/index.xhtml, serverUp=true}
	
	public int getCtr() {
		return ctr;
	}
	public void setCtr(int ctr) {
		this.ctr = ctr;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public String getWelcome() {
		return welcome;
	}
	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}
	public boolean isInstanceUp() {
		return instanceUp;
	}
	public void setInstanceUp(boolean instanceUp) {
		this.instanceUp = instanceUp;
	}
	public boolean isServerUp() {
		return serverUp;
	}
	public void setServerUp(boolean serverUp) {
		this.serverUp = serverUp;
	}
}
