package com.assoc.jad.loadbalancer.servlet;

import java.util.HashMap;

public class ServletStatic {
	public static final String SERVLETTABLENAME = "documents";
	
	public static HashMap<String,IDocServlet> docServletInterface = docServletInterfaceInit();
	public static HashMap<String,IDocServlet> docServletInterfaceInit() {
		
		HashMap<String, IDocServlet> interfaceClasses = new HashMap<String, IDocServlet>(10);
		interfaceClasses.put("uploadjar", new UploadJar());
		interfaceClasses.put("uploadfile", new UploadFiles());
		interfaceClasses.put("downloadfile", new DownloadFile());
		
		return interfaceClasses;
	}
	public static HashMap<String,IStorage> storageInterface = storageInterfaceInit();
	
	public static HashMap<String,IStorage> storageInterfaceInit() {
		
		HashMap<String, IStorage> interfaceClasses = new HashMap<String, IStorage>(10);
		interfaceClasses.put("filesystem", null);
		
		return interfaceClasses;
	}
}