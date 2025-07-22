
package com.ey.advisory.app.docs.dto.anx1;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Itc04GetSummaryDto {

	@Expose
	@SerializedName("m2j_chksum")
	private String m2jChksum;

	@Expose
	@SerializedName("table5A_chksum")
	private String table5AChksum;

	@Expose
	@SerializedName("table5B_chksum")
	private String table5BChksum;

	@Expose
	@SerializedName("table5C_chksum")
	private String table5cChksum;
	
	@Expose
	@SerializedName("table5B_no")
	private String table5bNo;
	
	@Expose
	@SerializedName("table5C_no")
	private String table5cNo;
	
	
	@Expose
	@SerializedName("m2jNoOfChallanRec")
	private String m2jNoOfChallanRec;
	
	@Expose
	@SerializedName("table5A_no")
	private String table5aNo;
	
	@Expose
	@SerializedName("m2j_ttl_tax")
	private String m2j_ttl_tax;
	

}