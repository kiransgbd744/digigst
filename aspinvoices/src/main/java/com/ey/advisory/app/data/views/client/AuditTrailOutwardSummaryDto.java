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
public class AuditTrailOutwardSummaryDto {

	private String supGSTIN;
	private String docType;
	private String docNumber;
	private String docDate;
	private String proFrqcy;
	private String proDateTime;
	private String userID;
	private String proSource;
	private String proStatus;
	private String whetherCan;
	private String custGSTIN;
	private String noOflineitems;
	private String totalTaxVal;
	private String totalTax;
	private String invValue;
}
