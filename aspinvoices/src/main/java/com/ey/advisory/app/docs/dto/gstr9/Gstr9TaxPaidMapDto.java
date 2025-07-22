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
public class Gstr9TaxPaidMapDto  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String subSection;

	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal paidThrCash = BigDecimal.ZERO;

	@Expose
	private BigDecimal paidThrItcIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal paidThrItcCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal paidThrItcSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal paidThrItcCess = BigDecimal.ZERO;

}
