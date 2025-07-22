package com.ey.advisory.core.async.domain.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * This table is used to dump the stack trace of the errors encountered while
 * executing a job, so that anyone with PROD DB read only access can view the 
 * stack trace instantly. This is a convenient way to view the full stack trace
 * without going through the full log files. The contents of this table can
 * be purged by the DBA team once in every two days. 
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name = "EY_JOB_STACK_TRACE")
public class AsyncExecStackTrace {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "EY_JOB_STACK_TRACE_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "JOB_ID")
	private Long jobId;

	@Column(name = "ERR_MSG")
	@Lob
	private String message;	
	
	@Column(name = "CREATED_DT")	
	private Date createdDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "AsyncExecStackTrace [id=" + id + ", jobId=" + jobId
				+ ", message=" + message + ", createdDate=" + createdDate + "]";
	}

	
	
}
