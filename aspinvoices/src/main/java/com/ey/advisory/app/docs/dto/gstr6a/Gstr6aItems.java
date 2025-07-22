package com.ey.advisory.app.docs.dto.gstr6a;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6aItems {

	@Expose
	@SerializedName("num")
	private Integer num;

	@Expose
	@SerializedName("itm_det")
	private Gstr6aItemDetails itmdet;

}
