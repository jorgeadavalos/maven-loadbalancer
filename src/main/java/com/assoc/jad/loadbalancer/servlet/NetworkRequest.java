package com.assoc.jad.loadbalancer.servlet;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class NetworkRequest {
	private JSONObject jsonObject;
	private ArrayList<byte[]> arrayList = new ArrayList<byte[]>();

/*
 * getters and setters
 */
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	public void addByteArray(byte[] byteArray) {
		this.arrayList.add(byteArray);
	}
	public ArrayList<byte[]> getArrayList() {
		return arrayList;
	}
	public void setArrayList(ArrayList<byte[]> arrayList) {
		this.arrayList = arrayList;
	}
	public void resetArrayList() {
		this.arrayList = new ArrayList<byte[]>();
	}
}
