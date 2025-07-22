package com.ey.advisory.common;

import com.google.gson.annotations.Expose;

public class Message {
	@Expose
	private Long id;

	@Expose
	private String jobCategory;

	@Expose
	private String paramsJson;

	@Expose
	private String groupCode;

	@Expose
	private String userName;

	@Expose
	private String messageType;

	public Message(Long id, String msgType, String groupCode, String userName,
			String paramsJson) {
		super();
		this.id = id;
		this.paramsJson = paramsJson;
		this.userName = userName;
		this.groupCode = groupCode;
		this.messageType = msgType;
	}

	public Message() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}

	public String getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(String jobCategory) {
		this.jobCategory = jobCategory;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", groupCode=" + groupCode + ", userName="
				+ userName + ", messageType=" + messageType + ", paramsJson="
				+ paramsJson + "jobCategory=" + jobCategory + "]";
	}

}
