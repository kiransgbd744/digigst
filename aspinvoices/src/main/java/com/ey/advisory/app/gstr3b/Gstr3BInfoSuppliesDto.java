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
public class Gstr3BInfoSuppliesDto {

	@Expose
	private String sectionName;
	
	@Expose
	private String subSectionName;
	
	@Expose
	private BigDecimal digiIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal digiCess = BigDecimal.ZERO;
	
	
	/**
	 * @param sectionName
	 * @param subSectionName
	 */
	public Gstr3BInfoSuppliesDto(String sectionName,
			String subSectionName) {
		super();
		this.sectionName = sectionName;
		this.subSectionName = subSectionName;
	}
	
	
	
}
