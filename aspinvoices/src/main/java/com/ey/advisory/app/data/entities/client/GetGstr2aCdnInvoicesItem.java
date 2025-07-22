/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aCdnInvoicesItem {

	@Column(name = "ITEM_NUMBER")
	protected Integer itemnum;

	@Column(name = "TAX_VALUE")
	protected BigDecimal taxval;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstamt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstamt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstamt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessamt;

	@Column(name = "TAX_RATE")
	protected BigDecimal taxrate;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derReturnPeriod;

}
