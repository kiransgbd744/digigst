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
public class RetTbl3cDto {

	@Expose
	@SerializedName("advrec")
	private RetItemDetailsDto advrec;

	@Expose
	@SerializedName("advadj")
	private RetItemDetailsDto advadj;
	
	@Expose
	@SerializedName("rdctn")
	private RetItemDetailsDto rdctn;

	@Expose
	@SerializedName("expliab")
	private RetItemDetailsDto expliab;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
	
	@Expose
	@SerializedName("dn")
	private RetItemDetailsDto dn;
	
	@Expose
	@SerializedName("cn")
	private RetItemDetailsDto cn;
	
	
}
