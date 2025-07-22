package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh NK
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissibleITC10PercentDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("tx2a")
	private BigDecimal taxAmount2a = BigDecimal.ZERO;

	@Expose
	@SerializedName("txPr")
	private BigDecimal prTaxAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("txAbvlPr")
	private BigDecimal prTaxAvlAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalItcAvl")
	private BigDecimal totalItcAvailable = BigDecimal.ZERO;

	@Expose
	@SerializedName("eligibleCrd")
	private BigDecimal eligibleCredit = BigDecimal.ZERO;

	@Expose
	@SerializedName("eligibleCrdPerc")
	private BigDecimal eligibleCrdPerc = BigDecimal.ZERO;

	@Expose
	@SerializedName("maxPermItc")
	private BigDecimal maxPermItc = BigDecimal.ZERO;

	@Expose
	@SerializedName("exsCrdAvl")
	private BigDecimal exsCrdAvl = BigDecimal.ZERO;

}
