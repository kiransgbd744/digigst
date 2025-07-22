/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

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
public class Anx1ErrorDocsRevIntegrationReqDto {

	@Expose
	private String gstin;

	@Expose
	private Long erpId;

	@Expose
	private String groupcode;

	// This(destinationName) should be removed from here later in time.
	@Expose
	private String destinationName;

	@Expose
	private Long batchId;

	@Expose
	private String scenarioName;

}
