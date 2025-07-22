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
public class CdnurLineItem {

	@Expose
	@SerializedName("num")
	private int lineNumber;

	@Expose
	@SerializedName("itm_det")
	private CdnurLineItemDetail itemDetail;

}
