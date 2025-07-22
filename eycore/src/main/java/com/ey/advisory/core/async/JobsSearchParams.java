package com.ey.advisory.core.async;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ey.advisory.core.search.PageRequest;

public class JobsSearchParams {

	private String groupCode;

	private Date jobStartDate;

	private Date jobEndDate;

	private List<String> status = new ArrayList<>();

	private String category;

	private String userName;

	private String jobId;
	
	private PageRequest pageReq;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public Date getJobStartDate() {
		return jobStartDate;
	}

	public void setJobStartDate(Date jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	public Date getJobEndDate() {
		return jobEndDate;
	}

	public void setJobEndDate(Date jobEndDate) {
		this.jobEndDate = jobEndDate;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		// Make sure that the list assigned is never null.
		this.status = status != null ? status : new ArrayList<>();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public PageRequest getPageRequest() {
		return pageReq;
	}

	public void setPageInfo(PageRequest pageReq) {
		this.pageReq = pageReq;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Override
	public String toString() {
		return "JobsReqParams [groupCode=" + groupCode + ", jobStartDate="
				+ jobStartDate + ", jobEndDate=" + jobEndDate + ", status="
				+ status + ", category=" + category + ", pageReq=" + pageReq
				+ ", userName=" + userName + ", jobId=" + jobId + "]";
	}

}
