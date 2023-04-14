package com.assoc.jad.loadbalancer;

import java.util.HashMap;

public class MapsCollector {
	private HashMap<String,HashMap<String,String>> mapResult = new HashMap<String, HashMap<String,String>>();
	private HashMap<String,String> hashvalues = new HashMap<String,String>(10);
	private String previuosRoot = null;
	private HashMap<String,HashMap<String,HashMap<String,String>>> applications = 
				new HashMap<String,HashMap<String,HashMap<String,String>>>();

	public void accumulate(String[] others) {
		if (others == null || others.length != 2) return;
		if (others[0].equalsIgnoreCase("application")) {
			if (hashvalues.size() > 0) mapResult.put(previuosRoot, hashvalues);
			hashvalues = new HashMap<String,String>(10);
			mapResult = new HashMap<String, HashMap<String,String>>();
			applications.put(others[1], mapResult);
			previuosRoot = null;
			return;
		}
		String key = others[0];
		String value = others[1];
		hashvalues.put(key, value);
		
		String currentRoot = hashvalues.get(BalancerServlet.configRoot);
		if (currentRoot != null) {
			if (previuosRoot == null) previuosRoot = currentRoot;
			if (!previuosRoot.equals(currentRoot)) {
				hashvalues.put(BalancerServlet.configRoot, previuosRoot); //replace correct PC name
				mapResult.put(previuosRoot, hashvalues);
				
				hashvalues = new HashMap<String,String>(10);
				hashvalues.put(BalancerServlet.configRoot,currentRoot);
				previuosRoot = currentRoot;
			}
		}
	}
	public void combine(MapsCollector others) {
		if (others.hashvalues != null && !others.hashvalues.isEmpty()) others.mapResult.put(previuosRoot, hashvalues);
		if (this.hashvalues != null && !this.hashvalues.isEmpty()) this.mapResult.put(previuosRoot, hashvalues);
		others.hashvalues = null;
		this.hashvalues = null;
		for (String key : others.mapResult.keySet()) {
			this.mapResult.put(key, others.mapResult.get(key));
		}
	}
	public HashMap<String,HashMap<String,HashMap<String,String>>> getResult() {
		return this.applications;
	}	
}
