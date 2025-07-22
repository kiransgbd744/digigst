package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
@MappedSuperclass
public class GetGstr2aAmdhistItem {

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;
	
	@Column(name = "AMD_TYPE")
	protected String amdType;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal amdTaxableValue;

	@Column(name = "IGST_AMT")
	protected BigDecimal amdIgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal amdCessAmt;

	

}
