package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class NilSupplies {

	@Expose
	@SerializedName("flag")
	private String flag;
	
	@Expose
	@SerializedName("chksum")
	private String checkSum;
	
	@Expose
	@SerializedName("inv")
	private List<NilInvoices> nilInbvoices;
	
}
