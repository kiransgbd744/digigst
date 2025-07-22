package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Umesha.M
 *
 */
@Data
public class B2CLLineItem {

	@Expose
	@SerializedName("num")
	private Integer lineNumber;

	@Expose
	@SerializedName("itm_det")
	private B2clLineItemDetail itemDetail;

}
