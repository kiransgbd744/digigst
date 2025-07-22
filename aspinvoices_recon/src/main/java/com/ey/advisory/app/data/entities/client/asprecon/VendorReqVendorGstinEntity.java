package com.ey.advisory.app.data.entities.client.asprecon;

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

@Entity
@Table(name= "VENDOR_REQUEST_VGSTIN")
@Getter
@Setter
@ToString
public class VendorReqVendorGstinEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name="VENDOR_GSTIN")
	private String vendorGstin;

	@Column(name="EMAIL_STATUS")
	private String emailStatus;

	
	@Column(name="REPORT_STATUS")
	private String reportStatus;

	@Column(name="FILEPATH")
	private String filePath;

	@Column(name="ERROR_DESC")
	private String errorDesc;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;
	 // new addition
	
	@Column(name = "TOTAL_RECORDS")
	private Integer totalRec;
	
	@Column(name = "IS_RESPONDED")
	private boolean isResp;
	
	@Column(name = "JSON_COUNT")
	private Integer jsonCnt;
	
	@Column(name = "RESPONDED_RECORDS_CNT")
	private Integer respRecordsCnt;
	
	@Column(name = "RESPONDED_FILE_PATH")
	private String respFilePath;
	
	@Column(name = "TOTAL_FILE_PATH")
	private String totFilePath;
	
	@Column(name = "IS_READ")
	private boolean isRead;
	
	@Column(name = "DOC_ID")
	private String docId;
	
	@Column(name = "DOC_ID_RESPONDED_FILE_PATH")
	private String docIdRespondedFilePath;
	
	@Column(name = "DOC_ID_TOTAL_FILE_PATH")
	private String docIdTotalFilePath;
	
	
	
}
