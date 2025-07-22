package com.ey.advisory.core.async.domain.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EY_PERIODIC_JOBS")
public class PeriodicExecJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "JOB_CATEG")
	private String jobCategory;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CRON_EXPR")
	private String cronExpression;

	@Column(name = "VALIDITY_ST_DATE")
	private Date validityStDate;

	@Column(name = "VALIDITY_END_DATE")
	private Date validityEndDate;

	@Column(name = "LAST_JOB_POST_DATE")
	private Date lastPostedDate;

	@Column(name = "LAST_JOB_ID")
	private Long lastPostedJobId;

	@Column(name = "LAST_JOB_COMP_DATE")
	private Date jobCompletionDate;

	@Column(name = "LAST_JOB_START_DATE")
	private Date jobstartDate;

	@Column(name = "CREATE_DATE")
	private Date createdDate;

	@Column(name = "UPDATE_DATE")
	private Date updatedDate;

	@Column(name = "REF_PARAMS")
	private String refParams;

	@Column(name = "ALLOW_PARALLEL_EXEC")
	private boolean allowParallelExec;

	@Column(name = "LAST_EVAL_MSG")
	private String lastEvalMsg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(String jobCategory) {
		this.jobCategory = jobCategory;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getValidityStDate() {
		return validityStDate;
	}

	public void setValidityStDate(Date validityStDate) {
		this.validityStDate = validityStDate;
	}

	public Date getValidityEndDate() {
		return validityEndDate;
	}

	public void setValidityEndDate(Date validityEndDate) {
		this.validityEndDate = validityEndDate;
	}

	public Date getLastPostedDate() {
		return lastPostedDate;
	}

	public void setLastPostedDate(Date lastPostedDate) {
		this.lastPostedDate = lastPostedDate;
	}

	public Long getLastPostedJobId() {
		return lastPostedJobId;
	}

	public void setLastPostedJobId(Long lastPostedJobId) {
		this.lastPostedJobId = lastPostedJobId;
	}

	public Date getJobCompletionDate() {
		return jobCompletionDate;
	}

	public void setJobCompletionDate(Date jobCompletionDate) {
		this.jobCompletionDate = jobCompletionDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getRefParams() {
		return refParams;
	}

	public void setRefParams(String refParams) {
		this.refParams = refParams;
	}

	public boolean isAllowParallelExec() {
		return allowParallelExec;
	}

	public void setAllowParallelExec(boolean allowParallelExec) {
		this.allowParallelExec = allowParallelExec;
	}

	public Date getJobstartDate() {
		return jobstartDate;
	}

	public void setJobstartDate(Date jobstartDate) {
		this.jobstartDate = jobstartDate;
	}

	public String getLastEvalMsg() {
		return lastEvalMsg;
	}

	public void setLastEvalMsg(String lastEvalMsg) {
		this.lastEvalMsg = lastEvalMsg;
	}

	@Override
	public String toString() {
		return "PeriodicExecJob [id=" + id + ", jobCategory=" + jobCategory
				+ ", isActive=" + isActive + ", cronExpression="
				+ cronExpression + ", validityStDate=" + validityStDate
				+ ", validityEndDate=" + validityEndDate + ", lastPostedDate="
				+ lastPostedDate + ", lastPostedJobId=" + lastPostedJobId
				+ ", jobCompletionDate=" + jobCompletionDate + ", jobstartDate="
				+ jobstartDate + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", refParams=" + refParams
				+ ", allowParallelExec=" + allowParallelExec + ", lastEvalMsg="
				+ lastEvalMsg + "]";
	}



}
