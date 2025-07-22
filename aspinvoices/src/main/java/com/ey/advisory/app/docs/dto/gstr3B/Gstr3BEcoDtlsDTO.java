package com.ey.advisory.app.docs.dto.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Vishal.Verma
 *
 */

@Data
public class Gstr3BEcoDtlsDTO {

	@Expose
	@SerializedName("eco_sup")
	private Gstr3BSecDetailsDTO ecoSup;
	
	@Expose
	@SerializedName("eco_reg_sup")
	private Gstr3BSecDetailsDTO ecoRegSup;
	
	
	
}
