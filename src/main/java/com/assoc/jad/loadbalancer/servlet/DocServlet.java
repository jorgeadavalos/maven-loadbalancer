package com.assoc.jad.loadbalancer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1L;
	private final String defaultClass = "DownloadFile";

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	private void performTask(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		
		if (ServletStatic.docServletInterface == null) ServletStatic.docServletInterface = ServletStatic.docServletInterfaceInit();
		
		String classname = request.getHeader("classname");
		if (classname == null) classname = request.getParameter("classname");
		if (classname == null) classname = defaultClass;
		IDocServlet iDocServlet = ServletStatic.docServletInterface.get(classname.toLowerCase());
		iDocServlet.execute(request, response);
		
	}
}