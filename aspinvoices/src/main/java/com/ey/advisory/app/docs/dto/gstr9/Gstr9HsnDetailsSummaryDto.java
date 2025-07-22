package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Data
public class Gstr9HsnDetailsSummaryDto {
	
	public Gstr9HsnDetailsSummaryDto(String section, String subSection) {
		super();
		this.section = section;
		this.subSection = subSection;
	}

	public Gstr9HsnDetailsSummaryDto() {
		
	}

	@Expose
	@SerializedName("section")
	private String section;
	
	@Expose
	@SerializedName("subSection")
	private String subSection;

	@Expose
	@SerializedName("digiPrcount")
	private AtomicInteger digiPrcount= new AtomicInteger(0);
	
	@Expose
	@SerializedName("digiPrtotalQty")
	private BigDecimal digiPrtotalQty = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiPrTaxableVal")
	private BigDecimal digiPrTaxableVal = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiPrIgst")
	private BigDecimal digiPrIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiPrCgst")
	private BigDecimal digiPrCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiPrSgst")
	private BigDecimal digiPrSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiPrCess")
	private BigDecimal digiPrCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("digiComputetotalQtycount")
	private AtomicInteger digiComputetotalQtycount = new AtomicInteger(0);
	
	@Expose
	@SerializedName("digiComputetotalQty")
	private BigDecimal digiComputetotalQty = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiComputeTaxableVal")
	private BigDecimal digiComputeTaxableVal = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiComputeIgst")
	private BigDecimal digiComputeIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiComputeCgst")
	private BigDecimal digiComputeCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiComputeSgst")
	private BigDecimal digiComputeSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("digiComputeCess")
	private BigDecimal digiComputeCess = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("gstr9Summcount")
	private AtomicInteger gstr9Summcount= new AtomicInteger(0);

	@Expose
	@SerializedName("gstr9SummQty")
	private BigDecimal gstr9SummQty= BigDecimal.ZERO;

	@Expose
	@SerializedName("gstr9SummTaxableVal")
	private BigDecimal gstr9SummTaxableVal = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstr9SummIgst")
	private BigDecimal gstr9SummIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstr9SummCgst")
	private BigDecimal gstr9SummCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstr9SummSgst")
	private BigDecimal gstr9SummSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("gstr9SummCess")
	private BigDecimal gstr9SummCess = BigDecimal.ZERO;

}
