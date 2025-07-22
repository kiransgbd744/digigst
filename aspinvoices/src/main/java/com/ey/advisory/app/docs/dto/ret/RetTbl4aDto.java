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
public class RetTbl4aDto {

	@Expose
	@SerializedName("elgcrdt")
	private RetItemDetailsDto elgcrdt;

	@Expose
	@SerializedName("provcrdt")
	private RetItemDetailsDto provcrdt;
	
	@Expose
	@SerializedName("itcadj")
	private RetItemDetailsDto itcadj;
	
	/////GET/////
	
	@Expose
	@SerializedName("rejcn")
	private RetItemDetailsDto rejcn;

	@Expose
	@SerializedName("pencn")
	private RetItemDetailsDto pencn;
	
	@Expose
	@SerializedName("acccn")
	private RetItemDetailsDto acccn;

	@Expose
	@SerializedName("rev")
	private RetItemDetailsDto rev;
	
	@Expose
	@SerializedName("imps")
	private RetItemDetailsDto imps;

	@Expose
	@SerializedName("impg")
	private RetItemDetailsDto impg;
	
	@Expose
	@SerializedName("impgsez")
	private RetItemDetailsDto impgsez;

	@Expose
	@SerializedName("isdc")
	private RetItemDetailsDto isdc;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;

}
