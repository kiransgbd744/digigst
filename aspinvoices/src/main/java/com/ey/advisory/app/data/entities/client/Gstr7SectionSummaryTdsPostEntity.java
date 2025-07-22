package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
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
 * 
 * @author Siva
 *
 */
@Entity
@Table(name = "GETGSTR7_SUMMARY_TDS_POST_OFFSETLIB")
@Data
public class Gstr7SectionSummaryTdsPostEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "GSTIN")
	private String gstIn;

	@Column(name = "RETURN_PERIOD")
	private String taxperiod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "SECTION_NAME")
	private String sectionName;

	@Column(name = "DEBIT_DESC")
	private String debitDesc;

	@Column(name = "LIAB_ID")
	private int liabId;

	@Column(name = "TRAN_CODE")
	private int TransCode;

	@Column(name = "TRAN_DATE")
	private LocalDate transDate;

	
	@Column(name = "TRAN_CODE_DESC")
	private String transCodeDesc;

	
	/*@Column(name = "TOTAL_IGST")
	private BigDecimal ttigst;

	@Column(name = "TOTAL_CGST")
	private BigDecimal ttcgst;

	@Column(name = "TOTAL_SGST")
	private BigDecimal ttsgst;
*/
	/*@Column(name = "CESS")
	private BigDecimal cess;*/
	
	@Column(name = "IGST_TAX")
	private BigDecimal igstTax;

	@Column(name = "IGST_INTEREST")
	private BigDecimal igstInterest;

	@Column(name = "IGST_PENDING")
	private BigDecimal igstPending;

	@Column(name = "IGST_FEE")
	private BigDecimal igstFee;

	@Column(name = "IGST_OTHERS")
	private BigDecimal igstOthers;

	@Column(name = "IGST_TOTAL")
	private BigDecimal igstTotal;

	@Column(name = "CGST_TAX")
	private BigDecimal cgstTax;

	@Column(name = "CGST_INTEREST")
	private BigDecimal cgstInterest;

	@Column(name = "CGST_PENDING")
	private BigDecimal cgstPending;

	@Column(name = "CGST_FEE")
	private BigDecimal cgstFee;

	@Column(name = "CGST_OTHERS")
	private BigDecimal cgstOthers;

	@Column(name = "CGST_TOTAL")
	private BigDecimal cgstTotal;

	@Column(name = "SGST_TAX")
	private BigDecimal sgstTax;

	@Column(name = "SGST_INTEREST")
	private BigDecimal sgstInterest;

	@Column(name = "SGST_PENDING")
	private BigDecimal sgstPending;

	@Column(name = "SGST_FEE")
	private BigDecimal sgstFee;

	@Column(name = "SGST_OTHERS")
	private BigDecimal sgstOthers;

	@Column(name = "SGST_TOTAL")
	private BigDecimal sgstTotal;
	
	@Column(name = "CESS_TAX")
	private BigDecimal cessTax;

	@Column(name = "CESS_INTEREST")
	private BigDecimal cessInterest;

	@Column(name = "CESS_PENDING")
	private BigDecimal cessPending;

	@Column(name = "CESS_FEE")
	private BigDecimal cesstFee;

	@Column(name = "CESS_OTHERS")
	private BigDecimal cessOthers;

	@Column(name = "CESS_TOTAL")
	private BigDecimal cessTotal;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private boolean isdelete;

}
