package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InnerDetailDto {
	
	@Expose
	private String taxPeriod;

	@Expose
	private LocalDateTime initiatedOn;

	@Expose
	private String reportStatus = "Not Initiated";
	
	@Expose
	private String filePath;
	
	@Expose
	private Boolean flag ;
	
	@Expose
	private String errorMsg ;
	
	@Expose
	private String docId;
	
	@Expose
	private boolean isReGenerate2b = true;

}
