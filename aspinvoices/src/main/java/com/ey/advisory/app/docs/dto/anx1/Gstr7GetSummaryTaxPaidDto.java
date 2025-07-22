
package com.ey.advisory.app.docs.dto.anx1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7GetSummaryTaxPaidDto {

	@Expose
	@SerializedName("pd_by_cash")
	List<Gstr7TaxPaySummaryDto> pd_by_cash;
	

}