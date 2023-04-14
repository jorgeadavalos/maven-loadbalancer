package com.assoc.jad.loadbalancer.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class LoadBalancerStatic {
	
	public static final String SERVERUP			= "serverUp";
	public static final String INSTANCEUP		= "instanceUp";
	public static final String THREADGROUP		= "loadBalancer";
	public static final String LOADBALANCERURL	= "loadBalancerURL:";
	public static final String myExternalIP 	= setExternalIP();

	
	public static HashMap<String,HashMap<String,HashMap<String,String>>> applications = 
			new HashMap<String,HashMap<String,HashMap<String,String>>>();

	public static HashMap<String,HashMap<String,HashMap<String,HashMap<String,List<String>>>>> allServicesData = 
			new HashMap<String,HashMap<String,HashMap<String,HashMap<String,List<String>>>>>();

	public static HashMap<String,HashMap<String,HashMap<String,List<String>>>> sessionidData = 
			new HashMap<String,HashMap<String,HashMap<String,List<String>>>>();

	public static HashMap<String,HashMap<String,String>> barChartInfo = setBarChartInfo(); //TODO
	
	private static HashMap<String, HashMap<String, String>> setBarChartInfo() {
		
		HashMap<String, String> infos = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> chartNames = new HashMap<String, HashMap<String, String>>();
		
		infos.put("title", "Detail Chart for %s");
		infos.put("xLabel", "HTML Pages:%s times");
		infos.put("yLabel", "Max Time in Ms");
		chartNames.put("detail", infos);
		
		infos = new HashMap<String, String>();
		infos.put("title", "Bar Chart for %s");
		infos.put("xLabel", "HTML Pages:%s times");
		infos.put("yLabel", "Max Time in Ms");
		chartNames.put("root", infos);
		
		return chartNames;
	}
	private static String setExternalIP() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));

			String ip;
			ip = in.readLine();
			return ip;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
