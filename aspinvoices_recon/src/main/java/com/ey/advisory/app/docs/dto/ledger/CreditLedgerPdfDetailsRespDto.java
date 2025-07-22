package com.ey.advisory.app.docs.dto.ledger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreditLedgerPdfDetailsRespDto {
	
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
	@SerializedName(value = "transType")
	private String transType;
	
/*	@Expose
	@SerializedName(value = "crDrIgst")
	private BigDecimal crDrIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "crDrCgst")
	private BigDecimal crDrCgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "crDrSgst")
	private BigDecimal crDrSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "crDrCess")
	private BigDecimal crDrCess = BigDecimal.ZERO;;
	
	@Expose
	@SerializedName(value = "crDrTotal")
	private BigDecimal crDrTotal = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "balIgst")
	private BigDecimal balIgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "balCgst")
	private BigDecimal balCgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "balSgst")
	private BigDecimal balSgst = BigDecimal.ZERO;
	
	@Expose
	@SerializedName(value = "balCess")
	private BigDecimal balCess = BigDecimal.ZERO;;
	
	@Expose
	@SerializedName(value = "balTotal")
	private BigDecimal balTotal = BigDecimal.ZERO;
	*/
	@Expose
	@SerializedName(value = "crDrIgst")
	private String crDrIgst = "0";

	@Expose
	@SerializedName(value = "crDrCgst")
	private String crDrCgst = "0";

	@Expose
	@SerializedName(value = "crDrSgst")
	private String crDrSgst = "0";

	@Expose
	@SerializedName(value = "crDrCess")
	private String crDrCess = "0";

	@Expose
	@SerializedName(value = "crDrTotal")
	private String crDrTotal = "0";

	@Expose
	@SerializedName(value = "balIgst")
	private String balIgst = "0";

	@Expose
	@SerializedName(value = "balCgst")
	private String balCgst = "0";

	@Expose
	@SerializedName(value = "balSgst")
	private String balSgst = "0";

	@Expose
	@SerializedName(value = "balCess")
	private String balCess = "0";

	@Expose
	@SerializedName(value = "balTotal")
	private String balTotal = "0";
	@Expose
	@SerializedName(value = "gstn")
	private String gstn;
	
}
