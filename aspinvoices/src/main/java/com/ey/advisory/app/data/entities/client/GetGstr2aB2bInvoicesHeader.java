/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aB2bInvoicesHeader {

	@Column(name = "SGSTIN")
	protected String sgstin;

	@Column(name = "CGSTIN")
	protected String cgstin;

	/*@Column(name = "CFS")
	protected String cfs;*/

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "SUPPLIER_INV_NUM")
	protected String invNum;

	@Column(name = "SUPPLIER_INV_DATE")
	protected LocalDate invDate;

	/*
	 * @Column(name = "ORG_INV_NUM") protected String origInvNum;
	 * 
	 * @Column(name = "ORG_INV_DATE") protected LocalDate origInvDate;
	 */

	@Column(name = "SUPPLIER_INV_VAL")
	protected BigDecimal invValue;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "RCHRG")
	protected String rchrg;

	@Column(name = "INV_TYPE")
	protected String invType;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercentage;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxable;

	@Column(name = "TAX_PERIOD")
	protected String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

	/*
	 * @Column(name = "CGSTIN_PAN") protected String cgstinPan;
	 * 
	 * @Column(name = "SGSTIN_PAN") protected String sgstinPan;
	 */

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable =
	 * false) protected GetGstr2aBatchEntity b2bBatchIdGstr2a;
	 */

	@Column(name = "BATCH_ID")
	protected Long b2bBatchIdGstr2a;

	@Column(name = "API_SECTION")
	protected String getSectionType;

	/*
	 * @Column(name = "SEC_7_ACT") protected Boolean sec7;
	 * 
	 * @Column(name = "REFUND_ELG") protected Boolean rfndElg;
	 * 
	 * @Column(name = "ITC_ENT") protected Boolean itc;
	 * 
	 * @Column(name = "UPLOAD_DATE") protected LocalDate uploadDate;
	 */
	@Column(name = "ACTION_TAKEN")
	protected String action;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;
	
	/*@Column(name = "IS_FILE_UPLOAD_DATA")
	protected boolean isFileUploadData;*/
	
	@Column(name = "SUPPLIER_TRADE_LEGAL_NAME")
	protected String supTradeName;
	
	/**
	 * version v2.0 changes
	 */
	
	@Column(name = "CFS")
	protected String cfsGstr1;
	
	@Column(name = "CFS_GSTR3B")
	protected String cfsGstr3B;
	
	@Column(name = "CANCEL_DATE")
	protected LocalDate cancelDate;
	
	@Column(name = "FILE_DATE")
	protected LocalDate fileDate;
	
	@Column(name = "FILE_PERIOD")
	protected String filePeriod;
	
	@Column(name = "ORG_INV_AMD_PERIOD")
	protected String orgInvAmdPeriod;
	
	@Column(name = "ORG_INV_AMD_TYPE")
	protected String orgInvAmdType;
	
	@Column(name = "SUPPLY_TYPE")
	protected String supType;
	
	@Column(name = "IRN_NUM")
	private String irnNum;

	/*@Column(name = "IRN_GEN_DATE")
	private String irnGenDate;*/
	
	@Column(name = "IRN_GEN_DATE")
	private LocalDate irnGenDate;

	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSrcType;
	
	@Column(name = "INV_KEY")
	protected String invKey;
	
	  @Column(name = "LINKING_DOC_KEY")
	    private String lnkingDocKey;

	
	
	/*@Column(name = "DELTA_INV_STATUS")
	protected String deltaInStatus;*/

}
