package com.assoc.jad.loadbalancer.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadFile extends ADocServlet {
	private String message;

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {

		InputStream is = bldInputStream(request);
		if (is == null) return;
		
		response.setContentType("application/" + downloadType);
		response.addHeader("Content-Disposition", "inline; filename=" + filename + ";");
		response.setContentLength((int) fileLen);

		try {
			OutputStream responseOutputStream = response.getOutputStream();
			int bytes;
			while ((bytes = is.read()) != -1) {
				responseOutputStream.write(bytes);
			}
		} catch (Exception e) {
			message = "file \""+filename+"\" failed to the download";
			e.printStackTrace();
			return;
		} finally {
			if (is != null) {
				try {is.close();} catch (IOException e) {}
			}
		}
	}
/*
 * getters and setters
 */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
