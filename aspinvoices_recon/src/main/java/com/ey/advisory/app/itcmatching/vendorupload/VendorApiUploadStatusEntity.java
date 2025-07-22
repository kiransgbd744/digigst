package com.ey.advisory.app.itcmatching.vendorupload;

import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_VENDOR_API_UPLOAD")
@Setter
@Getter
@ToString
public class VendorApiUploadStatusEntity {

	@Id
	@Column(name = "REF_ID")
	private String refId;

	@Column(name = "API_STATUS")
	private String apiStatus;

	@Column(name = "TOTAL_RECORDS")
	private int total;

	@Column(name = "PROCESSED_RECORDS")
	private int processed;

	@Column(name = "ERROR_RECORDS")
	private int error;

	@Column(name = "INFORMATION_RECORDS")
	private int information;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name ="ENTITY_ID")
	private Long entityId;
		
	@Column(name = "ERROR_DESC") 
	private String errorDesc;
	
	@Column(name = "REQUEST_PAYLOAD")
	private Clob reqPayload;
	
	@Column(name = "RESPONSE_PAYLOAD")
	private Clob respPayload;
	
}