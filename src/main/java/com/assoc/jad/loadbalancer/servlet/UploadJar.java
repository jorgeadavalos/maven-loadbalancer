package com.assoc.jad.loadbalancer.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadJar implements IDocServlet {
	private HashMap<String,String> requestParameters = new HashMap<String, String>();

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		requestParameters.put("userid",request.getParameter("userid"));
		requestParameters.put("password",request.getParameter("password"));
		File tempFile = uploadjar(request,response);
		saveJarEntries(tempFile);
	}

	private File uploadjar(HttpServletRequest request, HttpServletResponse response) {
		byte[] bytes = new byte[4096];
		int len = -1;
		FileOutputStream fos = null;
		File tempFile = null;
		try {
			ServletInputStream sis = request.getInputStream();
			tempFile = File.createTempFile(requestParameters.get("userid"), ".jar");
			fos = new FileOutputStream(tempFile);
			while ( (len = sis.read(bytes)) != -1) {
				fos.write(bytes,0,len);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try { fos.close(); } catch (IOException e) {}
		}
		return tempFile;
	}
	private void saveJarEntries(File tempFile) {
		JarFile jar;
		try {
			jar = new JarFile(tempFile);
			Enumeration<JarEntry> enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
			    JarEntry file = enumEntries.nextElement();
			    java.io.File f = new java.io.File("backupDIr" + file.getName());
			    if (file.isDirectory()) { // if its a directory, create it
			        f.mkdir();
			        continue;
			    }
			    java.io.InputStream is = jar.getInputStream(file); // get the input stream
			    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			    while (is.available() > 0) {  // write contents of 'is' to 'fos'
			        fos.write(is.read());
			    }
			    fos.close();
			    is.close();
			}
			jar.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}
}
