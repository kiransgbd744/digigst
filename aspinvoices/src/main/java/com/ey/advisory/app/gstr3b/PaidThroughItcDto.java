package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaidThroughItcDto {

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("otrci")
	private BigDecimal otrci;

	@Expose
	@SerializedName("pdi")
	private BigDecimal pdi;

	@Expose
	@SerializedName("pdc")
	private BigDecimal pdc;

	@Expose
	@SerializedName("pds")
	private BigDecimal pds;

	@Expose
	@SerializedName("pdcs")
	private BigDecimal pdcs;

	@Expose
	@SerializedName("rci8")
	private BigDecimal rci8;

	@Expose
	@SerializedName("inti10")
	private BigDecimal inti10;

	@Expose
	@SerializedName("lateFee12")
	private BigDecimal lateFee12;

	// freeze changes
	@Expose
	@SerializedName("otrc7")
	private BigDecimal otrc7;

	@Expose
	@SerializedName("ucb14")
	private BigDecimal ucb14;

	@Expose
	@SerializedName("acr15")
	private BigDecimal acr15;

	@Expose
	@SerializedName("otrci2A")
	private BigDecimal otrci2A;

	@Expose
	@SerializedName("otrci2B")
	private BigDecimal otrci2B;

	// Negative Liability

	@Expose
	@SerializedName("adjNegative2i")
	private BigDecimal adjNegative2i;

	@Expose
	@SerializedName("netOthRecTaxPayable2i")
	private BigDecimal netOthRecTaxPayable2i;

	@Expose
	@SerializedName("adjNegative8A")
	private BigDecimal adjNegative8A;
	
	@Expose
	@SerializedName("rci9")
	private BigDecimal rci9;//8-8a


}
