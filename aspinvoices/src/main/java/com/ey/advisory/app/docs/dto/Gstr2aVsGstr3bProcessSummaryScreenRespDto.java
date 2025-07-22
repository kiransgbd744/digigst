package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Gstr2aVsGstr3bProcessSummaryScreenRespDto {

    private String gstin;

    private BigDecimal gstr3BIgst;

    private BigDecimal gstr3BCgst;

    private BigDecimal gstr3BSgst;

    private BigDecimal gstr3BCess;

    private BigDecimal gstr2AIgst;

    private BigDecimal gstr2ACgst;

    private BigDecimal gstr2ASgst;

    private BigDecimal gstr2ACess;

    private BigDecimal diffIgst;

    private BigDecimal diffCgst;

    private BigDecimal diffSgst;

    private BigDecimal diffCess;

}
