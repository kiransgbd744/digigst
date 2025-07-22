package com.ey.advisory.app.data.services.customisedreport;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
public class CustomisedReportListDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("field")
	private String field;

	@Expose
	@SerializedName("sequence")
	private int sequence;

	@Expose
	@SerializedName("visible")
	private boolean visible;


}
