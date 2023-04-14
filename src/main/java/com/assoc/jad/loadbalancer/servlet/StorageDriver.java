package com.assoc.jad.loadbalancer.servlet;

import java.util.HashMap;

public class StorageDriver implements Runnable {

	private NetworkRequest networkRequest;
	private HashMap<String, String> requestParameters;

	public StorageDriver(NetworkRequest networkRequest, HashMap<String, String> requestParameters) {
		this.networkRequest = networkRequest;
		this.requestParameters = requestParameters;
	}

	@Override
	public void run() {
		String classname = (String)networkRequest.getJsonObject().get("storage");
		ServletStatic.storageInterface.get(classname).execute(networkRequest,requestParameters);
		
	}
}
