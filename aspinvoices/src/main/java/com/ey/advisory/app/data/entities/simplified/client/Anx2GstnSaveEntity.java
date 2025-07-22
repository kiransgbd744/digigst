package com.ey.advisory.app.data.entities.simplified.client;



import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Siva.Nandam
 *
 */
@Entity
@Table(name = "ANX2_GSTN_SAVE")
@Data
public class Anx2GstnSaveEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RECON_LINK_ID")
	private Long reconLinkId;
	
	@Column(name = "A2_RECIPIENT_GSTIN")
	private String recipgstin;
	
	@Column(name = "RETURN_PERIOD")
	private String retPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private String derivedRetPeriod;
	
	@Column(name = "CTIN")
	private String sgstin;
	
	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "A2_DOC_TYPE")
	private String a2DocType;

	@Column(name = "A2_DOC_NUM")
	private String a2DocNum;

	@Column(name = "A2_DOC_DATE")
	private LocalDate a2DocDate;

	@Column(name = "USER_RESPONSE")
	private String userResponse;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "ITCENTITLEMENT")
	private String itcEntitlement;

	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "ORG_RETURN_PERIOD")
	private String orgReturnPeriod;

	@Column(name = "BATCH_ID")
	private Long gstnBatchId;
	
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentToGSTNDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedToGSTNDate;
	
	@Column(name = "IS_GSTN_ERROR")
	private boolean isError;
	
	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

}