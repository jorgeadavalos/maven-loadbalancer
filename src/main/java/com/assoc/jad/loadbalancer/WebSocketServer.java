package com.assoc.jad.loadbalancer;

import java.util.HashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.assoc.jad.loadbalancer.lbinstance.free.WebSocketClient;
import com.assoc.jad.loadbalancer.tools.JsonToHashMap;
import com.assoc.jad.loadbalancer.tools.LoadBalancerStatic;

@ServerEndpoint("/websocketserver")
public class WebSocketServer {
	private static final Log LOG = LogFactory.getLog(WebSocketServer.class);

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Open Connection ...");
	}

	@OnClose
	public void onClose() {
		System.out.println("Close Connection ...");
	}

	@OnMessage
	public void onMessage(String jsonMsg) {
		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) new JSONParser().parse(jsonMsg);
			String servicesKey		= (String)jsonObj.get("instance");		//key1
			String viewId		= (String)jsonObj.get("viewId");		//key3
			StringBuilder url = new StringBuilder((String)jsonObj.get("schema"));
			url.append("://").append((String)jsonObj.get("serverName"));
			String port = (String)jsonObj.get("instancePort");
			if (port != null && port.length() > 0) url.append(':').append((String)jsonObj.get("instancePort"));

			HashMap<String, HashMap<String, String>> application = LoadBalancerStatic.applications.get(servicesKey);
			if (application != null ) {
				HashMap<String,String> instanceApp = application.get(url.toString());
				if (instanceApp == null) {
					LOG.warn("there is not registered instance for application="+servicesKey+" url="+url.toString());
					return;
				}
				instanceApp.put(LoadBalancerStatic.SERVERUP, "true");
			}
			
			JsonToHashMap jsonToHashMap = new JsonToHashMap(servicesKey, url.toString(), viewId);
			jsonToHashMap.bldListFromJson(jsonObj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Message from the client: " + jsonMsg);
	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}

}
