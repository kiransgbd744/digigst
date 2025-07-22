/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
public class CgstinSgstinDto {
	
	public CgstinSgstinDto() {}
	
	public CgstinSgstinDto(String cgstin, String sgstin, String cpan) {
		this.cgstin = cgstin;
		this.sGstin = sgstin;
		this.cpan = cpan;
	}

	@Expose
	private String cgstin;
	
	@Expose
	private String sGstin;
	
	@Expose
	private String cpan;

	@Override
	public String toString() {
		return "CgstinSgstinDto [cgstin=" + cgstin + ", sGstin=" + sGstin
				+ ", cpan=" + cpan + "]";
	}
	
	
	

}
