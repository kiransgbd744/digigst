/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Gstr6SaveCrossITCDto {

	@Expose
	private String gstin;

	@Expose
	private String fp;
	
	@Expose
	private Gstr6IsdItcCrossDto isdItcCross;
}
