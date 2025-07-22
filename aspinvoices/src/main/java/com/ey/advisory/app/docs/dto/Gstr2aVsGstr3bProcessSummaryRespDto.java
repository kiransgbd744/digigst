package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr2aVsGstr3bProcessSummaryRespDto {

    @Expose
    @SerializedName("gstin")
    private String gstin;

    @Expose
    @SerializedName("sectionName")
    private String sectionName;

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("timestamp")
    private String timestamp;

    @Expose
    @SerializedName("igst")
    private BigDecimal igst = BigDecimal.ZERO;

    @Expose
    @SerializedName("cgst")
    private BigDecimal cgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("sgst")
    private BigDecimal sgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("cess")
    private BigDecimal cess = BigDecimal.ZERO;

}
