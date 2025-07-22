package com.ey.advisory.app.docs.dto.itc04;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Itc04Dto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String taxperiod;

	@Expose
	@SerializedName("m2jw")
	private List<Itc04M2jwDto> m2jw;

	@Expose
	@SerializedName("table5A")
	private List<Itc04Table5ADto> table5a;

	@Expose
	@SerializedName("table5B")
	private List<Itc04Table5BDto> table5b;

	@Expose
	@SerializedName("table5C")
	private List<Itc04Table5CDto> table5c;

	@Expose
	@SerializedName("error_report")
	private Itc04ErrorReport itc04ErrorReport;

}
