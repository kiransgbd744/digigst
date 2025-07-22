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
public class ReconResultRepNameDetaUpdateDto {
	 
	@Expose
	private String reportName;
	
	@Expose
	private List<ReconResultUpdateComDetailsDto> CommonDetails;

}
