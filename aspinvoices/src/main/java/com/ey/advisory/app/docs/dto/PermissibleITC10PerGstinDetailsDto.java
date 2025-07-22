package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PermissibleITC10PerGstinDetailsDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("prTaxAmt2A")
	private BigDecimal prtaxAmount2a = BigDecimal.ZERO;

	@Expose
	@SerializedName("prTaxValue")
	private BigDecimal prTaxValue = BigDecimal.ZERO;

	@Expose
	@SerializedName("prTaxAvlValue")
	private BigDecimal prTaxAvlAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalItcAvl")
	private BigDecimal totalItcAvailable = BigDecimal.ZERO;

	@Expose
	@SerializedName("eligibleCrd")
	private BigDecimal eligibleCredit = BigDecimal.ZERO;

	@Expose
	@SerializedName("eligibleCRDPerc")
	private BigDecimal eligibleCrdPerc = BigDecimal.ZERO;

	@Expose
	@SerializedName("maxPermItc")
	private BigDecimal maxPermItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("exsCreditAvl")
	private BigDecimal exsCreditAvl = BigDecimal.ZERO;

	@Expose
	@SerializedName("taxBreakUpDetails")
	private List<TaxBreakUpDetailsDto> taxBreakUpDetails;

}
