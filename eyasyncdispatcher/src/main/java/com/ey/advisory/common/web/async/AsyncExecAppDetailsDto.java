package com.ey.advisory.common.web.async;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class AsyncExecAppDetailsDto {

	@Expose
	private String status;
	
	@Expose
	private int activeThreadCount;
	
	@Expose
	private int maxPoolSize;
	
	@Expose
	private int corePoolSize;
	
	@Expose
	private String msg;

	@Expose
	private String taskTypes;
	
	@Expose
	private String groupCode;
	
	@Expose
	private String digiGstUserName;
	
	@Expose
	private String apiKey;
	
	@Expose
	private String apiSecret;
	
	@Expose
	private String gspToken;
	
	
	public AsyncExecAppDetailsDto(String status, int activeThreadCount, 
			int maxPoolSize, int corePoolSize, String taskTypes, String msg) {
		this.status = status;
		this.activeThreadCount = activeThreadCount;
		this.maxPoolSize = maxPoolSize;
		this.corePoolSize = corePoolSize;
		this.taskTypes = taskTypes;		
		this.msg = msg;
	}

	public String getStatus() {
		return status;
	}

	public int getActiveThreadCount() {
		return activeThreadCount;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public String getMsg() {
		return msg;
	}
	
	public String getTaskTypes() {
		return this.taskTypes;
	}

	@Override
	public String toString() {
		return "AsyncExecAppDetailsDto [status=" + status
				+ ", activeThreadCount=" + activeThreadCount + ", maxPoolSize="
				+ maxPoolSize + ", corePoolSize=" + corePoolSize + ", msg="
				+ msg + ", taskTypes=" + taskTypes + "]";
	}
	

}
