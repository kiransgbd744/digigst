package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Gstr1VsGstr3bReviewSummaryScreenRespDto {

    private String supplies;

    private String formula;

    private BigDecimal taxableValue = BigDecimal.ZERO;

    private BigDecimal igst = BigDecimal.ZERO;

    private BigDecimal cgst = BigDecimal.ZERO;

    private BigDecimal sgst = BigDecimal.ZERO;

    private BigDecimal cess = BigDecimal.ZERO;

    private String gstin;

    private String taxPeriod;

    private List<Gstr1VsGstr3bReviewSummaryItemsRespDto> items = new ArrayList<Gstr1VsGstr3bReviewSummaryItemsRespDto>();

}
