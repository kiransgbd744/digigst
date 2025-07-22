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
public class ItcCrReversalAndReclaimDto {

    @Expose
    @SerializedName(value = "srNo")
    private Integer srNo;

    @Expose
    @SerializedName(value = "itcTransDate")
    private String itcTransDate;

    @Expose
    @SerializedName(value = "refNo")
    private String refNo;

    @Expose
    @SerializedName(value = "taxPeriod")
    private String taxPeriod;

    @Expose
    @SerializedName(value = "desc")
    private String desc;

    @Expose
    @SerializedName(value = "gstin")
    private String gstin;

	// ITC Claimed (Table 4A(5)) (All Other ITC)

    @Expose
    private String table4a5Igst = "0";

    @Expose
    private String table4a5Cgst = "0";

    @Expose
    private String table4a5Sgst = "0";

    @Expose
    private String table4a5Cess = "0";

    // ITC Reversal (Table 4B(2)) Eligible to Re-claim

    @Expose
    private String table4b2Igst = "0";

    @Expose
    private String table4b2Cgst = "0";

    @Expose
    private String table4b2Sgst = "0";

    @Expose
    private String table4b2Cess = "0";

    // ITC Reclaimed (Table 4D(1))

    @Expose
    private String table4d1Igst = "0";

    @Expose
    private String table4d1Cgst = "0";

    @Expose
    private String table4d1Sgst = "0";

    @Expose
    private String table4d1Cess = "0";

    // closing balance
    @Expose
    private String clsBalIgst = "0";

    @Expose
    private String clsBalCgst = "0";

    @Expose
    private String clsBalSgst = "0";

    @Expose
    private String clsBalCess = "0";

	
	

}
