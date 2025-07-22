/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2b;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */
@Data
public class Gstr2bLinkingDto {

	private String gstin;

	private String stateName;
	
	private String authToken;
	
	private String taxPeriod;

	private String linked;

	private String notLinked;
	
	private String lastUpdatedOn;
	
	private String linkingStatus;

}
