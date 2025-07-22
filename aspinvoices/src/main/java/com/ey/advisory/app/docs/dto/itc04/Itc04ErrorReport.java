package com.ey.advisory.app.docs.dto.itc04;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author SriBhavya
 *
 */
@Data
public class Itc04ErrorReport {

	@Expose
	@SerializedName("ITC04")
	private Itc04ErrorReportData ITC04;
	
}
