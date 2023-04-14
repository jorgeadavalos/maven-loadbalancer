package com.assoc.jad.loadbalancer.lbinstance.free;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CollectSystemData implements Runnable {
	
	private LoadBalancerObj lbObj;
	private String sessionid;
	private Method[] methods = ManagementFactory.getOperatingSystemMXBean().getClass().getDeclaredMethods();

	public CollectSystemData(LoadBalancerObj lbObj,String sessionid) {
		this.lbObj = lbObj;
    	this.sessionid = sessionid;
	}

	@Override
	public void run() {
		selectOSData();
		new Thread(new WebSocketClient(lbObj,sessionid),"Client").start();
	}

	private void populateLoadBalancerObj(Object value,String methodName) {
		Method[] methods = lbObj.getClass().getDeclaredMethods();
		Object[] arguments = new Object[] {value};
		
		methodName = methodName.replaceFirst("get", "set");
		for (int i=0;i<methods.length;i++) {
			if (!methods[i].getName().equals(methodName)) continue;
			
			try {
				methods[i].invoke(lbObj,arguments);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
    }
	private void selectOSData() {

		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : methods) {
			method.setAccessible(true);
			if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
					populateLoadBalancerObj(value,method.getName());
				} catch (Exception e) {
					value = e;
				}
				System.out.println(method.getName() + " = " + value);
			}
		}
	}
}
