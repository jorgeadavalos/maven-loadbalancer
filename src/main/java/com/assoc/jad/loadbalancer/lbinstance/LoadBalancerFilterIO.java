package com.assoc.jad.loadbalancer.lbinstance;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class LoadBalancerFilterIO {

    public class CharResponseWrapper extends HttpServletResponseWrapper {
        private ByteArrayOutputStream output;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new ByteArrayOutputStream();
        }

        public byte[] getByteArray() {
            return output.toByteArray();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
		HttpServletResponse resp = (HttpServletResponse) response;
		if (resp.isCommitted()) return;
		
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse)response);
        chain.doFilter(request, wrapper);
        response.getOutputStream().write(wrapper.getByteArray());
    }
}