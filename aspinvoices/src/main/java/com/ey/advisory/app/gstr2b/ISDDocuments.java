package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ISDDocuments {
	
	@Expose
	@SerializedName("ctin")
	private String suppGstin;
	
	@Expose
	@SerializedName("trdnm")
	private String suppName;
	
	@Expose
	@SerializedName("supfildt")
	private String gstr1FilingDate;
	
	@Expose
	@SerializedName("supprd")
	private String gstr1FilingPeriod;
	
	@Expose
	@SerializedName("doclist")
	private List<ISDDocDetails> isdDocDetails;


}
