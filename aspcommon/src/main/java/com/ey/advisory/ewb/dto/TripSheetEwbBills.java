/**
 * 
 */
package com.ey.advisory.ewb.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class TripSheetEwbBills {
	
	@Expose
	@SerializedName("ewbNo")
	private String ewbNo;

}
