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
public class RetTbl3bDto {

	@Expose
	@SerializedName("rev")
	private RetItemDetailsDto rev;

	@Expose
	@SerializedName("imps")
	private RetItemDetailsDto imps;

	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;

	@Expose
	@SerializedName("chksum")
	private String chksum;
}
