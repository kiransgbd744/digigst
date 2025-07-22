package com.ey.advisory.filter;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class X5CCeritificateDto {

	@Expose
	@SerializedName("kty")
	private String kty;

	@Expose
	@SerializedName("use")
	private String use;

	@Expose
	@SerializedName("kid")
	private String kid;

	@Expose
	@SerializedName("x5t")
	private String x5t;

	@Expose
	@SerializedName("n")
	private String n;

	@Expose
	@SerializedName("e")
	private String e;

	@Expose
	@SerializedName("x5c")
	private List<String> x5c;

}
