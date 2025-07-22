package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr3BNewSuppliesDto {

	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
	@Expose
	private BigDecimal digiTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCompTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCompIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCompCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCompSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCompCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userCess = BigDecimal.ZERO;
	
	@Expose
	private Boolean radioFlag = false;
	
	@Expose
	private String level = "L1";

	/**
	 * @param sectionName
	 * @param subSectionName
	 */
	public Gstr3BNewSuppliesDto(String sectionName,
			String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	
	
	
}
