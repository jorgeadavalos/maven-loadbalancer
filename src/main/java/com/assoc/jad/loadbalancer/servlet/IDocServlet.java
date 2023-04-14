package com.assoc.jad.loadbalancer.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IDocServlet {
	public void execute(HttpServletRequest request,HttpServletResponse response);
	public String getMessage();

}
