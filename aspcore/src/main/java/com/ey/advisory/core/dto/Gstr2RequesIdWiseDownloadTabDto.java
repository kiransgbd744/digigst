package com.ey.advisory.core.dto;

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
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2RequesIdWiseDownloadTabDto {
	
	@Expose
	private String  reportName;
	
	@Expose
	private String path;
	
	@Expose
	private boolean flag;

	/*@Expose
	private String level = "L1";
*/
	@Expose
	private String level;
	
	@Expose
	private Integer order;
	
	@Expose
	private String message;
	
	
} 
