package com.ey.advisory.app.data.services.compliancerating;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class VendorDueDateDTO {

	private String taxPeriod;
	private String returnType;
	private String dueDate;
	private String vendorStateCode;
	private String returnFilingFrequency;

}
