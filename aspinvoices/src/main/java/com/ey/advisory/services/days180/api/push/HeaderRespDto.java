/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author vishal.verma
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderRespDto {
	
	@Expose
	private String status;
	
	@Expose
	private String payloadId;
	
	@Expose
	private String companyCode;
	
	@Expose
	private Integer TotalCount;
	
	@Expose
	private Integer ProcessCount;
	
	@Expose
	private Integer ErrorCount;
	

}
