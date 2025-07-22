package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr2aVsGstr3bProcessSummaryFinalRespDto {

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
    @SerializedName("regType")
    private String regType;

    @Expose
    @SerializedName("reconStatus")
    private String reconStatus;

    @Expose
    @SerializedName("reconTimestamp")
    private String reconTimestamp;

    @Expose
    @SerializedName("getGstr3BStatus")
    private String getGstr3BStatus;

    @Expose
    @SerializedName("getGstr3BTimestamp")
    private String getGstr3BTimestamp;

    @Expose
    @SerializedName("gstr3BIgst")
    private BigDecimal gstr3BIgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr3BCgst")
    private BigDecimal gstr3BCgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr3BSgst")
    private BigDecimal gstr3BSgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr3BCess")
    private BigDecimal gstr3BCess = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr2AIgst")
    private BigDecimal gstr2AIgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr2ACgst")
    private BigDecimal gstr2ACgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr2ASgst")
    private BigDecimal gstr2ASgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr2ACess")
    private BigDecimal gstr2ACess = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffIgst")
    private BigDecimal diffIgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffCgst")
    private BigDecimal diffCgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffSgst")
    private BigDecimal diffSgst = BigDecimal.ZERO;

    @Expose
    @SerializedName("diffCess")
    private BigDecimal diffCess = BigDecimal.ZERO;

    @Expose
    @SerializedName("gstr2aStatus")
    private String gstr2aStatus;

    @Expose
    @SerializedName("gstr2aTimestamp")
    private String gstr2aTimestamp;

}
