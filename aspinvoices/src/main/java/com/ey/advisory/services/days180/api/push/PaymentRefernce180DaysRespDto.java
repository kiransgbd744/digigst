/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class PaymentRefernce180DaysRespDto {
	
	@Expose
	@SerializedName("TotalCount")
	private Integer totalCount;

	@Expose
	@SerializedName("ProcessCount")
	private Integer processedCount;
	
	@Expose
	@SerializedName("ErrorCount")
	private Integer errorCount;
	
	@Expose
	@SerializedName("invoices")
	List<PaymentreferenceDocSaveRespDto> docSaveResp;
}

