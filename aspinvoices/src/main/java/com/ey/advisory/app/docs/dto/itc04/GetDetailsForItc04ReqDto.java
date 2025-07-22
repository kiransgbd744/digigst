package com.ey.advisory.app.docs.dto.itc04;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Saif.S
 *
 */
@Data
public class GetDetailsForItc04ReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String fp;

	@Expose
	@SerializedName("m2jw")
	private List<Itc04M2jwDto> m2jwDto;

	@Expose
	@SerializedName("table5A")
	private List<Itc04Table5ADto> table5ADto;

	@Expose
	@SerializedName("table5B")
	private List<Itc04Table5BDto> table5BDto;

	@Expose
	@SerializedName("table5C")
	private List<Itc04Table5CDto> table5CDto;
}
