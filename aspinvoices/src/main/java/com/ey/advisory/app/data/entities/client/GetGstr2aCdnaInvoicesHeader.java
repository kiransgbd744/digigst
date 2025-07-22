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
public class GetGstr2aCdnaInvoicesHeader {

	@Column(name = "CTIN")
	protected String countergstin; 
	
	@Column(name = "GSTIN")
	protected String gstin; 
	
	/*@Column(name = "CFS")
	protected String counFillStatus;*/

	@Column(name = "CHKSUM")
	protected String checkSum;

	@Column(name = "NOTE_TYPE")
	protected String credDebRefVoucher;

	@Column(name = "NOTE_NUMBER")
	protected String credDebRefVoucherNum;
	
	@Column(name = "ORG_NOTE_TYPE")
	protected String oriCredDebType;

	@Column(name = "ORG_NOTE_NUMBER")
	protected String oriCredDebNum;

	@Column(name = "NOTE_DATE")
	protected LocalDate credDebRefVoucherDate;

	@Column(name = "ORG_NOTE_DATE")
	protected LocalDate oriCredDebDate;

	@Column(name = "NOTE_VALUE")
	protected BigDecimal notevalue;	
	
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxVal;	
	
	@Column(name = "INV_NUMBER")
	protected String invNum;

	@Column(name = "P_GST")
	protected String preGst;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffvalue;

	@Column(name = "INV_DATE")
	protected LocalDate invDate;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstamt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstamt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstamt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessamt;

	/*@ManyToOne
	@JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2aBatchEntity cdnBatchIdGstr2a;*/
	
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;
	
	/*@Column(name = "UPLODED_BY")
	protected Integer uploadedBy;

	@Column(name = "C_FLAG")
	protected String cFlag;
	
	@Column(name = "FROM_TIME")
	protected String fromTime;*/
	
	@Column(name = "BATCH_ID")
	protected Long cdnBatchIdGstr2a;
	
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
	
	@Column(name = "SUPPLIER_TRADE_LEGAL_NAME")
	protected String supTradeName;
	
	/*@Column(name = "POS")
	protected String pos;*/
	
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
	
	@Column(name = "POS")
	protected String pos;
	
	@Column(name = "RCHRG")
	protected String rcrg;
	
	@Column(name = "INV_TYPE")
	protected String invType;
	
	@Column(name = "D_FLAG")
	protected String dLinkFlag;
	
	@Column(name = "SUPPLY_TYPE")
	protected String supType;

	@Column(name = "INV_KEY")
	protected String invKey;
	
	  @Column(name = "LINKING_DOC_KEY")
	    private String lnkingDocKey;

	
	/*@Column(name = "DELTA_INV_STATUS")
	protected String deltaInStatus;*/

}
