package com.ey.advisory.app.anx1.counterparty;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterPartyTotalsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Expose
	private BigDecimal taxableValue = BigDecimal.ZERO ;

	@Expose
	private BigDecimal totalTax = BigDecimal.ZERO ;
	
	@Expose
	private Integer cnt = 0;

	public CounterPartyTotalsDto(BigDecimal taxableValue, BigDecimal totalTax,
			Integer cnt) {
		super();
		this.taxableValue = taxableValue;
		this.totalTax = totalTax;
		this.cnt = cnt;
	}

	public CounterPartyTotalsDto() {
		super();
	}

	
	
	

}
