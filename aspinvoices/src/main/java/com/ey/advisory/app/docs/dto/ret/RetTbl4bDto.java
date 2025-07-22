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
public class RetTbl4bDto {

	@Expose
	@SerializedName("itcrevinv")
	private RetItemDetailsDto itcrevinv;

	@Expose
	@SerializedName("inelgcredits")
	private RetItemDetailsDto inelgcredits;
	
	@Expose
	@SerializedName("itcrev")
	private RetItemDetailsDto itcrev;
	
	@Expose
	@SerializedName("itcrevoth")
	private RetItemDetailsDto itcrevoth;
	
	/////GET/////
	
	@Expose
	@SerializedName("postrjctdcredits")
	private RetItemDetailsDto postrjctdcredits;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
}
