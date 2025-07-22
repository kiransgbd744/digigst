package com.ey.advisory.app.docs.dto.gstr2;

import java.util.List;

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
public class GSTR2a2bRefreshTaggingJobReqDto {
	
	@SerializedName("financialYear")
	@Expose
	private String financialYear;

	@SerializedName("gstins")
	@Expose
	private List<RefreshTaggingJobnfo> gstins;

}
