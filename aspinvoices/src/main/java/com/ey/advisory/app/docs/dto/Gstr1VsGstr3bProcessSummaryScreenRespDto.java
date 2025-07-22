package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Gstr1VsGstr3bProcessSummaryScreenRespDto {

    private String gstin;
    private BigDecimal gstr3bTaxableValue = BigDecimal.ZERO;
    private BigDecimal gstr3bTotalTax = BigDecimal.ZERO;
    private BigDecimal gstr1TaxableValue = BigDecimal.ZERO;
    private BigDecimal gstr1TotalTax = BigDecimal.ZERO;
    private BigDecimal diffTaxableValue = BigDecimal.ZERO;
    private BigDecimal diffTotalTax = BigDecimal.ZERO;

}
