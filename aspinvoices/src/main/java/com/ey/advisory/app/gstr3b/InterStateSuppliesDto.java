package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InterStateSuppliesDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer pos = 0;
	private BigDecimal taxableVal = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	/**
	 * @param pos
	 * @param taxableVal
	 * @param igst
	 */
	public InterStateSuppliesDto(Integer pos, BigDecimal taxableVal,
			BigDecimal igst) {
		super();
		this.pos = pos;
		this.taxableVal = taxableVal;
		this.igst = igst;
	}
	@Override
	public String toString() {
		return "InterStateSuppliesDto [pos=" + pos + ", taxableVal="
				+ taxableVal + ", igst=" + igst + "]";
	}
	/**
	 * 
	 */
	public InterStateSuppliesDto() {
		super();
	}

	
}
