package com.assoc.jad.loadbalancer.lbinstance;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.faces.event.PhaseEvent;
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
	public void beforePhase(PhaseEvent event) {
		long processTimeBgn = System.currentTimeMillis();
		HttpServletRequest req = (HttpServletRequest)event.getFacesContext().getExternalContext().getRequest();
		if ("/heartBeat.xhtml".equals(req.getServletPath())) return;
		
		LoadBalancerObj lbObj = bldLoadBalancerObj(req);
		lbObj.setProcessCpuLoadBgn((Double)systemData("getProcessCpuLoad",lbObj));
		lbObj.setProcessTimeBgn(processTimeBgn);
	}
	public void afterPhase(PhaseEvent event) {
		long networkTimeBgn = System.currentTimeMillis();
		HttpServletRequest req = (HttpServletRequest)event.getFacesContext().getExternalContext().getRequest();
		if ("/heartBeat.xhtml".equals(req.getServletPath())) return;
		
		LoadBalancerObj lbObj = bldLoadBalancerObj(req);
		lbObj.setProcessTimeEnd(networkTimeBgn);
		lbObj.setProcessCpuLoadEnd((Double)systemData("getProcessCpuLoad",lbObj));
		lbObj.setNetworkTimeBgn(networkTimeBgn);
	}
	public LoadBalancerObj setEndNetworkTime(HttpServletRequest req) {
		String script = "NONAME";
		//String networkEndTime = "";
		String networkBgnTime = "";
		String instancename = "";
		String[] keyValues = req.getQueryString().split("&");
		if (keyValues == null || keyValues.length == 0) return null;
		
		for (int i=0;i<keyValues.length;i++) {
			int ndx = keyValues[i].indexOf('=');
			if (keyValues[i].startsWith("script")) script = keyValues[i].substring(++ndx);
			//if (keyValues[i].startsWith("networkEndTime")) networkEndTime = keyValues[i].substring(++ndx);
			if (keyValues[i].startsWith("networkBgnTime")) networkBgnTime = keyValues[i].substring(++ndx);
			if (keyValues[i].startsWith("instancename")) instancename = keyValues[i].substring(++ndx).replaceAll("/", "");
		}
		HashMap<String,LoadBalancerObj> wrkScriptHashMap = LifeCycleListener.hashMapLoadBalancerObj.get(req.getSession().getId());
		if (wrkScriptHashMap == null) return null; 
		LoadBalancerObj lbObj = wrkScriptHashMap.get(scriptname(script));
		if (lbObj == null) return null; 

		long networkTime = lbObj.getNetworkTimeBgn() + (System.currentTimeMillis()-lbObj.getNetworkTimeBgn())/2;
		lbObj.setInstance(instancename);
		lbObj.setNetworkTimeEnd(Long.valueOf(networkTime));
		if (networkBgnTime.length() > 0) lbObj.setNetworkTimeBgn(Long.valueOf(networkBgnTime));
		return lbObj;
	}
}
