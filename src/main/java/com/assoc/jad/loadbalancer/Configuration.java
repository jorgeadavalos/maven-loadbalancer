package com.assoc.jad.loadbalancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;

public class Configuration implements javax.servlet.ServletContextListener {

	private void readConfigFile(String filename) {
		BufferedReader inpf = null;
		
		try {
			inpf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
			inpf.lines().parallel()
				.filter(str -> (!str.startsWith("#") && str.length() > 0 && str.trim().startsWith("-D"))) 
			    .map(b -> b.trim().split("="))
			    .forEach(b->System.setProperty(b[0].trim(), b[1].trim()))
			    ;
		}finally {
			try {if (inpf != null) inpf.close();} catch (IOException e) {}
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		readConfigFile("loadbalancer.properties");
	}
}
