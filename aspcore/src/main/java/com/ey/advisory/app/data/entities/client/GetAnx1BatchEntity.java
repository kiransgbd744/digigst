/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

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

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX1_BATCH_TABLE")
@Setter
@Getter
public class GetAnx1BatchEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String sgstin;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "FROM_TIME")
	private String fromTime;
	
	@Column(name = "TO_TIME")
	private String toTime;

	@Column(name = "API_SECTION")
	private String apiSection;

	@Column(name = "GET_TYPE")
	private String type;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "START_TIME")
	private LocalDateTime startTime;

	@Column(name = "END_TIME")
	private LocalDateTime endTime;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "IS_FILED")
	private boolean isFiled;

	@Column(name = "ETIN")
	private String eTin;

	@Column(name = "INVOICE_COUNT")
	private Integer invCount;

	@Column(name = "ACK_RECPT_NUM")
	private String ackRecptNum;

	@Column(name = "ARN_DATE")
	private String arnDate;

	@Column(name = "ANX1_CHKSUM")
	private String anx1Chksum;

	@Column(name = "ANX2_CHKSUM")
	private String anx2Chksum;

	@Column(name = "IS_NIL")
	private boolean isNil;

	@Column(name = "AMD_NUM")
	private int amdNum;
	
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Column(name = "IS_TOKEN_RESPONSE")
	private boolean isTokenResponse;
	
	@Column(name = "USER_REQUEST_ID")
	private Long userRequestId;
	
	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Column(name = "RETRY_COUNT")
	private Long retryCount;
	
	@Column(name = "PORT_CODE")
	private String portCode;

	@Column(name = "BILL_OF_ENTRY_NUM")
	private Long billOfEntryNum;

	@Column(name = "BILL_OF_ENTRY_CREATED")
	private LocalDate billOfEntryCreated;
	
	@Column(name = "DELTA_NEW_COUNT")
	private int deltaNewCount;
	
	@Column(name = "DELTA_MODIFIED_COUNT")
	private int deltaModifiedCount;
	
	@Column(name = "DELTA_DELETED_COUNT")
	private int deltaDeletedCount;
	
	@Column(name = "DELTA_TOTAL_COUNT")
	private int deltaTotalCount;
	
	@Column(name = "ERP_PUSH_STATUS")
	private String erpPushStatus;

	@Column(name = "IS_DELTA_GET")
	private boolean isDeltaGet;
	
	@Column(name = "IS_AUTO_GET")
	private boolean isAutoGet;
	
	@Column(name = "SFTP_PUSH_STATUS")
	private String sftpStatus;
	
	@Column(name = "IMS_RETURN_TYPE")
	private String imsReturnType;
	
	@Column(name = "HASH_KEY")
	private String hashKey;
}
