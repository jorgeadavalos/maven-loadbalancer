package com.assoc.jad.loadbalancer.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * determine connectivity for every application and every instance in the static hashMap of applications.
 * verify that the server is up and that the application is up.
 *  
 * @author jorge
 *
 */
public class ExecutorHealthCheck implements Runnable {
	ExecutorService service = Executors.newFixedThreadPool(10);
	
	private static final Logger LOGGER = Logger.getLogger( ExecutorHealthCheck.class.getName() );
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Object[] keys = LoadBalancerStatic.applications.keySet().toArray();
			for (int i=0;i<keys.length;i++) {
				instanceHealth(keys[i].toString());
			}
			try {
				Thread.sleep(60000, 0);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING,"ExecutorHealthCheck brought down by interrupt ");
				System.out.println("ExecutorHealthCheck brought down by interrupt ");
				e.printStackTrace();
				return;
			}
		}
	}
	private Object instanceHealth(String application) {
		Object[] keys = LoadBalancerStatic.applications.get(application).keySet().toArray();
		for (int i=0;i<keys.length;i++) {
			service.execute(new HealthTask(keys[i].toString(),application));
		}
		return null;
	}
}
