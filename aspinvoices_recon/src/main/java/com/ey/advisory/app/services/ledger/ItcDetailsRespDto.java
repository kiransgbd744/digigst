package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItcDetailsRespDto {
	
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
