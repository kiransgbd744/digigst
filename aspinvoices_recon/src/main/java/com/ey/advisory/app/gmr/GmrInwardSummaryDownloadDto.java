package com.ey.advisory.app.gmr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GmrInwardSummaryDownloadDto {
	
	private String serialNumber;
	private String inwardSupplyDetails;
	private String gross_taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
}
