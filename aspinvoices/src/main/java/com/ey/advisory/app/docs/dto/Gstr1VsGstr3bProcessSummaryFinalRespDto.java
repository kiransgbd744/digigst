package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1VsGstr3bProcessSummaryFinalRespDto {

    @Expose
    @SerializedName("gstin")
    private String gstin;

    @Expose
    @SerializedName("authToken")
    private String authToken;

    @Expose
    @SerializedName("state")
    private String state;

    @Expose
    @SerializedName("reconStatus")
    private String reconStatus;

    @Expose
    @SerializedName("reconDateTime")
    private String reconDateTime;

    @Expose
    @SerializedName("gstr3bStatus")
    private String gstr3bStatus;

    @Expose
    @SerializedName("gstr3bTime")
    private String gstr3bTime;

    @Expose
    @SerializedName("gstr3bTaxableValue")
    private BigDecimal gstr3bTaxableValue = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr3bTotalTax")
    private BigDecimal gstr3bTotalTax = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr1TaxableValue")
    private BigDecimal gstr1TaxableValue = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr1TotalTax")
    private BigDecimal gstr1TotalTax = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffTaxableValue")
    private BigDecimal diffTaxableValue = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffTotalTax")
    private BigDecimal diffTotalTax = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr1Status")
    private String gstr1Status;

    @Expose
    @SerializedName("gstr1Time")
    private String gstr1Time;
    
    @Expose
    @SerializedName("gstr1aStatus")
    private String gstr1aStatus;

    @Expose
    @SerializedName("gstr1aTime")
    private String gstr1aTime;


}
