package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "RETURN_FILING_CONFIG")
public class ReturnFilingConfigEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "REQUEST_ID")
	protected Long requestId;
	
	@Expose
	@Column(name = "USER_UPLOAD_DATE")
	protected LocalDateTime dateOfUpload;
	
	@Expose
	@Column(name = "UPLOADED_FILENAME")
	protected String fileName;
	
	@Expose
	@Column(name = "NO_OF_GSTINS")
	protected Long noOfGstins;
	
	@Expose
	@Column(name = "STATUS")
	protected String status;
	
	@Expose
	@Column(name = "GENERATED_FILENAME")
	protected String filePath;
	
	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;
	
	@Expose
	@Column(name = "ERROR_MSG")
	protected String errorMsg;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;
	
	@Expose
	@Column(name = "DOC_ID")
	protected String docId;

}