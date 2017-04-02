package controller;

public class App {
	String name;
	int status;
	String ip;
	int port;
	int max_thread_count;
	AppType appType;
	public App(String name, int status, String ip, int port, int max_thread_count, AppType appType){
		this.name = name;
		this.status = status;
		this.ip = ip;
		this.port = port;
		this.max_thread_count = max_thread_count;
		this.appType = appType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getMax_thread_count() {
		return max_thread_count;
	}
	public void setMax_thread_count(int max_thread_count) {
		this.max_thread_count = max_thread_count;
	}
	public AppType getAppType() {
		return appType;
	}
	public void setAppType(AppType appType) {
		this.appType = appType;
	}
}
