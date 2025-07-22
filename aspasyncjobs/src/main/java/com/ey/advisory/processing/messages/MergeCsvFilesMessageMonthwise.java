package com.ey.advisory.processing.messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MergeCsvFilesMessageMonthwise {

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("TaxPeriod")
	private String taxPeriod;
	
	public MergeCsvFilesMessageMonthwise(String taxPeriod, 
			String returnType, String gstin) {
		this.returnType = returnType;
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
	}

	


	

	
}


