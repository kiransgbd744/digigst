package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
@Entity
@Table(name = "GSTN_SUBMIT")
public class GstnSubmitEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RET_PERIOD")
	private String retPeriod;
	
	@Column(name = "GSTN_SUBMIT_STATUS")
	private String gstnStatus;
	
	@Column(name = "REFERENCE_ID ")
	private String refId;
	
	@Column(name = "TXN_ID")
	private String txnId;
	
	@Column(name = "RETURN_TYPE")
	private String returnType;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
