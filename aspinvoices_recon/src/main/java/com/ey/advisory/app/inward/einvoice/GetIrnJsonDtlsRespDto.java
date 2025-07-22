package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetIrnJsonDtlsRespDto {@Expose
    @SerializedName("AckNo")
    private Long ackNo;

    @Expose
    @SerializedName("AckDt")
    private String ackDt;

    @Expose
    @SerializedName("Irn")
    private String irn;

    @Expose
    @SerializedName("SignedInvoice")
    private String signedInvoice;

    @Expose
    @SerializedName("SignedQRCode")
    private String signedQRCode;

    @Expose
    @SerializedName("Status")
    private String status;

    @Expose
    @SerializedName("EwbNo")
    private Long ewbNo;

    @Expose
    @SerializedName("EwbDt")
    private String ewbDt;

    @Expose
    @SerializedName("EwbValidTill")
    private String ewbValidTill;

    @Expose
    @SerializedName("Remarks")
    private String remarks;

    @Expose
    @SerializedName("Cnldt")
    private String cnldt;

    @Expose
    @SerializedName("CnlRsn")
    private String cnlRsn;

    @Expose
    @SerializedName("CnlRem")
    private String cnlRem;
    
}
