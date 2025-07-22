/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BItcReclaimParseDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("clsbal")
	private Gst3BCashBalanceDto clsbal;

}
