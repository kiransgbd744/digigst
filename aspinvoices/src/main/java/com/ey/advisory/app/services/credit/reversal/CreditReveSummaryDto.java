package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditReveSummaryDto {

	@Expose
	@SerializedName("reversalRatio1")
	private BigDecimal reversalRatio1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("reversalRatio2")
	private BigDecimal reversalRatio2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("reversalRatio3")
	private BigDecimal reversalRatio3 = BigDecimal.ZERO;

	@Expose
	@SerializedName("turnoverRatio1")
	private BigDecimal turnoverRatio1 = BigDecimal.ZERO;

	@Expose
	@SerializedName("turnoverRatio2")
	private BigDecimal turnoverRatio2 = BigDecimal.ZERO;

	@Expose
	@SerializedName("turnoverRatio3")
	private BigDecimal turnoverRatio3 = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1")
	private String ratio1;

	@Expose
	@SerializedName("ratio2")
	private String ratio2;

	@Expose
	@SerializedName("ratio3")
	private String ratio3;

}
