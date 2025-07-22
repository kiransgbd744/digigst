package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aTcsInvoicesItem {

	/*@Column(name = "ITEM_NUMBER")
	private int itmNum;

	@Column(name = "TAX_VALUE")
	private BigDecimal taxableValue;*/
	
	@Column(name = "SUPPLIER_INV_VALUE")
	private BigDecimal supVal;
	
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxbleVal;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derReturnPeriod;
	
	@Column(name = "IGST_RATE")
	private BigDecimal igstRate;

	@Column(name = "CGST_RATE")
	private BigDecimal cgstRate;

	@Column(name = "SGST_RATE")
	private BigDecimal sgstRate;

	@Column(name = "CESS_RATE")
	private BigDecimal cessRate;

	/*@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;*/
	
	/*@Column(name = "HSN")
	private Integer hsn;*/
}
