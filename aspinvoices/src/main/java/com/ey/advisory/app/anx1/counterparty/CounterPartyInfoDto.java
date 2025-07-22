package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CounterPartyInfoDto implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String sgstin;
	
	@Expose
	private String tableSection;
	
	@Expose
	private String action;
	
	@Expose
	private Integer cnt = 0;
	
	@Expose
	private BigDecimal taxableValue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal igstAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cgstAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal cessAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal docAmt = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxPayable = BigDecimal.ZERO;
	
	public CounterPartyInfoDto(String sgstin, String tableSection,
			String action, Integer cnt, BigDecimal taxableValue,
			BigDecimal igstAmt, BigDecimal cgstAmt, BigDecimal sgstAmt,
			BigDecimal cessAmt, BigDecimal docAmt, BigDecimal taxPayable) {
		super();
		this.sgstin = sgstin;
		this.tableSection = tableSection;
		this.action = action;
		this.cnt = cnt;
		this.taxableValue = taxableValue;
		this.igstAmt = igstAmt;
		this.cgstAmt = cgstAmt;
		this.sgstAmt = sgstAmt;
		this.cessAmt = cessAmt;
		this.docAmt = docAmt;
		this.taxPayable = taxPayable;
	}
	public CounterPartyInfoDto() {
		super();
	}
	@Override
	public String toString() {
		return "CounterPartyInfoDto [sgstin=" + sgstin + ", tableSection="
				+ tableSection + ", action=" + action + ", cnt=" + cnt
				+ ", taxableValue=" + taxableValue + ", igstAmt=" + igstAmt
				+ ", cgstAmt=" + cgstAmt + ", sgstAmt=" + sgstAmt + ", cessAmt="
				+ cessAmt + ", docAmt=" + docAmt + ", taxPayable=" + taxPayable
				+ "]";
	}
	
	

}
