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
public class GSTR3BRateDto {
	

	@Expose
	@SerializedName("start_dt")
	private String rateStartDate;
	
	@Expose
	@SerializedName("end_dt")
	private String rateEndDate;
	
	@Expose
	@SerializedName("rate")
	private String rate;
	
	@Expose
	@SerializedName("delay")
	private String delay;
	
	@Expose
	@SerializedName("interest")
	private GSTR3BTaxDto interesRate;

}
