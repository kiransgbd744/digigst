/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class RevIntegrationScenarioTriggerDto {

	@Expose
	private String gstin;

	@Expose
	private Long entityId;

	@Expose
	private Long scenarioId;

	@Expose
	private String groupcode;

	@Expose
	protected String destinationName;

	@Expose
	protected Long erpId;

	@Expose
	protected String finYear;

	@Expose
	private String section;
	
	@Expose
	private String scenarioName;
	
	@Expose
	private String sourceId;
	
	//for 3B
	@Expose
	private String source;

}
