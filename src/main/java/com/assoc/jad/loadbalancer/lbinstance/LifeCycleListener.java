package com.assoc.jad.loadbalancer.lbinstance;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * add the following phase listener to the instance that needs to be load balancer needs to be aware of.<br>
 * This should be added to file <b>faces-config.xml</b><br>
 *	<b>&#60;lifecycle&#62;<br>
 *  	&#60;phase-listener>com.assoc.jad.shoplist.shoplist.lbinstance.LifeCycleListener&#60;/phase-listener><br>
 *	&#60;/lifecycle><br></b>
 * @author jorge
 *
 */
public class LifeCycleListener implements PhaseListener {

	private static final long serialVersionUID = 1L;
	public static HashMap<String,HashMap<String,LoadBalancerObj>> hashMapLoadBalancerObj = new HashMap<String, HashMap<String,LoadBalancerObj>>();
	public static String loadBalancerWSURL = initWS();
	
	private CreateLoadBalancerObj createLoadBalancerObj = new CreateLoadBalancerObj();

	private static String initWS() {
		String wrkURL = "invalid url "+System.getenv("LBHANDSHAKE");
		try {
			URL url = new URL(System.getenv("LBHANDSHAKE")); 
			wrkURL = "ws://"+url.getAuthority()+"/loadbalancer/websocketserver";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return wrkURL;
	}
	@Override
	public void afterPhase(PhaseEvent event) {
		createLoadBalancerObj.afterPhase(event);
	}
	@Override
	public void beforePhase(PhaseEvent event) {
		createLoadBalancerObj.beforePhase(event);
	}
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
