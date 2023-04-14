package com.assoc.jad.loadbalancer.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public abstract class ADocServlet implements IDocServlet {
	protected long fileLen = 0;
	protected String downloadType = "jar";
	protected String filename = "dummy";

	protected InputStream bldInputStream(HttpServletRequest request) {
		InputStream is = null;
		
		String reqType = request.getParameter("resource");
		switch (reqType) {
			case "resource":
				is = findResourceFile(request);
				break;
			default:
				break;
		}
		return is;
	}
	protected InputStream findResourceFile(HttpServletRequest request) {
		String resource = "lbinstance";
		String startWith = request.getParameter("startWith");
		String endWith  = request.getParameter("endWith");
		
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		URL dirURL = contextClassLoader.getResource(resource);
		File dir;
		try {
			dir = new File(dirURL.toURI());
			File[] files = dir.listFiles();
			for (File file : files) {
				if (startWith == null || !file.getName().startsWith(startWith)) continue;
				if (endWith == null || !file.getName().endsWith(endWith)) continue;
				
				filename = file.getName();
				fileLen = file.length();
				return new FileInputStream(file);
				
			} 
		} catch (URISyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
