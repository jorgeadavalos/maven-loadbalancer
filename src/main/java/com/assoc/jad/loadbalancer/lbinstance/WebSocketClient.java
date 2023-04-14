package com.assoc.jad.loadbalancer.lbinstance;

import java.lang.reflect.Method;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.json.simple.JSONObject;

@ClientEndpoint
public class WebSocketClient  implements Runnable {
    protected   WebSocketContainer container;
    protected   Session userSession = null;
    protected LoadBalancerObj obj;
    protected String sessionid;
    
    public WebSocketClient(LoadBalancerObj obj,String sessionid) {
    	this.obj = obj;
    	this.sessionid = sessionid;
    }
    public void test() {
        container = ContainerProvider.getWebSocketContainer();
        try {
        	String message = bldJsonFromObj();
            userSession = container.connectToServer(this, new URI(obj.getLoadBalancerWSURL()));
            userSession.getBasicRemote().sendText(message);
            userSession.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
    }
	private String methodToFieldName(String methodName) {
		StringBuilder fldName = new StringBuilder(methodName.replaceFirst("get", ""));
		byte cap = (byte) fldName.charAt(0);
		cap = (byte)(0x20 | cap);
		fldName.setCharAt(0, (char) cap);
		return fldName.toString();
	}
    @SuppressWarnings("unchecked")
	private String bldJsonFromObj() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sessionid",this.sessionid);
		Method[] methods = obj.getClass().getDeclaredMethods();
		Object[] arguments = null;
		
		for (int i=0;i<methods.length;i++) {
			if (!methods[i].getName().startsWith("get")) continue;
			
			try {
				Object retObj = methods[i].invoke(obj,arguments);
				if (retObj == null) retObj = "NOVALUE";
				jsonObj.put(methodToFieldName(methods[i].getName()),retObj.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return jsonObj.toJSONString();
    }
	@Override
	public void run() {
		test();
	}
}
