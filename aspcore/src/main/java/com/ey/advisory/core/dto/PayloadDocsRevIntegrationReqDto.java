/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Umesh
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class PayloadDocsRevIntegrationReqDto {
	
	/*@Expose
	private String gstin;*/
	
	/*@Expose
	private Long entityId;
	
	@Expose
	private Long scenarioId;*/
	
	@Expose
	private String groupcode;

	//This(destinationName) should be removed from here later in time.
	/*@Expose
	private String destinationName;*/
	
	@Expose 
	private String payloadId;
	
	@Expose
	private String scenarioName;
	
	@Expose
	private String sourceId;

	@Expose
	private Long jobId;
	
	@Expose
	private Long id;

}
