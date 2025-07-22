/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr1;

import java.util.Set;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
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
public class Gstr1SaveToGstnDependencyRetryReqDto {

	@Expose
	private Set<String> errorCodes;
	
	@Expose
	private String section;
	
	@Expose
	private Long batchId;
	
	@Expose
	private Long retryCount;
	
	@Expose
	private Long userRequestId;
	
	@Expose
	private SaveToGstnOprtnType operationType;
	
	@Expose
	private String returnType;
}
