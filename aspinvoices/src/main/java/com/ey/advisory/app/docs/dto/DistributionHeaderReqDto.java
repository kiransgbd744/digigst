/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class DistributionHeaderReqDto {

	@Expose
	@SerializedName("isdGstin")
	private String isdGstin;

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;
	
	@Expose
	@SerializedName("dataOriginTypeCode")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("docs")
	private List<DistributuionItemReqDto> disInvoices;
}
