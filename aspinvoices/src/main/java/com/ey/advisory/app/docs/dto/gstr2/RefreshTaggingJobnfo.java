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
public class RefreshTaggingJobnfo {

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("taxPeriod")
	@Expose
	private List<String> taxPeriod;
}
