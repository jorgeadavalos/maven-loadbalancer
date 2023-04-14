package com.assoc.jad.loadbalancer.servlet;

import java.util.HashMap;

public interface IStorage {

	public void execute(NetworkRequest networkRequest, HashMap<String, String> requestParameters);

}
