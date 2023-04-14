package com.assoc.jad.loadbalancer.lbinstance.free;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.assoc.jad.loadbalancer.tools.LoadBalancerStatic;

public class CreateLoadBalancerObj {

	private static Method[] methods = ManagementFactory.getOperatingSystemMXBean().getClass().getDeclaredMethods();

	private LoadBalancerObj getLoadBalancerObj(HttpServletRequest req) {		
		HashMap<String,LoadBalancerObj> scriptHashMap = LifeCycleListener.hashMapLoadBalancerObj.get(req.getSession().getId());
		LoadBalancerObj lbObj = (scriptHashMap==null) ? null : scriptHashMap.get(req.getServletPath());
		if ( lbObj == null) lbObj = new LoadBalancerObj();
		return lbObj;
	}
	private Object systemData(String name,LoadBalancerObj lbObj) {
		Object value = new Object();
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : methods) {
			if (!method.getName().equals(name)) continue;
			method.setAccessible(true);
			try {
				value = method.invoke(operatingSystemMXBean);
				//lbObj.setProcessCpuLoadBgn((Double)value);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	private String scriptname(String parm) {
		int ndx1 = parm.lastIndexOf("/");
		if (ndx1 != -1) {
			parm = parm.substring(ndx1);
		}
		return parm;
	}
	
	public LoadBalancerObj bldLoadBalancerObj(HttpServletRequest req) {
		long beginTime = System.currentTimeMillis();
		LoadBalancerObj lbObj = getLoadBalancerObj( req);		
		lbObj.setProcessTime(beginTime);
		if ("/heartBeat.xhtml".equals(req.getServletPath())) return lbObj;
		
		lbObj.setViewId(req.getServletPath());
		lbObj.setInstancePort(req.getLocalPort());
		lbObj.setServerName(req.getServerName());
		lbObj.setExternalIP(LoadBalancerStatic.myExternalIP);
		lbObj.setSchema(req.getScheme());
		lbObj.setLoadBalancerWSURL(LifeCycleListener.loadBalancerWSURL);
		lbObj.setNetworkTimeBgn(System.currentTimeMillis());
		if (lbObj.getProcessTimeBgn() == 0.0) lbObj.setProcessTimeBgn(beginTime);
		//System data
		if (lbObj.getProcessCpuLoadBgn() == 0.0) lbObj.setProcessCpuLoadBgn((Double)systemData("getProcessCpuLoad",lbObj));

		HashMap<String,LoadBalancerObj> scriptHashMap = LifeCycleListener.hashMapLoadBalancerObj.get(req.getSession().getId());
		if (scriptHashMap==null) scriptHashMap = new HashMap<String,LoadBalancerObj>();
		scriptHashMap.put(scriptname(req.getServletPath()),lbObj);
		LifeCycleListener.hashMapLoadBalancerObj.put(req.getSession().getId(),scriptHashMap);
		
		return lbObj;
	}
	public LoadBalancerObj bldLoadBalancerObj(String sessionTag,LoadBalancerObj lbObj) {
		long beginTime = System.currentTimeMillis();
		lbObj.setProcessTime(beginTime);
			    
		lbObj.setExternalIP(LoadBalancerStatic.myExternalIP);
		lbObj.setLoadBalancerWSURL(LifeCycleListener.loadBalancerWSURL);
		lbObj.setNetworkTimeBgn(System.currentTimeMillis());
		if (lbObj.getProcessTimeBgn() == 0.0) lbObj.setProcessTimeBgn(beginTime);
		//System data
		if (lbObj.getProcessCpuLoadBgn() == 0.0) lbObj.setProcessCpuLoadBgn((Double)systemData("getProcessCpuLoad",lbObj));

		HashMap<String,LoadBalancerObj> scriptHashMap = LifeCycleListener.hashMapLoadBalancerObj.get(sessionTag);
		if (scriptHashMap==null) scriptHashMap = new HashMap<String,LoadBalancerObj>();
		//scriptHashMap.put(scriptname(req.getServletPath()),lbObj);
		LifeCycleListener.hashMapLoadBalancerObj.put(sessionTag,scriptHashMap);
		
		return lbObj;
	}
	public LoadBalancerObj setEndNetworkTime(HttpServletRequest req) {
		String script = "NONAME";
		//String networkEndTime = "";
		String networkBgnTime = "";
		String instancename = "";
		String[] keyValues = req.getQueryString().split("&");
		String[] names = {};
		if (keyValues == null || keyValues.length == 0) return null;
		
		for (int i=0;i<keyValues.length;i++) {
			int ndx = keyValues[i].indexOf('=');
			if (keyValues[i].startsWith("script")) script = keyValues[i].substring(++ndx);
			//if (keyValues[i].startsWith("networkEndTime")) networkEndTime = keyValues[i].substring(++ndx);
			if (keyValues[i].startsWith("networkBgnTime")) networkBgnTime = keyValues[i].substring(++ndx);
			if (keyValues[i].startsWith("instancename")) {
				
				String wrkstr = keyValues[i].substring(++ndx).trim();
				if (wrkstr.startsWith("/")) wrkstr = wrkstr.replaceFirst("/", "");
				names = wrkstr.split("/");
			}
		}
		instancename = names[0];
		if (names.length > 1) script = names[1];
		HashMap<String,LoadBalancerObj> wrkScriptHashMap = LifeCycleListener.hashMapLoadBalancerObj.get(req.getSession().getId());
		if (wrkScriptHashMap == null) return null; 
		LoadBalancerObj lbObj = wrkScriptHashMap.get(scriptname(script));
		if (lbObj == null) return null; 

		lbObj.setProcessCpuLoadEnd((Double)systemData("getProcessCpuLoad",lbObj));
		long networkTime = lbObj.getNetworkTimeBgn() + (System.currentTimeMillis()-lbObj.getNetworkTimeBgn())/2;
		lbObj.setInstance(instancename);
		lbObj.setNetworkTimeEnd(Long.valueOf(networkTime));
		if (networkBgnTime.length() > 0) lbObj.setNetworkTimeBgn(Long.valueOf(networkBgnTime));
		return lbObj;
	}
}
