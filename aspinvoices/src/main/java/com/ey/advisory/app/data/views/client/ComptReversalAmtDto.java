/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComptReversalAmtDto {

	private String particulars;
	private String subsectionName;
	private String gstin;
	private String totalTax;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

}
