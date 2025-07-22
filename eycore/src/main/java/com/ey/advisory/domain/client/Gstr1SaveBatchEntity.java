package com.ey.advisory.domain.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * This entity class is responsible for storing gstn batch details in the
 * database
 * 
 * @author Hemasundar.J
 *
 * @param <T>
 */

@Entity
@Table(name = "GSTR1_GSTN_SAVE_BATCH")
@Data
public class Gstr1SaveBatchEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_SAVE_BATCH_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "GSTN_SAVE_REF_ID")
	private String refId;

	@Column(name = "GSTN_SAVE_STATUS")
	private String gstnStatus;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "BATCH_DATE")
	private LocalDateTime batchDate;

	@Column(name = "GSTN_RESP_DATE")
	private LocalDateTime gstnRespDate;

	@Column(name = "BATCH_SIZE")
	private int batchSize;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedTaxperiod;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "TXN_ID")
	private String txnId;

	@Column(name = "ERROR_COUNT")
	private Integer errorCount;

	@Column(name = "SAVE_REQUEST_PAYLOAD")
	private Clob saveRequestPayload;

	@Column(name = "SAVE_RESPONSE_PAYLOAD")
	private Clob saveResponsePayload;

	@Column(name = "GET_RESPONSE_PAYLOAD")
	private Clob getResponsePayload;
	
	@Column(name = "H_MAX_ID")
	private Long hMaxId;
	
	@Column(name = "V1_MAX_ID")
	private Long v1MaxId;
	
	@Column(name = "V2_MAX_ID")
	private Long v2MaxId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "OPERATION_TYPE")
	private String operationType;
	
	@Column(name = "RETRY_COUNT")
	private Long retryCount;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "USER_REQUEST_ID")
	private Long userRequestId;
	
	@Column(name = "USER_MAX_ID")
	private Long userMaxId;

	@Column(name = "ORIGIN")
	private String origin;

}
