package com.ey.advisory.app.docs.dto;

public class GetGstr2aDetailsStatusItemsDto {

	private String section;
	private String callStatus;
	private String callDatetime;
	private String successStatus;
	private String successDatetime;

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(String callStatus) {
		this.callStatus = callStatus;
	}

	public String getCallDatetime() {
		return callDatetime;
	}

	public void setCallDatetime(String callDatetime) {
		this.callDatetime = callDatetime;
	}

	public String getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}

	public String getSuccessDatetime() {
		return successDatetime;
	}

	public void setSuccessDatetime(String successDatetime) {
		this.successDatetime = successDatetime;
	}

}
