package com.assoc.jad.loadbalancer.tools;

import java.util.HashMap;

public class Applications {
	public static HashMap<String,InServer> applications = 
			new HashMap<String,InServer>();

}
class InServer {
	public static HashMap<String,KeyValuePair> inServer = 
			new HashMap<String,KeyValuePair>();

}
class KeyValuePair {
	public static HashMap<String,String> keyValuePair = 
			new HashMap<String,String>();

}
