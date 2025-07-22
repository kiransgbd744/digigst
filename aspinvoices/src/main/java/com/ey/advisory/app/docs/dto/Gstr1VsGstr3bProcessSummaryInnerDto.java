package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author vishl.verma
 *
 */
@Data
public class Gstr1VsGstr3bProcessSummaryInnerDto {

    private String gstin;

    private BigDecimal diffTaxableValue = BigDecimal.ZERO;

    private BigDecimal diffTotalTax = BigDecimal.ZERO;

   
}
