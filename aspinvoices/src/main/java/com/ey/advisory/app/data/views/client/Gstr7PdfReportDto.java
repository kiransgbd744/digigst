/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import lombok.Data;

/**
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr7PdfReportDto {

private String noofRecords;
private String taxableValue;
private String totalIntigratedValue;
private String totalCentralTax;
private String totalStateTax;

	
	
}
