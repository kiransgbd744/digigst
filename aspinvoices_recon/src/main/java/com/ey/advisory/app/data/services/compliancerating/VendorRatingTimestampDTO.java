package com.ey.advisory.app.data.services.compliancerating;

import lombok.Data;

/**
 * 
 * @author Jithendra.B
 *
 */
@Data
public class VendorRatingTimestampDTO {

	String uploadTime;
	String uploadStatus;
	String retFilingTime;
	String retFilingStatus;
	String retFrequencyStatus;
	String retFrequencyTime;

}
