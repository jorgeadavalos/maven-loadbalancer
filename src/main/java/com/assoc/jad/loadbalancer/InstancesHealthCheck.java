package com.assoc.jad.loadbalancer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.assoc.jad.loadbalancer.tools.LoadBalancerStatic;

/**
 * determine connectivity for every application and every instance in the static hashMap of applications.
 * verify that the server is up and that the application is up.
 *  
 * @author jorge
 *
 */
public class InstancesHealthCheck implements Runnable {
	ExecutorService service = Executors.newCachedThreadPool();
	
	private static final Logger LOGGER = Logger.getLogger( InstancesHealthCheck.class.getName() );
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			//LoadBalancerStatic.applications.keySet().forEach(a -> instanceHealth(a));
			Object[] keys = LoadBalancerStatic.applications.keySet().toArray();
			for (int i=0;i<keys.length;i++) {
				instanceHealth(keys[i].toString());
			}
			try {
				Thread.sleep(60000, 0);
			} catch (InterruptedException e) {
				LOGGER.log(Level.INFO,"InstancesHealthCheck brought down by interrupt ");
				System.out.println("InstancesHealthCheck brought down by interrupt ");
				return;
			}
		}
	}
	private Object instanceHealth(String application) {
		//LoadBalancerStatic.applications.get(application).keySet().forEach(a -> healthCheck(a,application));
		Object[] keys = LoadBalancerStatic.applications.get(application).keySet().toArray();
		for (int i=0;i<keys.length;i++) {
			healthCheck(keys[i].toString(),application);
		}
		return null;
	}
	private void healthCheck(String instanceKey,String application) {
		
		String wrkString = instanceKey.replaceAll("http://", "");
		wrkString = wrkString.replaceAll("https://", "");
		int ndx = wrkString.indexOf(":");
		if (ndx == -1) ndx = wrkString.indexOf("/");
		
		if (ndx == -1) ndx = wrkString.length();
		String host = wrkString.substring(0, ndx);
		
		HashMap<String,HashMap<String,String>> instances = LoadBalancerStatic.applications.get(application);
		HashMap<String,String> instanceApp = instances.get(instanceKey);
		instanceApp.put(LoadBalancerStatic.SERVERUP, pingServer(host).toString());
		
		wrkString = instanceApp.get(LoadBalancerStatic.INSTANCEUP);
		if (wrkString != null && wrkString.equalsIgnoreCase("false")) return;

		instanceApp.put(LoadBalancerStatic.INSTANCEUP, pingInstance(instanceKey,application).toString());
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
