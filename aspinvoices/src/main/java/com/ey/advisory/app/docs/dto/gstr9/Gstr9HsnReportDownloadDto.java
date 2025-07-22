/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr9HsnReportDownloadDto {

	private String Gstin;

	private String Fy;

	private String TableNumber;

	private String Hsn;

	private String Description;

	private BigDecimal RateofTax = BigDecimal.ZERO;

	private String Uqc;
	
	private BigDecimal TotalQuantity = BigDecimal.ZERO;

	private BigDecimal TaxableValue = BigDecimal.ZERO;

	private String ConcessionalRateFlag;

	private BigDecimal Igst = BigDecimal.ZERO;

	private BigDecimal Cgst = BigDecimal.ZERO;

	private BigDecimal Sgst = BigDecimal.ZERO;

	private BigDecimal Cess = BigDecimal.ZERO;

}
