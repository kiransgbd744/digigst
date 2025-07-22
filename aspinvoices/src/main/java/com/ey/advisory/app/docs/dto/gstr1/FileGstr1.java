/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr1;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class FileGstr1 {

	@Expose
	private String st;
	
	@Expose
	private String data;
	
	@Expose
	private String sign;
	
	@Expose
	private String action;
	
	@Expose
	private String sid;
	
}
