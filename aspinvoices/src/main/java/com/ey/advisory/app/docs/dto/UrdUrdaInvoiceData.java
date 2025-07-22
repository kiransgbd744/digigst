package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Data
public class UrdUrdaInvoiceData {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String financialPeriod;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

	@Expose
	@SerializedName("urd")
	private List<UrdLineItem> urdLineItems;
	
	@Expose
	@SerializedName("tcsa")
	private List<UrdaLineItem> urdaLineItems;

	@Expose
	@SerializedName("errorCd")
	private String errorCd;

	@Expose
	@SerializedName("errorMsg")
	private String errorMsg;

}
