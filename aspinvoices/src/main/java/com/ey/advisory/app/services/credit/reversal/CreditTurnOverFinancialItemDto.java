package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class CreditTurnOverFinancialItemDto {

	@Expose
	private String turnoverComp;

	@Expose
	private String sno;
	
	@Expose
	private BigDecimal total = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal april = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal may = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal june = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal july = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal aug = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal sep = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal oct = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal nov = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal dec = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal jan = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal feb = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal march = BigDecimal.ZERO;
	
	@Expose
	private List<CreditTurnOverFinancialItemDto> items;
}
