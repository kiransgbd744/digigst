/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Nikhil.Duseja
 *
 */
@Getter
@Setter
@ToString
public class ReconResponseReportTypeDto {
	
	
	ReconResponseReportTypeDto(String reportType) {
		this.reportType = reportType;
	}
	
	@Expose
	String reportType;

}
