package com.assoc.jad.loadbalancer.tools;

import org.json.simple.JSONObject;

public class JsonToHashMap extends AllServicesHashMap {
	private String servicesKey;
	private String scriptKey;
	private String instanceKey;
	private JSONObject jsonObj;
	
	public JsonToHashMap(String servicesKey, String instanceKey, String scriptKey) {
		super(servicesKey, instanceKey, scriptKey);
		this.servicesKey = servicesKey;
		this.scriptKey = scriptKey;
		this.instanceKey = instanceKey;
	}

	public String getServicesKey() {
		return servicesKey;
	}
	public void setServicesKey(String servicesKey) {
		this.servicesKey = servicesKey;
	}
	public String getScriptKey() {
		return scriptKey;
	}
	public void setScriptKey(String scriptKey) {
		this.scriptKey = scriptKey;
	}
	public String getInstanceKey() {
		return instanceKey;
	}
	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
	}
	public JSONObject getJsonObj() {
		return jsonObj;
	}
	public void setJsonObj(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
}
