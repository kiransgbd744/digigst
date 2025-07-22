package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ibm.icu.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "GETGSTR2A_ERP_CONSOLIDATED")
@Data
public class GetGstr2aErpConsolidatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SGSTIN")
	private String sgstin;

	@Column(name = "CGSTIN")
	private String cgstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "CHUNK_ID")
	private Integer chunkId;

	@Column(name = "CFS")
	private String cfs;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "SUPPLIER_NAME")
	private String supplietName;

	@Column(name = "STATE_NAME")
	private String stateName;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "INV_NUM")
	private String invNum;

	@Column(name = "INV_DATE")
	private LocalDate invDate;

	@Column(name = "POS")
	private String pos;

	@Column(name = "RCHRG")
	private String rchrg;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "ORG_INV_NUM")
	private String orgInvNum;

	@Column(name = "ORG_INV_DATE")
	private LocalDate orgInvDate;

	@Column(name = "ITEM_NUMBER")
	private Integer itemNumber;

	@Column(name = "CRDR_PRE_GST")
	private String crdrPreGst;

	@Column(name = "ITC_ELIGIBLE")
	private String itcEligible;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "INV_VAL")
	private BigDecimal invVal;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "IS_SENT_TO_ERP")
	private boolean isSentToErp;

	@Column(name = "SENT_TO_ERP_DATE")
	private LocalDate sentToErpDate;

	@Column(name = "GET_BATCH_ID")
	private Long getBatchId;

	@Column(name = "ERP_BATCH_ID")
	private Long erpBatchId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
}
