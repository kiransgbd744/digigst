/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class CrossItcVerticalDto {

	private String isdGstin;
	private String taxPeriod;
	private String igstUsedAsIgst;
	private String sgstUsedAsIgst;
	private String cgstUsedAsIgst;
	private String sgstUsedAsSgst;
	private String igstUsedAsSgst;
	private String cgstUsedAsCgst;
	private String igstUsedAsCgst;
	private String cessUsedAsCess;
	private String aspInformationID;
	private String aspInformationDescription;
	private String aspErrorCode;
	private String aspErrorDescription;
}
