package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2aVsGstr3bReviewSummaryRespDto {

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("calFeild")
    private String calFeild;

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
