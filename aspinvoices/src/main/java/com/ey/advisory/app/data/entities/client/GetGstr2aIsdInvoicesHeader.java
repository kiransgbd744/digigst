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
public class GetGstr2aIsdInvoicesHeader {

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "RET_PERIOD")
	protected String returnPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

	@Column(name = "CTIN")
	protected String cgstin;

	@Column(name = "CFS")
	protected String counFillStatus;

	@Column(name = "CHKSUM")
	protected String checkSum;

	@Column(name = "DOC_NUM")
	protected String documentNumber;

	@Column(name = "DOC_DATE")
	protected LocalDate documentDate;
	
	/*// CR num
	@Column(name = "CREDIT_NOTE_NUM")
	protected String creditNoteNumber;

	//CR date
	@Column(name = "CREDIT_NOTE_DATE")
	protected LocalDate creditNoteDate;*/
	

	@Column(name = "ISD_DOC_TYPE")
	protected String isdDocumentType;

	@Column(name = "ITC_ELG")
	protected String itcElg;

	/*@Column(name = "ORG_DOC_DATE")
	protected String originalDocumentDate;

	@Column(name = "ORG_DOC_NUM")
	protected String originalDocumentNumber;*/

	@Column(name = "IGST_AMT")
	protected BigDecimal igstamt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstamt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstamt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cesamt;

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
	
	@Column(name = "SUPPLIER_TRADE_LEGAL_NAME")
	private String supTradeName;
	
	@Column(name = "BATCH_ID")
	private Long isdBatchIdGstr2a;
	
	@Column(name = "SUPPLY_TYPE")
	protected String supType;
	
	@Column(name = "INV_KEY")
	protected String invKey;
	
	/*@Column(name = "DELTA_INV_STATUS")
	protected String deltaInStatus;*/

}
