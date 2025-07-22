/**
 * 
 */
package com.ey.advisory.app.data.services.itc04stocktrack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class Itc04StockTrackingReqDto {

	@Expose
	@SerializedName("requestType")
	private String requestType;

	@Expose
	@SerializedName("fy")
	private String fy;

	@Expose
	@SerializedName("fromReturnPeriod")
	private String fromReturnPeriod;

	@Expose
	@SerializedName("toReturnPeriod")
	private String toReturnPeriod;

	@Expose
	@SerializedName("fromChallanDate")
	private String fromChallanDate;

	@Expose
	@SerializedName("toChallanDate")
	private String toChallanDate;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs = new HashMap<>();

}
