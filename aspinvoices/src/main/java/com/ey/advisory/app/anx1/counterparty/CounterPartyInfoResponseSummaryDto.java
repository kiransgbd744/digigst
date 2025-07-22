package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterPartyInfoResponseSummaryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Expose
	private String type;
	
	@Expose
	private Integer percent = 0;
	
	@Expose
	private Integer count = 0;
	
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
	
	@Expose
	private BigDecimal taxPayable = BigDecimal.ZERO;
	
	
	
	public CounterPartyInfoResponseSummaryDto(){}
	
	public CounterPartyInfoResponseSummaryDto(String type) {
		this.type = type;
	}
	
	public CounterPartyInfoResponseSummaryDto(String type, Integer percent,
			Integer count, BigDecimal taxableVal, BigDecimal igst,
			BigDecimal cgst, BigDecimal sgst, BigDecimal cess,
			BigDecimal taxPayable) {
		super();
		this.type = type;
		this.percent = percent;
		this.count = count;
		this.taxableVal = taxableVal;
		this.igst = igst;
		this.cgst = cgst;
		this.sgst = sgst;
		this.cess = cess;
		this.taxPayable = taxPayable;
	}
	
}
