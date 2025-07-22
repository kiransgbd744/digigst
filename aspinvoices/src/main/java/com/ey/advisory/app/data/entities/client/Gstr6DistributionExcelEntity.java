package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;/**
 * 
 * @author Balakrishna.S
 *
 */

@Entity
@Table(name = "GSTR6_ISD_DISTRIBUTION_AS_ENTERED")
@Data
public class Gstr6DistributionExcelEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR6_ISD_DISTRIBUTION_AS_ENTERED_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE) 	
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("isdGstin")
	@Column(name = "ISD_GSTIN")
	protected String isdGstin;

	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String custGstin;

	@Expose
	@SerializedName("retPeriod")
	@Column(name = "TAX_PERIOD")
	protected String retPeriod;

	@Expose
	@SerializedName("stateCode")
	@Column(name = "STATE_CODE")
	protected String stateCode;

	@Expose
	@SerializedName("orgCustGstin")
	@Column(name = "ORG_CUST_GSTIN")
	protected String orgCustGstin;

	@Expose
	@SerializedName("orgStateCode")
	@Column(name = "ORG_STATE_CODE")
	protected String orgStateCode;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("docNum")
	@Column(name = "DOC_NUM")
	protected String docNum;

	@Expose
	@SerializedName("orgDocNum")
	@Column(name = "ORG_DOC_NUM")
	protected String orgDocNum;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

	@Expose
	@SerializedName("orgDocDate")
	@Column(name = "ORG_DOC_DATE")
	protected LocalDate orgDocDate;

	@Expose
	@SerializedName("orgCrNum")
	@Column(name = "ORG_CR_NUM")
	protected String orgCrNum;

	@Expose
	@SerializedName("orgCrDate")
	@Column(name = "ORG_CR_DATE")
	protected LocalDate orgCrDate;

	@Expose
	@SerializedName("eligibleIndicator")
	@Column(name = "ELIGIBLE_INDICATOR")
	protected String eligibleIndicator;

	
	@Expose
	@SerializedName("igstAsIgst")
	@Column(name = "IGST_AMT_AS_IGST")
	protected String igstAsIgst;

	
	@Expose
	@SerializedName("igstAsSgst")
	@Column(name = "IGST_AMT_AS_SGST")
	protected String igstAsSgst;
	
	@Expose
	@SerializedName("igstAsCgst")
	@Column(name = "IGST_AMT_AS_CGST")
	protected String igstAsCgst;
	
	@Expose
	@SerializedName("sgstAsSgst")
	@Column(name = "SGST_AMT_AS_SGST")
	protected String sgstAsSgst;

	@Expose
	@SerializedName("sgstAsIgst")
	@Column(name = "SGST_AMT_AS_IGST")
	protected String sgstAsIgst;
	
	@Expose
	@SerializedName("cgstAsCgst")
	@Column(name = "CGST_AMT_AS_CGST")
	protected String cgstAsCgst;

	@Expose
	@SerializedName("cgstAsIgst")
	@Column(name = "CGST_AMT_AS_IGST")
	protected String cgstAsIgst;
	
	@Expose
	@SerializedName("cessAmount")
	@Column(name = "CESS_AMT")
	protected String cessAmount;

	@Expose
	@SerializedName("derived_Ret_period")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derived_Ret_period;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;
	
	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	@Expose
	@SerializedName("fileName")
	@Column(name = "FILE_NAME")
	protected String fileName;
	
	@Expose
	@SerializedName("processKey")
	@Column(name = "DOC_KEY")
	protected String processKey;
	
	/*@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;
*/
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	protected boolean isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	protected boolean isProcessed;

	
	@Expose
	@SerializedName("isSavedToGstn")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSavedToGstn;
	
	@Expose
	@SerializedName("isSentToGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSentToGstn;
	

	@Expose
	@SerializedName("isSubmitted")
	@Column(name = "IS_SUBMITTED")
	protected boolean isSubmitted;

	
	@Expose
	@SerializedName("sentToGstnDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGstnDate;
	
	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginType;
	

	
	
	
/*	@Transient
	private Long entityId;

	@Transient
	private Long groupId;

	@Transient
	private Map<Long, List<Pair<String, String>>> entityAtValMap;

	@Transient
	private Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap;

	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;
	
	@Transient
	private String formReturnType;
	
	@Transient
	private boolean isCgstInMasterCust;*/
	
	
	
}