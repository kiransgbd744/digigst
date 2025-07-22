/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GlTaxCodeMasterDto {
	 private Long id;
    private String transactionTypeGl;
    private String taxCodeDescriptionMs;
    private String taxTypeMs;
    private String eligibilityMs;
    private BigDecimal taxRateMs;
    private String errorCode;
    private String errorDescription;
}

