package com.ey.advisory.app.docs.dto.gstr2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Akhilesh Yadav
 *
 */

@Setter
@Getter
public class GSTR2a2bLinkingStatusReqDto {

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("financialYear")
	private String financialYear;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs = new HashMap<>();

}
