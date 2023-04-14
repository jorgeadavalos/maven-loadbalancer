package com.assoc.jad.loadbalancer.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.assoc.jad.loadbalancer.tools.Downloads;
import com.assoc.jad.loadbalancer.tools.LoadBalancerStatic;

@Named
@SessionScoped
public class HealthCheckBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String infomsg;
	private String selectedApp;
	private String applications;
	private List<SelectItem> applicationList  = new ArrayList<SelectItem>();
	private String resetBeanValues;

	private Object instanceKey;

	private void bldApplicationList() {
		if (applicationList.size() > 0) return;
		
		StringBuilder sb = new StringBuilder();
		Iterator<?> iter = LoadBalancerStatic.applications.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			SelectItem item = new SelectItem();
			item.setLabel(key);
			item.setValue(key);
			applicationList.add(item);
			sb.append(key).append(' ');
		}
		selectedApp = (String) applicationList.get(0).getValue();
		applications = sb.toString();
	}
	private String getHTMLParam(String name) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext external = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) external.getRequest();
		return request.getParameter(name);
	}

	public Set<String> getApplicationService() {
		if (LoadBalancerStatic.applications.get(selectedApp) == null) return null;
		
		return LoadBalancerStatic.applications.get(selectedApp).keySet();
	}
	public Collection<HashMap<String, String>> getService() {
		Collection<HashMap<String, String>> serviceParms = LoadBalancerStatic.applications.get(selectedApp).values();
		return serviceParms;
	}
	public HashMap<String,HashMap<String,String>> getService2() {
		HashMap<String,HashMap<String,String>> serviceParms = LoadBalancerStatic.applications.get(selectedApp);
		return serviceParms;
	}
	
	public HashMap<String, HashMap<String, HashMap<String, List<String>>>> getPerformanceService() {
		if (selectedApp == null) bldApplicationList();		
		return LoadBalancerStatic.allServicesData.get(selectedApp);
	}
	public HashMap<String,HashMap<String,List<String>>> getInstanceData() {
		if (instanceKey == null) getResetBeanValues();
		HashMap<String,HashMap<String,HashMap<String,List<String>>>> appInstances = LoadBalancerStatic.allServicesData.get(selectedApp);
		if (appInstances == null) return null;
		
		HashMap<String,HashMap<String,List<String>>> instance = appInstances.get(instanceKey);
		return instance;
	}	
	public String getInfomsg() {
		return infomsg;
	}
	public void setInfomsg(String infomsg) {
		this.infomsg = infomsg;
	}
	public String getSelectedApp() {
		//if (this.selectedApp == null) this.selectedApp = LoadBalancerStatic.applications.keySet().iterator().next();
		return selectedApp;
	}
	public void setSelectedApp(String selectedApp) {
		this.selectedApp = selectedApp;
	}
	public List<SelectItem> getApplicationList() {
		bldApplicationList();
		return applicationList;
	}
	public void setApplicationList(List<SelectItem> applicationList) {
		this.applicationList = applicationList;
	}
	public String getApplications() {
		bldApplicationList();
		return applications;
	}
	public void setApplications(String applications) {
		this.applications = applications;
	}
	public String getResetBeanValues() {
		this.selectedApp = this.getHTMLParam("application");
		this.instanceKey = this.getHTMLParam("instancekey");
		return this.resetBeanValues;
	}
	public void setResetBeanValues(String resetBeanValues) {
		this.resetBeanValues = resetBeanValues;
	}
	public void downloadJar() {
		HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		Downloads downloads = new Downloads();
		downloads.downloadJar(res);
	}
	public void initApplicationList(String parm) {
		applicationList  = new ArrayList<SelectItem>();
	}
}
