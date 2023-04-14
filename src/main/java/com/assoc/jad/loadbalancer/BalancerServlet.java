package com.assoc.jad.loadbalancer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assoc.jad.loadbalancer.tools.ExecutorHealthCheck;
import com.assoc.jad.loadbalancer.tools.LoadBalancerStatic;

public class BalancerServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;

	public static String configRoot;
	private static String host = "localhost";
	private static int port = 80;
	private static int counter = 0;
	private static String balancer;
	private static String subdir;
	
	private enum loadBalancerVars {
		loadBalancerURL(String.format("\tloadBalancerURL: \"http://%s:%d%s%s\",", host,port,balancer,subdir)),  //javascript var spacename syntax.
		loadBalancerCounter(String.format("windowNamw = %s;", counter)), 
		;
		String cmd = null;
		loadBalancerVars(String cmd) {
			this.cmd = cmd;
		}
		public String getCmd() { return this.cmd;}
	}


	private void bldConfig(String filename) {
		BufferedReader inpf = null;
		
		try {
			inpf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
			LoadBalancerStatic.applications = inpf.lines().parallel()
				.filter(str -> (!str.startsWith("#") && str.length() > 0)) 
			    .map(a -> a.trim().split("="))
			    .collect(MapsCollector::new,
			    		MapsCollector::accumulate,
			    		MapsCollector::combine
			    		)
			    .getResult();
		}finally {
			try {if (inpf != null) inpf.close();} catch (IOException e) {}
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	public void init() throws ServletException {
		ServletConfig config = getServletConfig();
		String configFile = config.getInitParameter("configFile");
		configRoot = config.getInitParameter("root");
		bldConfig(configFile);
		ThreadGroup loadBalancerThreads = new ThreadGroup(LoadBalancerStatic.THREADGROUP);
		new Thread(loadBalancerThreads,new ExecutorHealthCheck(),"BalancerServlet").start();

	}
	@SuppressWarnings("unused")
	public void destroy() {
		Thread[] threads = new Thread[100];
		Thread.enumerate(threads);
		if (threads == null) return;
		for (Thread thread : threads) {
			if (thread.getName().equals("BalancerServlet")) {
				thread.interrupt();
				return;
			}
		}
	}
	public void run() throws IOException {
	}
	/**
	 * networkbgnTime is in the same entry in list as networkEndTime.
	 * @param request
	 */
	public void updateInstanceData(HttpServletRequest request) {
    	String application = request.getParameter("application");
    	if  (application == null) return;
    	
    	String key = request.getParameter("key");
    	String networkBgnTime = request.getParameter("networkBgnTime");
    	String networkEndTime = request.getParameter("networkEndTime");
		HashMap<String,HashMap<String,HashMap<String,List<String>>>> instances = LoadBalancerStatic.allServicesData.get(application);
		HashMap<String,HashMap<String,List<String>>> instance = instances.get(key);
		HashMap<String,List<String>> scriptParms = instance.get(request.getParameter("script"));
		if (scriptParms == null) return;
		List<String> networkBgnTimes = scriptParms.get("networkBgnTime");
		List<String> networkEndTimes = scriptParms.get("networkEndTime");
		for (int i=0;i<networkBgnTimes.size();i++) {
			if (!networkBgnTime.equals(networkBgnTimes.get(i))) continue;
			networkEndTimes.set(i, networkEndTime);
			break;
		}
		
	}
	public void updateapplication(HttpServletRequest request) {
    	String application = request.getParameter("application");
    	if (application == null) return;
    	
    	String key = request.getParameter("key");
		HashMap<String,HashMap<String,String>> appServices = LoadBalancerStatic.applications.get(application);
		HashMap<String,String> parms = appServices.get(key);
		Integer CTR = Integer.valueOf(parms.get("ctr"));
		CTR--;
		parms.put("ctr", CTR.toString());
	}
	private void requestFromService(HttpServletRequest request,HttpServletResponse response, String appCmd ) {
		
		switch (appCmd.toLowerCase()) {
        case "taskdone":
        	updateapplication(request);
        	updateInstanceData(request);
            break;
        default:
        	processFile(request,response);
        	break;
		}
	}
	private void processFile(HttpServletRequest request, HttpServletResponse response) {

		BufferedReader inpf = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("catalina.base")).append(File.separator).append("webapps"); 
		String contextPath = request.getContextPath().substring(1);
		String[] URLElements = request.getRequestURL().toString().split("/");
		for (int i=0;i<URLElements.length;i++) {
			if (!contextPath.equals(URLElements[i])) continue;
			subdir = "/"+URLElements[i+1];
			for (;i<URLElements.length;i++)
				sb.append("/").append(URLElements[i]);
		}
		String type = "text/html";
		if (URLElements[URLElements.length-1].endsWith("js")) type = "text/javascript";
		
		response.setContentType(type); 
		String filePath = sb.toString();
		if (filePath.indexOf("loadbalancer") != -1 && filePath.indexOf("lbcommands") != -1) {
			setJavscriptVars(request);
			editRequest(filePath,response);
			return;
		}
		try {

	        PrintWriter out = response.getWriter();  
			inpf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			inpf.lines().parallel()
			    .forEach(a-> out.write(a+System.lineSeparator()));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {if (inpf != null) inpf.close();} catch (IOException e) {}
		}
	}

	private void editRequest(String filePath,HttpServletResponse response ) {
		BufferedReader inpf = null;

		try {

	        PrintWriter out = response.getWriter();  
			inpf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
			inpf.lines().parallel()
				.map(a -> {
					String[] array = a.trim().split("\\s+");
					if (array[0].equals(LoadBalancerStatic.LOADBALANCERURL)) {
						for (loadBalancerVars parm : loadBalancerVars.values()) {
							if (array[0].indexOf(parm.name()) != -1) {
								return parm.getCmd();
							}
						}
					}
					return a;
				})
			    .forEach(a-> out.write(a+System.lineSeparator()));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {if (inpf != null) inpf.close();} catch (IOException e) {}
		}
	}
	private void setJavscriptVars(HttpServletRequest request) {
		host = request.getServerName();
		port = request.getServerPort();
		balancer = request.getContextPath();
		
	}

	private void performTask(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String application=request.getPathInfo().substring(1);
		HashMap<String,HashMap<String,String>> appServices = LoadBalancerStatic.applications.get(application);
		if (appServices == null) {
			requestFromService(request,response,application);	//TODO process data from service
			return;
		}
		setJavscriptVars(request);		
		String service = selectAppService(appServices);
//		String url = service+"/"+application+"?loadBalancerWSURL="+String.format("ws://%s:%d%s/websocketserver", host,port,balancer); 
		String url = service+"/"+application; 
		if (service == null) {	//no active service found for a given application
			response.sendRedirect("../test.xhtml");
		} else
			response.sendRedirect(url);
	}
	private String selectAppService(HashMap<String, HashMap<String, String>> appServices) {
		String savedURL = null;
		Boolean instanceAlive = false;
		for (String key1 : appServices.keySet()) {
			HashMap<String,String> instance = appServices.get(key1);
			if (!"true".equalsIgnoreCase(instance.get(LoadBalancerStatic.INSTANCEUP))) continue;
			instanceAlive = true;
			Integer CTR1 = Integer.valueOf(instance.get("ctr"));
			if (savedURL == null) savedURL = key1;
			HashMap<String,String> savedInstance = appServices.get(savedURL);
			Integer CTR2 = Integer.valueOf(savedInstance.get("ctr"));
			if (CTR2>CTR1) savedURL = key1;
		}
		if (!instanceAlive) return null;
		
		HashMap<String,String> parms = appServices.get(savedURL);
		Integer CTR = Integer.valueOf(parms.get("ctr"));
		CTR++;
		parms.put("ctr", CTR.toString());

		//String script = appServices.get(savedURL).get("welcome");
		//if (script != null ) savedURL += script;
		return savedURL;		
	}
}