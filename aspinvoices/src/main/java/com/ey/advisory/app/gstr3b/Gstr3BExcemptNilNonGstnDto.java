package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author vishal.verma
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Gstr3BExcemptNilNonGstnDto {
	

	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
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
	private String pos;
	
	@Expose
	private BigDecimal interState = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal intraState = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal interStateCompute = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal intraStateCompute = BigDecimal.ZERO;
	
	/**
	 * @param sectionName
	 * @param subSectionName
	 */
	public Gstr3BExcemptNilNonGstnDto(String sectionName,
			String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	

}
