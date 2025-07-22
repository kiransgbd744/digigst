package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr6Items {

	@Expose
	@SerializedName("num")
	private Integer num;

	@Expose
	@SerializedName("itm_det")
	private Gstr6ItemDetails itmdet;

	

}
