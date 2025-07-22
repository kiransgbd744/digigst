/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ITCReversal180DownloadTabDto {
	
	@Expose
	private String  reportName;
	
	@Expose
	private String path;
	
	@Expose
	private Boolean flag;

}
