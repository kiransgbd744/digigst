/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class GSTR3BChalanDto {
	
	@Expose
	@SerializedName("ret_period")
	private String retPeriodChalan;
	
	@Expose
	@SerializedName("pdcash")
	private GSTR3BTaxDto pdCashChalan;

}
