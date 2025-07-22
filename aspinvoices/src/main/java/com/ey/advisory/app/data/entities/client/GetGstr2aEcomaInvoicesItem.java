/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aEcomaInvoicesItem {

	@Column(name = "ITEM_NUMBER")
	protected int itmNum;

	@Column(name = "TAX_VALUE")
	protected BigDecimal taxableValue;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

}
