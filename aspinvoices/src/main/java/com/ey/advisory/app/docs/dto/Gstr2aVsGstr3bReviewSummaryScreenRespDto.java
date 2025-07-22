package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Gstr2aVsGstr3bReviewSummaryScreenRespDto {

    private String description;

    private String calFeild;

    private BigDecimal igst = BigDecimal.ZERO;

    private BigDecimal cgst = BigDecimal.ZERO;

    private BigDecimal sgst = BigDecimal.ZERO;

    private BigDecimal cess = BigDecimal.ZERO;
}
