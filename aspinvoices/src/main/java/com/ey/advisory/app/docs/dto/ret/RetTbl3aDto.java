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
public class RetTbl3aDto {

	@Expose
	@SerializedName("priorliab")
	private RetItemDetailsDto priorliab;

	@Expose
	@SerializedName("expliab")
	private RetItemDetailsDto expliab;
	
	/////////GET//////////
	
	@Expose
	@SerializedName("b2c")
	private RetItemDetailsDto b2c;
	
	@Expose
	@SerializedName("b2b")
	private RetItemDetailsDto b2b;
	
	@Expose
	@SerializedName("expwp")
	private RetItemDetailsDto expwp;
	
	@Expose
	@SerializedName("expwop")
	private RetItemDetailsDto expwop;
	
	@Expose
	@SerializedName("sezwp")
	private RetItemDetailsDto sezwp;
	
	@Expose
	@SerializedName("sezwop")
	private RetItemDetailsDto sezwop;
	
	@Expose
	@SerializedName("de")
	private RetItemDetailsDto de;
	
	@Expose
	@SerializedName("subtotal")
	private RetItemDetailsDto subtotal;
	
	@Expose
	@SerializedName("chksum")
	private String chksum;
}
