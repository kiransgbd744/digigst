package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class GSTR2aAutoReconRevIntgReqDto {

	private String gstin;

	private Long scenarioId;
	
	private Long autoReconId;

	private Long configId;

	private String scenarioName;

	private String destinationName;

	private Long erpId;

	private String groupCode;
	
	private String destinationType;

    private String retType;
}
