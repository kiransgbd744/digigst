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
public class Gstr1SaveToGstnRetryReqDto {

	@Expose
	private Long batchId;

	@Expose
	private String refId;

}
