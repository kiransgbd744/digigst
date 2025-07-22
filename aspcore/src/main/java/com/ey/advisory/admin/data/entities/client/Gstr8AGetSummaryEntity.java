/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun KA
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GSTR8A_GET_SUMMARY")
public class Gstr8AGetSummaryEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CGSTIN")
	private String cgstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "FIN_YEAR")
	private String finYear;
	
	@Column(name = "SGSTIN")
	private String sgstin;

	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_TYPE")
	private String docType;
	
	@Column(name = "DOC_NUM")
	private String docNum;
	
	@Column(name = "DOC_DATE")
	private LocalDate docDate;
	
	@Column(name = "ORIGINAL_NOTE_TYPE")
	private String originalNoteType;
	
	@Column(name = "ORIGINAL_DOC_NUM")
	private String originalDocNum;
	
	@Column(name = "ORIGINAL_DOC_DATE")
	private LocalDate originalDocDate;
	
	@Column(name = "ORIGINAL_INV_NUM")
	private String originalInvNum;
	
	@Column(name = "ORIGINAL_INV_DATE")
	private LocalDate originalInvDate;
	
	@Column(name = "POS")
	private String pos;

	@Column(name = "REVERSE_CHARGE")
	private String reverseCharge;
	
	@Column(name = "INVOICE_VALUE")
	private BigDecimal invValue;

	@Column(name = "TAX_PAYABLE")
	private BigDecimal taxPayable;

	@Column(name = "IGST")
	private BigDecimal igst;

	@Column(name = "CGST")
	private BigDecimal cgst;
	
	@Column(name = "SGST")
	private BigDecimal sgst;
	
	@Column(name = "CESS")
	private BigDecimal cess;

	@Column(name = "ELIGIBLE_ITC")
	private String eligibleItc;
	
	@Column(name = "REASON")
	private String reason;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDate createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDate modifiedOn;

	@Column(name = "DERIVED_RETURN_PERIOD")
	private Integer derivedTaxPeriod;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "FILING_DATE")
	private LocalDate filingDate;
	
	@Column(name = "TAX_RATE")
	private Integer taxRate;

}

