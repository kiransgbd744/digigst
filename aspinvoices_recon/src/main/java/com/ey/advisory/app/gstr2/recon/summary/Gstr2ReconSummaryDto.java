package com.ey.advisory.app.gstr2.recon.summary;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2ReconSummaryDto {
	
	@Expose
	private String perticulas;
	
	@Expose
	private String perticulasName;
	
	@Expose 
	private Long prCount = 0L;
	
	@Expose
	private BigDecimal prPercenatge = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prTaxableValue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prTotalTax = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prCess = BigDecimal.ZERO;
	
	
	@Expose 
	private Long a2Count = 0L;
	
	@Expose
	private BigDecimal a2Percenatge = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2TaxableValue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2TotalTax = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2Igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2Cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2Sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal a2Cess = BigDecimal.ZERO;

	
	@Expose
	private String level = "L2";
	
	@Expose
	private String orderPosition;
	
	
}
