package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ReconSummaryDto {
    private Long prVendorGstinCount;
    private Integer prDocumentCount;
    private BigDecimal prTotalTax;
    private BigDecimal prIgst;
    private BigDecimal prCgst;
    private BigDecimal prSgst;
    private BigDecimal prCess;
    private BigDecimal prAvailableIgst;
    private BigDecimal prAvailableCgst;
    private BigDecimal prAvailableSgst;
    private BigDecimal prAvailableCess;
    private Long b2VendorGstinCount;
    private Integer b2DocumentCount;
    private BigDecimal b2TotalTax;
    private BigDecimal b2Igst;
    private BigDecimal b2Cgst;
    private BigDecimal b2Sgst;
    private BigDecimal b2Cess;
    private Long a2VendorGstinCount;
    private Integer a2DocumentCount;
    private BigDecimal a2TotalTax;
    private BigDecimal a2Igst;
    private BigDecimal a2Cgst;
    private BigDecimal a2Sgst;
    private BigDecimal a2Cess;
    private Integer cnt;
}
