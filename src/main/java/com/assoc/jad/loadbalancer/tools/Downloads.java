package com.assoc.jad.loadbalancer.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletResponse;

public class Downloads {
	
	public void downloadJar(HttpServletResponse response) {
		File file = findLBinstanceJar();
		try {
			downloadBin( response,  file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private File findLBinstanceJar() {
		String resource = "lbinstance";
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		URL dirURL = contextClassLoader.getResource(resource);
		File dir;
		try {
			dir = new File(dirURL.toURI());
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.getName().startsWith(resource) && file.getName().endsWith(".jar")) {
					return file;
				}
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void downloadBin(HttpServletResponse response, File file) throws IOException {

		String filename = file.getName();
		int ndx = filename.lastIndexOf('.');
		if (ndx == -1) throw new AbortProcessingException("filename does not have an extension name=" + filename);

		String type = filename.substring(++ndx);

		response.setContentType("application/" + type);
		response.addHeader("Content-Disposition", "inline; filename=" + filename + ";");
		response.setContentLength((int) file.length());

		FileInputStream fileInputStream = new FileInputStream(file);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}
		fileInputStream.close();
	}
}
