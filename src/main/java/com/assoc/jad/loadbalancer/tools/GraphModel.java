package com.assoc.jad.loadbalancer.tools;

import java.util.HashMap;
import java.util.List;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

public class GraphModel {
	private BarChartModel barChartModel = new BarChartModel();
	private Long maxTime;
	private HashMap<String, HashMap<String, List<String>>> instance;
	private String selectedApp;
	private String instanceKey;
	private String title;
	private String xLabel;
	private String yLabel;
	
	public GraphModel(String selectedApp,String instanceKey) {
		this.selectedApp = selectedApp;
		this.instanceKey = instanceKey;
	}

	private void barChartLabels() {
		barChartModel.setTitle(title);
		barChartModel.setLegendPosition("ne");
		Axis xAxis = barChartModel.getAxis(AxisType.X);
		xAxis.setLabel(xLabel);
		Axis yAxis = barChartModel.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setLabel(yLabel);
		double tenPercent = (maxTime/100.0)*20;
		yAxis.setMax(maxTime+tenPercent);
	}
	private void createbarModelRoot() {
		maxTime = 0l;
		barChartModel = new BarChartModel();

		for (String htmlPage : instance.keySet()) {
			HashMap<String, List<String>> list = instance.get(htmlPage);
			barChartModel.addSeries(calculatePageTotalTime(htmlPage,list));
		}
		barChartLabels();
	}
	private void createBarModelDetail(HashMap<String, List<String>> list) {
		maxTime = 0l;
		barChartModel = new BarChartModel();
		initBarModelDetail(list);
		barChartLabels();
	}
	private Long singleItemtime(String s_bgn,String s_end) {
		Long lapsedTime = 0l;
		try {
			Long endTime = Long.valueOf(s_end);
			Long bgnTime = Long.valueOf(s_bgn);
			lapsedTime = endTime - bgnTime;					
		} catch (Exception e) {return lapsedTime;}
		return lapsedTime;
	}
	private Long calMaxTime(HashMap<String, List<String>> hashMaplist) {
		Long pageEndTime = 0l;
		List<String> bgnTimeList = hashMaplist.get("networkTimeBgn");
		List<String> endTimeList = hashMaplist.get("networkTimeEnd");
		List<String> bgnTimeList2 = hashMaplist.get("processTimeBgn");
		List<String> endTimeList2 = hashMaplist.get("processTimeEnd");
		if (bgnTimeList == null && endTimeList == null) return pageEndTime;
		
		for (int i=0;i<endTimeList.size();i++) {
			try {
				Long lapseTime = singleItemtime(bgnTimeList.get(i),endTimeList.get(i));
				lapseTime += singleItemtime(bgnTimeList2.get(i),endTimeList2.get(i));
				if (pageEndTime < lapseTime) pageEndTime = lapseTime;
			} catch (Exception e) { continue;}
		}
		return pageEndTime;
	}
	private ChartSeries calculatePageTotalTime(String barLabel,HashMap<String, List<String>> list) {
		ChartSeries chartSeries = new ChartSeries();
		chartSeries.setLabel(barLabel);
		Long pageMaxTime = calMaxTime(list);
		if (pageMaxTime > maxTime) maxTime = pageMaxTime;
		chartSeries.set(0,pageMaxTime);
		return chartSeries;
	}
	private void initBarModelDetail(HashMap<String, List<String>> list) {			
			List<String> bgnTimeList = list.get("networkTimeBgn");
			List<String> endTimeList = list.get("networkTimeEnd");
			List<String> bgnTimeList2 = list.get("processTimeBgn");
			List<String> endTimeList2 = list.get("processTimeEnd");
			if (bgnTimeList == null && endTimeList == null) return;
			
			List<String> sessionids = list.get("sessionid");
			for (int i=0;i<endTimeList.size();i++) {
				ChartSeries chartSeries = new ChartSeries();
				chartSeries.setLabel(sessionids.get(i));
				Long lapseTime = singleItemtime(bgnTimeList.get(i),endTimeList.get(i));
				lapseTime += singleItemtime(bgnTimeList2.get(i),endTimeList2.get(i));
				if (lapseTime > maxTime) maxTime = lapseTime;
				chartSeries.set(i,lapseTime);
				barChartModel.addSeries(chartSeries);
			}
	}

	/*
	 * getters and setters
	 */
	public BarChartModel getBarModelRoot() {
		HashMap<String, HashMap<String, HashMap<String, List<String>>>> webapp = LoadBalancerStatic.allServicesData.get(selectedApp);
		if (webapp != null) instance = webapp.get(instanceKey);
		if (instance != null) createbarModelRoot();
		if (barChartModel == null) barChartModel = new BarChartModel();
		return barChartModel;
	} 
	public void setbarChartModel(BarChartModel barChartModel) {
		this.barChartModel = barChartModel;
	}
	public Long getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(Long maxTime) {
		this.maxTime = maxTime;
	}
	public HashMap<String, HashMap<String, List<String>>> getInstance() {
		return instance;
	}
	public void setInstance(HashMap<String, HashMap<String, List<String>>> instance) {
		this.instance = instance;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setYLabel(String yLabel) {
		this.yLabel = yLabel;
	}
	public void setXLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	public BarChartModel getBarModelDetail(HashMap<String, List<String>> list) {
		createBarModelDetail(list);
		return barChartModel;
	}
	public BarChartModel getBarModelSessionId(String sessionid) {
		instance = LoadBalancerStatic.sessionidData.get(sessionid);
		if (instance != null) createbarModelRoot();
		if (barChartModel == null) barChartModel = new BarChartModel();
		return barChartModel;
	}
}
