
package com.ey.advisory.app.docs.dto.anx1;


import java.time.LocalDate;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr7TaxPaySummaryDto {

	@Expose
	@SerializedName("igst")
	Gstr7TaxPayIgstSummaryDto igst;
	
	@Expose
	@SerializedName("cgst")
	Gstr7TaxPayIgstSummaryDto cgst;
	
	@Expose
	@SerializedName("sgst")
	Gstr7TaxPayIgstSummaryDto sgst;
	
	@Expose
	@SerializedName("cess")
	Gstr7TaxPayIgstSummaryDto cess;

	@Expose
	@SerializedName("liab_id")
	private int liabId;

	@Expose
	@SerializedName("trancd")
	private int TransCode;

	@Expose
	@SerializedName("trandate")
	private String transDate;
	
	@Expose
	@SerializedName("debit_id")
	private String debit_id;
	
	
}