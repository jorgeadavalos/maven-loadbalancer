package com.assoc.jad.loadbalancer.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Logger;

public class HealthTask implements Runnable {

	private static final Logger LOGGER = Logger.getLogger( HealthTask.class.getName() );
	private String instanceURI;
	private String application;
	
	public HealthTask(String instanceURI, String application) {
		super();
		this.instanceURI = instanceURI;
		this.application = application;
	}

	@Override
	public void run() {
		healthCheck();
	}
	private void healthCheck() {
		
		String wrkString = instanceURI.replaceAll("http://", "");
		wrkString = wrkString.replaceAll("https://", "");
		int ndx = wrkString.indexOf(":");
		if (ndx == -1) ndx = wrkString.indexOf("/");
		
		if (ndx == -1) ndx = wrkString.length();
		String host = wrkString.substring(0, ndx);
		
		HashMap<String,HashMap<String,String>> instances = LoadBalancerStatic.applications.get(application);
		HashMap<String,String> instanceApp = instances.get(instanceURI);
		instanceApp.put(LoadBalancerStatic.SERVERUP, pingServer(host).toString());
		
		wrkString = instanceApp.get(LoadBalancerStatic.INSTANCEUP);
		if (wrkString != null && wrkString.equalsIgnoreCase("false")) return;

		instanceApp.put(LoadBalancerStatic.INSTANCEUP, pingInstance(instanceURI,application).toString());
		return;
	}
	private Boolean pingInstance(String instanceKey,String application) {
		byte[] bytes = new byte[4096];
		String wrkInstanceKey = instanceKey +"/"+application;
		try {
			URL url = new URL(wrkInstanceKey);
			URLConnection urlC = url.openConnection();
			InputStream is = urlC.getInputStream();
			is.read(bytes);
		}
		catch (IOException e) {
			LOGGER.info(() -> "URL "+wrkInstanceKey+" "+e.getMessage());
			if (e.getClass().toString().indexOf("java.net.ConnectException") != -1) return false;
			return true;
		} 
		return true;
	}
	private Boolean pingServer(String host) {
		NetworkInterface networkInterface = null;
		try {
			InetAddress inetAddress = InetAddress.getByName(host);
			return inetAddress.isReachable(networkInterface,2,3000);
		} catch (Exception e) {
			LOGGER.info(() -> "invalid host "+host);
			return false;
		}
	}
}
