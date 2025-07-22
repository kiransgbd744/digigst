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
public class RetTbl4itcDto {

	@Expose
	@SerializedName("itccg")
	private RetItemDetailsDto itccg;

	@Expose
	@SerializedName("itccs")
	private RetItemDetailsDto itccs;
	
	/////GET/////
	
	@Expose
	@SerializedName("firstmon")
	private RetItemDetailsDto firstmon;
	
	@Expose
	@SerializedName("secmon")
	private RetItemDetailsDto secmon;
	
	@Expose
	@SerializedName("itcavl")
	private RetItemDetailsDto itcavl;
	
	@Expose
	@SerializedName("netitcavl")
	private RetItemDetailsDto netitcavl;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
}
