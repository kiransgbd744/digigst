package com.ey.advisory.app.services.ledger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */
@Getter
@Setter
@ToString
public class GetSummaryLedgerBalanceDto {
	
	private String gstin;
	private String state;
	private String status;
	private BigDecimal cashigst_tot_bal = BigDecimal.ZERO;
	private BigDecimal cashcgst_tot_bal = BigDecimal.ZERO;
	private BigDecimal cashsgst_tot_bal = BigDecimal.ZERO;
	private BigDecimal cashcess_tot_bal = BigDecimal.ZERO;
	private BigDecimal itccgst_totbal = BigDecimal.ZERO;
	private BigDecimal itcsgst_totbal = BigDecimal.ZERO;
	private BigDecimal itcigst_totbal = BigDecimal.ZERO;
	private BigDecimal itccess_totbal = BigDecimal.ZERO;
	private BigDecimal libigst_totbal = BigDecimal.ZERO;
	private BigDecimal libcgst_totbal = BigDecimal.ZERO;
	private BigDecimal libsgst_totbal = BigDecimal.ZERO;
	private BigDecimal libcess_totbal = BigDecimal.ZERO;
	//CR reversal and reclaim
	private BigDecimal crRevigst_totbal = BigDecimal.ZERO;
	private BigDecimal crRevcgst_totbal = BigDecimal.ZERO;
	private BigDecimal crRevsgst_totbal = BigDecimal.ZERO;
	private BigDecimal crRevcess_totbal = BigDecimal.ZERO;
	private LocalDateTime lastupdated_date;
	
	private BigDecimal rcmIgst_totbal = BigDecimal.ZERO;
	private BigDecimal rcmCgst_totbal = BigDecimal.ZERO;
	private BigDecimal rcmSgst_totbal = BigDecimal.ZERO;
	private BigDecimal rcmCess_totbal = BigDecimal.ZERO;
	
	private BigDecimal negativeIgst_totbal = BigDecimal.ZERO;
	private BigDecimal negativeCgst_totbal = BigDecimal.ZERO;
	private BigDecimal negativeSgst_totbal = BigDecimal.ZERO;
	private BigDecimal negativeCess_totbal = BigDecimal.ZERO;
	
	//2 new columns
	private String getCallStatus;
	private String getCallStatusTimeStamp;
	

}

