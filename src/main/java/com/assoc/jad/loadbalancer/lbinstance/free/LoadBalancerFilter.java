package com.assoc.jad.loadbalancer.lbinstance.free;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * 
 * this JSF filter allows http request to be modified (line added) to include a
 * script line. must be aware of 2 ways to output; via <b>getWriter</b> for html
 * text and <b>getOutputStream</b> for everything else.<br>
 * first execute <b>chain.doFilter(request, wrapper)</b>;<br>
 * next if ( wrapper.output.size() == 0) goto <b>wrapperIO</b> that executes
 * getOutputStream.<br>
 * otherwise add the <b>include</b> line
 *
 * @author jorge
 */
public class LoadBalancerFilter implements Filter {
	public static final String javascriptTag = System.lineSeparator() + "<script type=\"text/javascript\" src='%s'></script>" + System.lineSeparator();
	public static final String networkBgnTimeTag = "\t<input type=\"hidden\" value=\"%d\" id=\"networkBgnTime\" name=\"networkBgnTime\"/>\r\n";
	public static final String pagenameTag = "\t<input type=\"hidden\" value=\"%s\" id=\"script\" name=\"script\"/>\r\n";
	
	public static String javascript;
	private CreateLoadBalancerObj createLoadBalancerObj = new CreateLoadBalancerObj();
	
	public class CharResponseWrapper extends HttpServletResponseWrapper {
		private CharArrayWriter output;

		public String toString() {
			return output.toString();
		}

		public CharResponseWrapper(HttpServletResponse response) {
			super(response);
			output = new CharArrayWriter();
		}

		public PrintWriter getWriter() {
			return new PrintWriter(output);
		}

		public void setOutput(CharArrayWriter output) {
			this.output = output;
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		//javascript = String.format(javascriptTag, filterConfig.getInitParameter("script"));
		String wrkparm = System.getenv("LBHANDSHAKE");
		//int ndx1 = wrkparm.lastIndexOf("/");
		//ndx1++;
		//String src = wrkparm.substring(0, ndx1)+"lbcommands.js";
		javascript = String.format(javascriptTag, wrkparm);
		//javascript += String.format(javascriptTag,src);

	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		LoadBalancerFilterIO wrapperIO = new LoadBalancerFilterIO();
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (resp.isCommitted()) return;
		
		if (req.getServletPath().indexOf("/loadEndNetworkTime") != -1) {
			setEndNetworkTime(req);
			return;
		}
		LoadBalancerObj lbObj = createLoadBalancerObj.bldLoadBalancerObj(req);
		
		CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		
		long networkTimeBgn = System.currentTimeMillis();
		lbObj.setProcessTimeEnd(networkTimeBgn);
		lbObj.setNetworkTimeBgn(networkTimeBgn);
		
		int size = -1;
		if ((size = wrapperOK(wrapper)) > 0) {
			CharArrayWriter caw = new CharArrayWriter();
			caw.write(wrapper.toString().substring(0, size));
			if (wrapper.toString().indexOf("</head>") != -1) caw.write(javascript);
			caw.write(String.format(networkBgnTimeTag, lbObj.getProcessTime()));
			caw.write(String.format(pagenameTag, req.getRequestURI()));
			
			caw.write(wrapper.toString().substring(--size));
			response.setContentLength(caw.toString().length());
			response.getWriter().write(caw.toString());
		} else {
			if (wrapper.output.size() == 0)
				wrapperIO.doFilter(request, response, chain);
			else {
				response.getWriter().write(wrapper.toString());
			}
		}
	}
	private void setEndNetworkTime(HttpServletRequest req) {
		LoadBalancerObj lbObj = createLoadBalancerObj.setEndNetworkTime(req);
		if (lbObj == null) return;
		
		new Thread(new CollectSystemData(lbObj,req.getSession().getId()),"shipLBData").start();
	}

	private int wrapperOK(CharResponseWrapper wrapper) {
		if (wrapper.getContentType() == null) return -1;
		if (wrapper.getContentType().indexOf("text/html") == -1) return -1;
		
		int ndx = wrapper.toString().indexOf("</head>");
		if (ndx == -1) ndx = wrapper.toString().indexOf("</form>");
		if (ndx == -1) ndx = wrapper.toString().indexOf("</body>");
		if (ndx == -1) ndx = wrapper.toString().indexOf("</html>");
		return ndx;
	}
}