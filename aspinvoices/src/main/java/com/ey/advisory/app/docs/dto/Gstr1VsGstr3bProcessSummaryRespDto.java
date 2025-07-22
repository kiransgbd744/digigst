package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Gstr1VsGstr3bProcessSummaryRespDto {

    private String gstin;

    private String authToken;

    private String state;

    private String reconStatus;

    private String reconDateTime;

    private String gstr3bStatus;

    private String gstr3bTime;

    private BigDecimal gstr3bTaxableValue = BigDecimal.ZERO;

    private BigDecimal gstr3bTotalTax = BigDecimal.ZERO;

    private BigDecimal gstr1TaxableValue = BigDecimal.ZERO;

    private BigDecimal gstr1TotalTax = BigDecimal.ZERO;

    private int derviedRetPeriod;

    private String gstr1Status;

    private String gstr1Time;
    
    //gstr1a
    private String gstr1aStatus;

    private String gstr1aTime;
    
    private BigDecimal diffTaxableValue = BigDecimal.ZERO;

    private BigDecimal diffTotalTax = BigDecimal.ZERO;

}
