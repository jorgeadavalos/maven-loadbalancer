package com.assoc.jad.loadbalancer.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.assoc.jad.loadbalancer.lbinstance.LoadBalancerObj;
import com.assoc.jad.loadbalancer.tools.GraphModel;
import com.assoc.jad.loadbalancer.tools.HtmlJsfFunctions;

@RequestScoped
public class BarGraphBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private BarChartModel barModelRoot = null;
	private BarChartModel barModelDetail;
	private HashMap<String, HashMap<String, List<String>>> instance;
	private String selectedField;
	private List<SelectItem> fieldList;
	private GraphModel graphModel;

	private String urlUpToHostAndPort(String htmlParam) {
		int ndx2 = -1;
		int ndx1 = htmlParam.indexOf("//");
		if (ndx1 != -1) {
			ndx1 += 2;
			ndx2 = htmlParam.indexOf('/', ndx1);
			if (ndx2 == -1) return htmlParam;
		}
		return htmlParam.substring(0, ndx2);
	}
	private void bldFieldList() {
		fieldList  = new ArrayList<SelectItem>();
		Field[] fields = LoadBalancerObj.class.getDeclaredFields();

		for (int i=0;i<fields.length;i++) {
			SelectItem item = new SelectItem();
			item.setLabel(fields[i].getName());
			item.setValue(fields[i].getName());
			fieldList.add(item);
		}
	}
	private void createBarModelRoot() {
		HtmlJsfFunctions htmlJsfFunctions = new HtmlJsfFunctions();
		String selectedApp = htmlJsfFunctions.getHTMLParam("application");
		String instanceKey = urlUpToHostAndPort(htmlJsfFunctions.getHTMLParam("instanceurl"));

		graphModel = new GraphModel(selectedApp,instanceKey);
		graphModel.setTitle("Bar Chart for "+selectedApp+" "+instanceKey);
		graphModel.setXLabel("HTML Pages:Total(network+CPu) times");
		graphModel.setYLabel("Max Time in Ms");

		barModelRoot = graphModel.getBarModelRoot();
		instance = graphModel.getInstance();
	}
	private void createBarModelDetail(String htmlPage) {
		graphModel.setTitle("Detail Chart for "+htmlPage);
		graphModel.setXLabel("HTML Pages:Total(network+CPu) times");
		graphModel.setYLabel("Max Time in Ms");
		barModelDetail = graphModel.getBarModelDetail(instance.get(htmlPage));

	}
	private void createBarModelSessionId(String sessionid) {
		graphModel.setTitle("Detail Chart for sessionid="+sessionid);
		graphModel.setXLabel("HTML Pages:Total(network+CPu) times");
		graphModel.setYLabel("Max Time in Ms");
		barModelDetail = graphModel.getBarModelSessionId(sessionid);

	}

	public void itemSelectFromRoot(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
		ChartSeries chartSeries = barModelRoot.getSeries().get(event.getSeriesIndex());
		createBarModelDetail(chartSeries.getLabel());
    }
	public void itemSelectFromDetail(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
		ChartSeries chartSeries = barModelDetail.getSeries().get(event.getSeriesIndex());
		createBarModelSessionId(chartSeries.getLabel());
    }

	/*
	 * getters and setters
	 */
	public void setBarModelRoot(BarChartModel barModelRoot) {
		this.barModelRoot = barModelRoot;
	}

	public BarChartModel getBarModelRoot() {
		if (instance != null || barModelRoot != null) return barModelRoot;
		createBarModelRoot();
		return barModelRoot;
	}
	public BarChartModel getBarModelDetail() {
		if (barModelDetail == null) return new BarChartModel();
		return barModelDetail;
	}
	public List<SelectItem> getFieldList() {
		if (fieldList == null || fieldList.size() == 0) bldFieldList();
		return fieldList;
	}
	public void setFieldList(List<SelectItem> fieldList) {
		this.fieldList = fieldList;
	}
	public String getSelectedField() {
		return selectedField;
	}
	public void setSelectedField(String selectedField) {
		this.selectedField = selectedField;
	}
	public String resetValues() {
		barModelRoot = null;
		barModelDetail = null;
		instance = null;
		return "";
	}
}
