package com.assoc.jad.loadbalancer.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UploadFiles implements IDocServlet {
	private HashMap<String,String> requestParameters = new HashMap<String, String>();
	private ServletInputStream sis;
    private final String crlf = "\r\n";

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		saveRequestParameters( request);
		getNetworkData(request);
		pushResponse(response);
	}
	private void saveRequestParameters(HttpServletRequest request)  {

		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getParameter(name);
			requestParameters.put(name, value);
		}
		requestParameters.put("servletoutput", request.getHeader("servletoutput"));
	}
	
	private void pushResponse(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin","*");
		try {
			Writer writer = response.getWriter();
			response.setStatus(200);
			writer.write("200");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* incoming record format:
	 * json+boundary+photo+boundary+json+boundary+photo+boundary
	 */
	private void getNetworkData(HttpServletRequest request) {
        String boundary = crlf +request.getHeader("boundary")+ crlf;
		byte[] bytes = new byte[4096];
		int len = -1;
		StringBuilder stringBuilder = new StringBuilder();
		NetworkRequest networkReq = null;
		byte[] readBytes = null;
		byte[] savedreadBytes = null;
		try {
			sis = request.getInputStream();
			while ( (len = sis.read(bytes)) != -1) {
				if (savedreadBytes != null) {
					readBytes = ArrayUtils.addAll(savedreadBytes, Arrays.copyOfRange(bytes, 0,len));
					savedreadBytes = null;
				} else {
					readBytes = Arrays.copyOfRange(bytes, 0,len);
				}
				stringBuilder.append(new String(Arrays.copyOfRange(bytes, 0,len)));
				int next = 0;
				int prev = 0;
				if (stringBuilder.indexOf(boundary) == -1) {
					savedreadBytes = readBytes.clone();
				}
				while (stringBuilder.substring(prev).indexOf(crlf) != -1 &&			// 'while' is for multiple boundaries in one read
					(next=stringBuilder.substring(prev).indexOf(boundary)) != -1) {
					networkReq = new NetworkRequest();
						String jsonString = stringBuilder.substring(prev, next).toString();
						networkReq.setJsonObject(((JSONObject) new JSONParser().parse(jsonString)));
						long filelen = (Long)networkReq.getJsonObject().get("filelen");
						networkReq.resetArrayList();
						if (filelen == 0) continue;

						networkReq.getArrayList().add(Arrays.copyOfRange(readBytes, next+boundary.length(),readBytes.length));
						filelen -= readBytes.length-(next+boundary.length());
						
						readFullFile(networkReq,filelen);
						uploadjar(networkReq);
						if (filelen > 0) {		// file had more data left and executed 'readFullFile' so; the array 'byte was not sufficient
							stringBuilder.setLength(0);	//next read request starts a new json+filedata;
							break;
						}
					prev = next+boundary.length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readFullFile(NetworkRequest networkReq,long filelen) {
	
		byte[] bytes = new byte[4096];
		int end = bytes.length;
		int len = 0;
		if (end >= filelen ) end = (int)filelen;
		
		try {
			while ( filelen > 0 && (len = sis.read(bytes,0,end)) != -1) {
				networkReq.getArrayList().add(Arrays.copyOfRange(bytes, 0,len));
				filelen -= len;
				if (end >= filelen ) end = (int)filelen;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadjar(NetworkRequest networkRequest) {
		requestParameters.put("methodName", "insertDocument");
		new Thread(new StorageDriver(networkRequest,requestParameters),"StorageDriver").start();
/*
		FileOutputStream fos = null;
		File tempFile = null;
		String filename = (String) networkRequest.getJsonObject().get("filename");
		try {
			tempFile = File.createTempFile(requestParameters.get("userid"), filename);
			fos = new FileOutputStream(tempFile);
			for (int i=0;i<networkRequest.getArrayList().size();i++) {
				fos.write(networkRequest.getArrayList().get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try { fos.close(); } catch (IOException e) {}
		}
*/
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
