/**
 * 
 */
package com.ey.advisory.service.hsn.summary;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant.Shukla
 *
 */

@Getter
@Setter
public class HsnSummaryReportDto {

	private String serialNo;
	private String hsnCode;
	private String hsnDescription;
	private String gstRate;
	private String quantity;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String totalValue;
}
