package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReversalAndReclaimDetailsDto {

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
	private String table4a5Igst = "-";

	@Expose
	private String table4a5Cgst = "-";

	@Expose
	private String table4a5Sgst = "-";

	@Expose
	private String table4a5Cess = "-";

	// ITC Reversal (Table 4B(2)) Eligible to Re-claim

	@Expose
	private String table4b2Igst = "-";

	@Expose
	private String table4b2Cgst = "-";

	@Expose
	private String table4b2Sgst = "-";

	@Expose
	private String table4b2Cess = "-";

	// ITC Reclaimed (Table 4D(1))

	@Expose
	private String table4d1Igst = "-";

	@Expose
	private String table4d1Cgst = "-";

	@Expose
	private String table4d1Sgst = "-";

	@Expose
	private String table4d1Cess = "-";

	// closing balence
	@Expose
	private String clsBalIgst = "-";

	@Expose
	private String clsBalCgst = "-";

	@Expose
	private String clsBalSgst = "-";

	@Expose
	private String clsBalCess = "-";
	
	

}
