package com.assoc.jad.loadbalancer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = "/download/*", loadOnStartup = 1, asyncSupported = true)
public class DownloadServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
		while (System.getProperties().keySet().iterator().hasNext()) {
			String key = (String) System.getProperties().keySet().iterator().next();
			if (System.getProperty(key).startsWith("webapp")) System.out.println(key);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	private void displayDoc(HttpServletResponse response, String document)
			throws IOException {

		int ndx = document.lastIndexOf('.');
		if (ndx == -1)
			throw new AbortProcessingException(
					"filename does not have an extension name=" + document);

		String type = document.substring(++ndx);

		File pdfFile = new File(document);

		response.setContentType("application/" + type);
		response.addHeader("Content-Disposition", "inline; filename="
				+ document + ";");
		response.setContentLength((int) pdfFile.length());

		FileInputStream fileInputStream = new FileInputStream(pdfFile);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}
		fileInputStream.close();
	}

	public void run() throws IOException {
	}

	private void performTask(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String document = request.getParameter("file");
		if (document != null) {
			displayDoc(response, document);
			return;
		}	}
}