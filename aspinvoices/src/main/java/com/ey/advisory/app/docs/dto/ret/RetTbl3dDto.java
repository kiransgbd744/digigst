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
public class RetTbl3dDto {

	@Expose
	@SerializedName("expnilsup")
	private RetItemDetailsDto expnilsup;

	@Expose
	@SerializedName("nongstsup")
	private RetItemDetailsDto nongstsup;
	
	@Expose
	@SerializedName("revnt")
	private RetItemDetailsDto revnt;

	@Expose
	@SerializedName("impgsez")
	private RetItemDetailsDto impgsez;
	
	
	/////GET/////
	@Expose
	@SerializedName("expnil")
	private RetItemDetailsDto expnil;

	@Expose
	@SerializedName("nongst")
	private RetItemDetailsDto nongst;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
	
}
