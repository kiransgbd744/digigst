package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class CreditTurnOverFinancialProcess2Dto {

	@Expose
	private String turnoverComp;

	@Expose
	private String sno;
	
	@Expose
	private String total;

	@Expose
	private String april;

	@Expose
	private String may;

	@Expose
	private String june;

	@Expose
	private String july;

	@Expose
	private String aug;

	@Expose
	private String sep;

	@Expose
	private String oct;

	@Expose
	private String nov;

	@Expose
	private String dec;

	@Expose
	private String jan;

	@Expose
	private String feb;

	@Expose
	private String march;
	@Expose
	private List<CreditTurnOverFinancialItem2Dto> items;
}
