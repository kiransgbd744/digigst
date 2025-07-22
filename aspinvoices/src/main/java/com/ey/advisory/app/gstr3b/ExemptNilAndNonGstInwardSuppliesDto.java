package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExemptNilAndNonGstInwardSuppliesDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal interStateSupplies = BigDecimal.ZERO;
	private BigDecimal intraStateSupplies = BigDecimal.ZERO;
	/**
	 * @param interStateSupplies
	 * @param intraStateSupplies
	 */
	public ExemptNilAndNonGstInwardSuppliesDto(BigDecimal interStateSupplies,
			BigDecimal intraStateSupplies) {
		super();
		this.interStateSupplies = interStateSupplies;
		this.intraStateSupplies = intraStateSupplies;
	}
	/**
	 * 
	 */
	public ExemptNilAndNonGstInwardSuppliesDto() {
		super();
	}
	@Override
	public String toString() {
		return "ExemptNilAndNonGstInwardSuppliesDto [interStateSupplies="
				+ interStateSupplies + ", intraStateSupplies="
				+ intraStateSupplies + "]";
	}
	
	

}
