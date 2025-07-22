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
public class GetGstr2aTdsInvoicesItem {

	/*@Column(name = "ITEM_NUMBER")
	private int itmNum;

	@Column(name = "TAX_VALUE")
	private BigDecimal taxableValue;*/

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

/*	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;*/
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derReturnPeriod;
	
	/*@Column(name = "HSN")
	private Integer hsn;*/
}
