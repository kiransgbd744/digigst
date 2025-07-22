package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Jithendra.B
 *
 */

@Setter
@Getter
public class Gstr9InwardUserInputDTO {

	@Expose
	private String section;

	@Expose
	private String subSection;

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

	/*
	 * used only for inward 7H - other reversals
	 */ 
	@Expose
	private String particulers;

}
