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
public class Gstr2bLinkingRespDto {

	private String taxPeriod;

	private String linked;

	private String notLinked;
	
	private String lastUpdatedOn;
	
	private String linkingStatus;

}
