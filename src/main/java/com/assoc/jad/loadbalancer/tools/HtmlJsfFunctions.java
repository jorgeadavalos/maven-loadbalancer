package com.assoc.jad.loadbalancer.tools;

import java.io.IOException;
import java.util.Enumeration;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HtmlJsfFunctions {

	public String getHTMLParam(String name) {
		
		ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) external.getRequest();
		String parm = request.getParameter(name);
		if (parm != null) return parm;
		
		String formname = request.getParameter("formname");
		if (formname != null) {
			return request.getParameter(formname+":"+name);
		}
		
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String fullName = keys.nextElement();
			if (fullName.indexOf(name) == -1) continue;
			return request.getParameter(fullName);
		}
		return null;
	}
//    public synchronized void forward(String parm) {
//		
//		ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
//		HttpServletResponse resp = (HttpServletResponse) external.getResponse();
//		HttpServletRequest req = (HttpServletRequest) external.getRequest();
//		if ( external.isResponseCommitted()) return;
//		
//		String currentPage = ((HttpServletRequest) external.getRequest()).getRequestURI();
//		if (parm == null || parm.length() == 0) parm = currentPage;
//		if (parm.length() > 0 && currentPage.indexOf(parm) != -1) {
//			return;
//		}
//		
//		try {
//			RequestDispatcher disp = req.getRequestDispatcher(parm);
//			disp.forward(req, resp);
//		} catch (IOException | ServletException e) {
//			e.printStackTrace();
//			return;
//		}
//	}
    public void redirect(String parm) {
		
		ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletResponse resp = (HttpServletResponse) external.getResponse();
		if ( external.isResponseCommitted()) return;
		
		String currentPage = ((HttpServletRequest) external.getRequest()).getRequestURI();
		if (parm == null || parm.length() == 0) parm = currentPage;
		if (parm.length() > 0 && currentPage.indexOf(parm) != -1) {
			return;
		}
		
		try {
			resp.sendRedirect(parm);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
    public void forward(String parm) {
		
		ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletResponse resp = (HttpServletResponse) external.getResponse();
		HttpServletRequest req = (HttpServletRequest) external.getRequest();
	    RequestDispatcher dispatcher = req.getRequestDispatcher(parm);
	    try {
			dispatcher.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
			return;
		}
	}
	public String getSessionid() {
		return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getId();
	}
}
