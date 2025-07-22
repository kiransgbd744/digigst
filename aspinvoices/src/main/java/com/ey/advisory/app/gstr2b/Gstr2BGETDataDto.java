package com.ey.advisory.app.gstr2b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2BGETDataDto {
	
	@Expose
	@SerializedName("chksum")
	private String checksum;
	
	@Expose
	@SerializedName("data")
	private Gstr2BDetailedData detailedData;

	@Expose
	@SerializedName("itcRejected")
	private boolean itcRejected;

}
