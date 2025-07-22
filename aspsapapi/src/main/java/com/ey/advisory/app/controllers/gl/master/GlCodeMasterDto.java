/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GlCodeMasterDto {
	private Long id;
    private BigDecimal cgstTaxGlCode;
    private BigDecimal sgstTaxGlCode;
    private BigDecimal igstTaxGlCode;
    private BigDecimal ugstTaxGlCode;
    private BigDecimal compensationCessGlCode;
    private BigDecimal keralaCessGlCode;
    private BigDecimal revenueGls;
    private BigDecimal expenceGls;
    private BigDecimal exchangeRate;
   // private BigDecimal diffGl;
    private BigDecimal exportGl;
    private BigDecimal forexGlsPor;
    private BigDecimal taxableAdvanceLiabilityGls;
    private BigDecimal nonTaxableAdvanceLiabilityGls;
    private BigDecimal ccAndStGls;
    private BigDecimal unbilledRevenueGls;
    private BigDecimal bankAccGls;
    private BigDecimal inputTaxGls;
    private BigDecimal fixedAssetGls;
    
    private String gstin;
    private String errorCode;
    private String errorDescription;
}

