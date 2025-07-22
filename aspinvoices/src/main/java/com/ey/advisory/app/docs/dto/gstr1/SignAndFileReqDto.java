/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr1;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hemasundar.J
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignAndFileReqDto {

	@Expose
	private String gstin;

	@Expose
	private String taxPeriod;
	
	@Expose
	private String pan;
	
	@Expose 
	private String returnType;
	
	//DRC01B
	@Expose 
	private String refId;

	@Expose 
	private String isNil;
}
