package com.ey.advisory.app.docs.dto.gstr9;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Saif.S
 *
 */
@Getter
@Setter
public class Gstr9PyTransactionInCyMapDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String section;

	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal cess = BigDecimal.ZERO;

}
