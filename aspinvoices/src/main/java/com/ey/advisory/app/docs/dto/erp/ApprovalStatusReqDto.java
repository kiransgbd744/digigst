/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hemasundar.J
 *
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ApprovalStatusReqDto {

	@Expose
	private Long id;
	
	@Expose
	private String returnType;
	
	@Expose
	private String gstin;

	@Expose
	private String returnPeriod;

	@Expose
	private Long entityId;

	@Expose
	private Long scenarioId;

	@Expose
	private String groupcode;

	@Expose
	protected String destinationName;
	
	@Expose
	private List<String> gstins;

	@Expose
	private Map<Long,String> gstinIds;
}
