package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FinancialYearFinalRespDto {

	@Expose
	@SerializedName("Ratio1")
	private List<FinancialYearRespDto> ratio1;

	@Expose
	@SerializedName("Ratio2")
	private List<FinancialYearRespDto> ratio2;

	@Expose
	@SerializedName("Ratio3")
	private List<FinancialYearRespDto> ratio3;
}
