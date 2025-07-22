/**
 * 
 */
package com.ey.advisory.app.docs.dto.ret;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class RetTbl5Dto {

	///// GET/////

	@Expose
	@SerializedName("tds")
	private RetItemDetailsDto tds;

	@Expose
	@SerializedName("tcs")
	private RetItemDetailsDto tcs;

	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;

	@Expose
	@SerializedName("chksum")
	private String chksum;
}
