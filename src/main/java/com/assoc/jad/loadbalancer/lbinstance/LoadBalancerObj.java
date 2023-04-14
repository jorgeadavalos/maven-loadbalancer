package com.assoc.jad.loadbalancer.lbinstance;

/**
 * to reference system fields , look here: 
 * <a href="https://docs.oracle.com/javase/8/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html">com.sun.management</a>
 * @author jorge
 *
 */

public class LoadBalancerObj {
	private long 	processTime;
	private String 	viewId;
	private String 	serverName;
	private String	externalIP;
	private int 	serverPort;
	private String 	Instance; 
	private String 	schema;
	private String 	loadBalancerWSURL;
	private long 	networkTimeBgn;
	private long 	networkTimeEnd;
	private double	processCpuLoadBgn;
	private double	processCpuLoadEnd;
	private long 	processTimeBgn;
	private long 	processTimeEnd;
	//System data
	private long	committedVirtualMemorySize;
	private long	freePhysicalMemorySize;
	private long	freeSwapSpaceSize;
	private double	processCpuLoad;
	private long	processCpuTime;
	private double	systemCpuLoad;
	private long	totalPhysicalMemorySize;
	private long	totalSwapSpaceSize;

	public long getProcessTime() {
		return processTime;
	}
	public void setProcessTime(long processTime) {
		this.processTime = processTime;
	}
	public void setViewId(String viewId) {
		this.viewId = viewId;		
	}
	public String getViewId() {
		return this.viewId;		
	}
	public void setInstance(String contextName) {
		this.Instance = contextName;
	}
	public String getInstance() {
		return this.Instance;
	}
	public void setInstancePort(int serverPort) {
		this.serverPort = serverPort;
	}
	public int getInstancePort() {
		return this.serverPort;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerName() {
		return this.serverName;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getLoadBalancerWSURL() {
		return loadBalancerWSURL;
	}
	public void setLoadBalancerWSURL(String loadBalancerWSURL) {
		this.loadBalancerWSURL = loadBalancerWSURL;
	}
	public long getCommittedVirtualMemorySize() {
		return committedVirtualMemorySize;
	}
	public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
		this.committedVirtualMemorySize = committedVirtualMemorySize;
	}
	public long getFreePhysicalMemorySize() {
		return freePhysicalMemorySize;
	}
	public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
		this.freePhysicalMemorySize = freePhysicalMemorySize;
	}
	public long getFreeSwapSpaceSize() {
		return freeSwapSpaceSize;
	}
	public void setFreeSwapSpaceSize(long freeSwapSpaceSize) {
		this.freeSwapSpaceSize = freeSwapSpaceSize;
	}
	public double getProcessCpuLoad() {
		return processCpuLoad;
	}
	public void setProcessCpuLoad(double processCpuLoad) {
		this.processCpuLoad = processCpuLoad;
	}
	public long getProcessCpuTime() {
		return processCpuTime;
	}
	public void setProcessCpuTime(long processCpuTime) {
		this.processCpuTime = processCpuTime;
	}
	public double getSystemCpuLoad() {
		return systemCpuLoad;
	}
	public void setSystemCpuLoad(double systemCpuLoad) {
		this.systemCpuLoad = systemCpuLoad;
	}
	public long getTotalPhysicalMemorySize() {
		return totalPhysicalMemorySize;
	}
	public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
	}
	public long getTotalSwapSpaceSize() {
		return totalSwapSpaceSize;
	}
	public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
		this.totalSwapSpaceSize = totalSwapSpaceSize;
	}
	public double getProcessCpuLoadBgn() {
		return processCpuLoadBgn;
	}
	public void setProcessCpuLoadBgn(double processCpuLoadBgn) {
		this.processCpuLoadBgn = processCpuLoadBgn;
	}
	public double getProcessCpuLoadEnd() {
		return processCpuLoadEnd;
	}
	public void setProcessCpuLoadEnd(double processCpuLoadEnd) {
		this.processCpuLoadEnd = processCpuLoadEnd;
	}
	public long getProcessTimeEnd() {
		return processTimeEnd;
	}
	public void setProcessTimeEnd(long processTimeEnd) {
		this.processTimeEnd = processTimeEnd;
	}
	public long getProcessTimeBgn() {
		return processTimeBgn;
	}
	public void setProcessTimeBgn(long processTimeBgn) {
		this.processTimeBgn = processTimeBgn;
	}
	public long getNetworkTimeBgn() {
		return networkTimeBgn;
	}
	public void setNetworkTimeBgn(long networkTimeBgn) {
		this.networkTimeBgn = networkTimeBgn;
	}
	public long getNetworkTimeEnd() {
		return networkTimeEnd;
	}
	public void setNetworkTimeEnd(long networkTimeEnd) {
		this.networkTimeEnd = networkTimeEnd;
	}
	public String getExternalIP() {
		return externalIP;
	}
	public void setExternalIP(String externalIP) {
		this.externalIP = externalIP;
	}
}
