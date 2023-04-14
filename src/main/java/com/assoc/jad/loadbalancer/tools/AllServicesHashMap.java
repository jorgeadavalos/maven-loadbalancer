package com.assoc.jad.loadbalancer.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;

public abstract class AllServicesHashMap {

	private HashMap<String,HashMap<String,HashMap<String,List<String>>>> servicesHashMap = null;
	private HashMap<String,HashMap<String,List<String>>> serviceHashMap = null;
	private HashMap<String,List<String>> scriptHashMap = null;
	
	private String servicesKey;
	private String serviceKey;
	private String scriptKey;

	public AllServicesHashMap(String servicesKey, String serviceKey, String scriptKey) {
		this.serviceKey = serviceKey;
		this.servicesKey = servicesKey;
		this.scriptKey  = scriptKey;
		registerWebApp();
	}
	private void registerWebApp() {
		
		HashMap<String,HashMap<String,String>> applicationName = LoadBalancerStatic.applications.get(servicesKey);
		HashMap<String,String> applicationInstance = null;
		if (applicationName == null) {
			applicationName = new HashMap<String,HashMap<String,String>>();
			applicationInstance = new HashMap<String,String>();
			//applicationName.put(serviceKey, applicationInstance);
			
			//LoadBalancerStatic.applications.put(servicesKey, applicationName);
		} else {
			applicationInstance = applicationName.get(serviceKey);
			if (applicationInstance != null) return;

			applicationInstance = new HashMap<String,String>();
			//applicationName.put(serviceKey, applicationInstance);
		}
		
		applicationInstance = new HashMap<String,String>();
		applicationInstance.put("ctr", "0");
		applicationInstance.put("instance",serviceKey);
		applicationInstance.put("instanceUp", "true");
		applicationInstance.put("serverUp", "true");
		applicationName.put(serviceKey, applicationInstance);
		LoadBalancerStatic.applications.put(servicesKey, applicationName);
	}
	private void getServicesHashMap() {
		servicesHashMap = LoadBalancerStatic.allServicesData.get(servicesKey);
		if (servicesHashMap == null) {
			servicesHashMap = new HashMap<String,HashMap<String,HashMap<String,List<String>>>>();
			LoadBalancerStatic.allServicesData.put(servicesKey, servicesHashMap);
		}
	}
	private void getServiceHashMap() {
		if (servicesHashMap == null) this.getServicesHashMap();
		serviceHashMap = servicesHashMap.get(serviceKey);
		if (serviceHashMap == null) {
			serviceHashMap = new HashMap<String,HashMap<String,List<String>>>();
			servicesHashMap.put(serviceKey,serviceHashMap);
		}
	}
	private void getScriptHashMap() {
		if (serviceHashMap == null) this.getServiceHashMap();
		scriptHashMap = serviceHashMap.get(scriptKey);
		if (scriptHashMap == null) {
			scriptHashMap = new HashMap<String,List<String>>();
			serviceHashMap.put(scriptKey,scriptHashMap);
		}
	}
	private void bldSessionIdHashMap(JSONObject jsonItems) {
		String jsonSessionid = (String)jsonItems.get("sessionid");
		String jsonViewId = (String)jsonItems.get("viewId");
		HashMap<String, HashMap<String, List<String>>> sessionidHashMap = LoadBalancerStatic.sessionidData.get(jsonSessionid);
		if (sessionidHashMap == null) {
			sessionidHashMap = new HashMap<String, HashMap<String, List<String>>>();
			LoadBalancerStatic.sessionidData.put(jsonSessionid, sessionidHashMap);
		}
		if (sessionidHashMap.get(jsonViewId) == null) {
			HashMap<String, List<String>> keyList = new HashMap<String, List<String>>();
			sessionidHashMap.put(jsonViewId,keyList);
		}

		transformJsonToList( jsonItems, sessionidHashMap.get(jsonViewId));
	}
	public void transformJsonToList( JSONObject  jsonItems,HashMap<String, List<String>> keyList) {
		List<String> itemList = null;
		if (keyList == null) {
			keyList = new HashMap<String, List<String>>();
		}
		Iterator<?> iter = jsonItems.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			itemList = keyList.get(key);
			if (itemList == null) itemList = new ArrayList<String>();
			itemList.add((String)jsonItems.get(key));
			keyList.put(key, itemList);
		}
	}
	public void bldListFromJson( JSONObject  jsonItems) {
		getScriptHashMap();
		transformJsonToList( jsonItems, scriptHashMap);
		bldSessionIdHashMap(jsonItems);
	}
	public String getServicesKey() {
		return servicesKey;
	}
	public void setServicesKey(String servicesKey) {
		this.servicesKey = servicesKey;
	}
	public String getServiceKey() {
		return serviceKey;
	}
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
	public String getScriptKey() {
		return scriptKey;
	}
	public void setScriptKey(String scriptKey) {
		this.scriptKey = scriptKey;
	}
}
