/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

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
public class ReconResultDocSummaryReqDto {
	
	
	@Expose
	private Long entityId;
	
	@Expose
	private String taxPeriod;
	
	@Expose
	private List<String> gstin;
	
	@Expose
	private List<String> tableType;
	
	@Expose
	private List<String> reportType;
	
	
	@Expose
	private List<String> docType;
	
}
