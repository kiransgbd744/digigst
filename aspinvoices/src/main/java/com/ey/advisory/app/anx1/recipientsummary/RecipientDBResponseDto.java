package com.ey.advisory.app.anx1.recipientsummary;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipientDBResponseDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Expose
	private String cgstin;
	
	@Expose
	private String cPan;
	
	@Expose
	private String cName;
	
	@Expose
	private String tableSection;
	
	@Expose
	private String action;
	
	@Expose
	private String docType;
	
	@Expose
	private Integer count = 0;
	
	@Expose
	private BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal taxAmt = BigDecimal.ZERO;
	
	@Expose
	private String level;
	
	public RecipientDBResponseDto(Integer count, 
			BigDecimal taxableVal, BigDecimal taxAmt) {
		super();
		this.count = count;
		this.taxableVal = taxableVal;
		this.taxAmt = taxAmt;
		
	}
	public RecipientDBResponseDto() {
		super();
	}
	@Override
	public String toString() {
		return "RecipientDBResponseDto [cgstin=" + cgstin + ", cPan=" + cPan
				+ ", cName=" + cName + ", tableSection=" + tableSection
				+ ", action=" + action + ", docType=" + docType + ", count="
				+ count + ", taxableVal=" + taxableVal + ", taxAmt=" + taxAmt
				+ "]";
	}
	/**
	 * @param level
	 */
	public RecipientDBResponseDto(String level) {
		super();
		this.level = level;
	}
	

}
