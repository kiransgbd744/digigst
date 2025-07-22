package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Jithendra.B
 *
 * This Dto is Used for Inward and Outward DashBoard
 */

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Gstr9GstinInOutwardDashBoardDTO {

	@Expose
	private String section;

	@Expose
	private String subSection;

	@Expose
	private Boolean header;
	
	@Expose
	private BigDecimal digiComputeTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal digiComputeCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal autoCalCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal userInputCess = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal gstinCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal getCompTaxableVal = BigDecimal.ZERO;

	@Expose
	private BigDecimal getCompIgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal getCompCgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal getCompSgst = BigDecimal.ZERO;

	@Expose
	private BigDecimal getCompCess = BigDecimal.ZERO;

	public Gstr9GstinInOutwardDashBoardDTO() {
		super();
	}

	public Gstr9GstinInOutwardDashBoardDTO(String subSection, Boolean header) {
		super();
		this.subSection = subSection;
		this.header = header;
	}

}
