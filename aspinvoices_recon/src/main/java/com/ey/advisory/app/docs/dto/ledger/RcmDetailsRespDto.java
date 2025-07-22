package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RcmDetailsRespDto {

    @Expose
    @SerializedName(value = "srNo")
    private Integer srNo;

    @Expose
    @SerializedName(value = "trandt")
    private String date; 

    @Expose
    @SerializedName(value = "refNo")
    private String refNo; 

    @Expose
    @SerializedName(value = "rtnprd")
    private String taxPeriod; 

    @Expose
    @SerializedName(value = "desc")
    private String desc;  

    @Expose
    @SerializedName(value = "gstin")
    private String gstin; 

    @Expose
    private String table4a2Igst = "0";

    @Expose
    private String table4a2Cgst = "0";

    @Expose
    private String table4a2Sgst = "0";

    @Expose
    private String table4a2Cess = "0";

    @Expose
    private String table4a3Igst = "0";

    @Expose
    private String table4a3Cgst = "0";

    @Expose
    private String table4a3Sgst = "0";

    @Expose
    private String table4a3Cess = "0";

    @Expose
    private String table31dIgst = "0";

    @Expose
    private String table31dCgst = "0";

    @Expose
    private String table31dSgst = "0";

    @Expose
    private String table31dCess = "0";

    // Closing balance
    @Expose
    private String clsBalIgst = "0";

    @Expose
    private String clsBalCgst = "0";

    @Expose
    private String clsBalSgst = "0";

    @Expose
    private String clsBalCess = "0";

	

}
