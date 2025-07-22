
package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NegativeDetailsRespDto {

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
    @SerializedName(value = "tranTyp")
    private String tranTyp;

    @Expose
    @SerializedName(value = "gstin")
    private String gstin;
    
    @Expose
    @SerializedName(value = "trancd")
    private BigInteger trancd;

    @Expose
    private String amtCrDrOtherIgst = "0";

    @Expose
    private String amtCrDrOtherCgst = "0";

    @Expose
    private String amtCrDrOtherSgst = "0";

    @Expose
    private String amtCrDrOtherCess = "0";


    @Expose
    private String amtCrDrRevChargeIgst = "0";

    @Expose
    private String amtCrDrRevChargeCgst = "0";

    @Expose
    private String amtCrDrRevChargeSgst = "0";

    @Expose
    private String amtCrDrRevChargeCess = "0";

    // closing balance
    @Expose
    private String clsBalOtherIgst = "0";

    @Expose
    private String clsBalOtherCgst = "0";

    @Expose
    private String clsBalOtherSgst = "0";

    @Expose
    private String clsBalOtherCess = "0";

    @Expose
    private String clsBalRevChargeIgst = "0";

    @Expose
    private String clsBalRevChargeCgst = "0";

    @Expose
    private String clsBalRevChargeSgst = "0";

    @Expose
    private String clsBalRevChargeCess = "0";

	
	

}
