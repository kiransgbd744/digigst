package com.ey.advisory.core.async.domain.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "EY_JOB_DETAILS")
public class AsyncExecJob {

	@Id
/*	@GeneratedValue(strategy = GenerationType.IDENTITY)
*/	@SequenceGenerator(name = "sequence", sequenceName = "EY_JOB_DETAILS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)

	@Column(name = "ID")
	private Long jobId;

	@Column(name = "JOB_CATEG")
	private String jobCategory;

	@Column(name = "JOB_PARAMS")
	private String message;

	@Column(name = "JOB_STATUS")
	private String status;

	@Column(name = "CREATE_DATE")
	private Date createdDate;

	@Column(name = "UPDATE_DATE")
	private Date updatedDate;

	@Column(name = "ERR_CODE")
	private String errorCode;

	@Column(name = "ERR_MSG")
	private String errReason;

	@Column(name = "JOB_START_DATE")
	private Date jobStartDate;

	@Column(name = "JOB_END_DATE")
	private Date jobEndDate;

	@Column(name = "JOB_PRIORITY")
	private Long jobPriority;

	@Version
	@Column(name = "VERSION")
	private long version;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "IS_SCHEDULED")
	private boolean isScheduled;

	@Column(name = "USER_NAME")
	protected String userName;

	@Column(name = "PARENT_ID")
	protected Long parentId;

	@Column(name = "SCHEDULED_TIME")
	private Date scheduledTime;

	@Column(name = "PERIODIC_JOB_ID")
	private Long periodicJobId;

	@Column(name = "INSTANCE_ID")
	private String instanceId;
	
	@Column(name = "JOB_BUNDLE")
	private String jobBundle;

	@Column(name = "CHILD_JOBS_STATUS")
	protected String childJobsStatus;

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrReason() {
		return errReason;
	}

	public void setErrReason(String errReason) {
		this.errReason = errReason;
	}

	public String getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(String jobCategory) {
		this.jobCategory = jobCategory;
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

	public Long getJobPriority() {
		return jobPriority;
	}

	public void setJobPriority(Long jobPriority) {
		this.jobPriority = jobPriority;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public boolean isScheduled() {
		return isScheduled;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Long getPeriodicJobId() {
		return periodicJobId;
	}

	public void setPeriodicJobId(Long periodicJobId) {
		this.periodicJobId = periodicJobId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getChildJobsStatus() {
		return childJobsStatus;
	}

	public void setChildJobsStatus(String childJobsStatus) {
		this.childJobsStatus = childJobsStatus;
	}
	
	public boolean isPeriodic() {
		return periodicJobId != null;
	}

	public String getJobBundle() {
		return jobBundle;
	}

	public void setJobBundle(String jobBundle) {
		this.jobBundle = jobBundle;
	}

	@Override
	public String toString() {
		return "AsyncExecJob [jobId=" + jobId + ", jobCategory=" + jobCategory
				+ ", message=" + message + ", status=" + status
				+ ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", errorCode=" + errorCode + ", errReason="
				+ errReason + ", jobStartDate=" + jobStartDate + ", jobEndDate="
				+ jobEndDate + ", jobPriority=" + jobPriority + ", version="
				+ version + ", groupCode=" + groupCode + ", isScheduled="
				+ isScheduled + ", userName=" + userName + ", parentId="
				+ parentId + ", scheduledTime=" + scheduledTime
				+ ", periodicJobId=" + periodicJobId + ", instanceId="
				+ instanceId + ", childJobsStatus=" + childJobsStatus + "]";
	}


}
