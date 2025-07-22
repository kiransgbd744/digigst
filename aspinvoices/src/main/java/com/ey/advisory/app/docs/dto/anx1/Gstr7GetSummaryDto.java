
package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7GetSummaryDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String fp;

	@Expose
	@SerializedName("tds")
	Gstr7GstnTdsSummeyDto tds;

	@Expose
	@SerializedName("tdsa")
	Gstr7GstnTdsSummeyDto tdsa;
	
	@Expose
	@SerializedName("tax_pay")
	List<Gstr7TaxPaySummaryDto> tax_pay;
	
	@Expose
	@SerializedName("tax_paid")
	Gstr7GetSummaryTaxPaidDto tax_paid;
	

}