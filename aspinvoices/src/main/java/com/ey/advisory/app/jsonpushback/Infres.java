
package com.ey.advisory.app.jsonpushback;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Infres implements Serializable
{

    @SerializedName("MessageType")
    @Expose
    private String messageType;
    @SerializedName("DealerID")
    @Expose
    private String dealerID;
    @SerializedName("GSTINID")
    @Expose
    private String gstinId;
    @SerializedName("FilingPeriod")
    @Expose
    private String filingPeriod;
    @SerializedName("AT")
    @Expose
    private String at;
    @SerializedName("MessageID")
    @Expose
    private String messageID;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("AcknowledgementNumber")
    @Expose
    private String acknowledgementNumber;
    @SerializedName("ResponseErrorFileGenerationTimestamp")
    @Expose
    private Timestamp responseErrorFileGenerationTimestamp;
    @SerializedName("BatchId")
    @Expose
    private String batchId;
    @SerializedName("InvoiceLevelResponse")
    @Expose
    private InvoiceLevelResponse invoiceLevelResponse;
    private final static long serialVersionUID = -7542065343417990721L;

}
