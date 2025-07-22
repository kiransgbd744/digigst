/**
 * 
 */
package com.ey.advisory.app.vendor.service;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Entity
@Data
@ToString
@Table(name = "TBL_PAYLOAD_METADATA_VENDOR_VALIDATOR")
public class VendorValidatorPayloadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Column(name = "ERROD_CODE")
	private String errorCode;

	@Column(name = "JSON_ERROR_RESPONSE")
	private String jsonErrorResponse;

	@Column(name = "NOTIFICATION_STATUS")
	private String notificationStatus;
	
	@Column(name = "REVERSE_INT_PUSH_STATUS")
	private Integer revIntPushStatus;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "GSTIN_COUNT")
	private Integer gstinCount;

	@Column(name = "PUSH_TYPE")
	private Integer pushType;

	@Column(name = "CHECK_SUM")
	private String checkSum;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "CLOUD_CHECK_SUM")
	private String cloudCheckSum;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "ERROR_COUNT")
	private Integer errorCount;

	@Column(name = "TOTAL_COUNT")
	private Integer totalCount;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;
	
	@Column(name = "REQ_PAYLOAD")
	protected Clob reqPlayload;
	
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "GET_GSTIN_VALIDATOR_STATUS")
	private boolean getGstinValidationStatus = false;
	
	@Column(name = "GET_FILLING_FREQUENCY_STATUS")
	private boolean getFillingFrequencyStatus = false;
	
	@Column(name = "ERROD_CODE_FILLING_FREQUENCY")
	private String errorCodeFillingfrequency;
	
	@Column(name = "ERROD_MSG_FILLING_FREQUENCY")
	private String errorMsgFillingfrequency;
	
	@Column(name = "GET_FILLING_DETAILS_STATUS")
	private boolean getFillingDetailsStatus = false;
	
	@Column(name = "ERROD_CODE_FILLING_DETAILS")
	private String errorCodeFillingDetails;
	
	@Column(name = "ERROD_MSG_FILLING_DETAILS")
	private String errorMsgFillingDetails;
}
