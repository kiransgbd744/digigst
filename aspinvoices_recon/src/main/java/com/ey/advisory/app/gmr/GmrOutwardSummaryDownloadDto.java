package com.ey.advisory.app.gmr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GmrOutwardSummaryDownloadDto {
	
	private String serialNumber;
	private String outwardSupplyDetails;
	private String hsn;
	private String taxRate;
	private String gross_taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
}
