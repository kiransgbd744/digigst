package com.ey.advisory.app.docs.dto.ledger;

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
public class CreditDetailsRespDto {
	
	@Expose
	private Integer srNo;
	
	@Expose
	private String gstn;
	
	@Expose
	private String itcTransDate="-";;
	
	
	@Expose
	private String refNo="-";;
	
	@Expose
	private String taxPeriod="-";;
	
	@Expose
	private String desc="-";;
	
	@Expose
	private String transType="-";;
	
	@Expose
	private String crDrIgst = "0";

	@Expose
	private String crDrCgst = "0";

	@Expose
	private String crDrSgst = "0";

	@Expose
	private String crDrCess = "0";

	@Expose
	private String crDrTotal = "0";

	@Expose
	private String balIgst = "0";

	@Expose
	private String balCgst = "0";

	@Expose
	private String balSgst = "0";

	@Expose
	private String balCess = "0";

	@Expose
	private String balTotal = "0";

	

	}
