package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.docs.dto.ledger.GetCashLedgerDetailsReqDto;

/**
 * s
 * @author Hemasundar.J
 *
 */
public interface CashLedgerDetailsDataAtGstn {

	public String fromGstn(GetCashLedgerDetailsReqDto dto);
	public String fromRcmGstn(GetCashLedgerDetailsReqDto dto);
	public String fromReclaimGstn(GetCashLedgerDetailsReqDto dto);
	public String fromGstnDetailedRcmDetails(GetCashLedgerDetailsReqDto dto);
	public String fromGstnDetailedNegativeDetails(GetCashLedgerDetailsReqDto dto);

	public String getCreditReversalAndReclaimfromGstn(GetCashLedgerDetailsReqDto dto);
	public String getCreditReversalAndReclaimfromGstnTest(GetCashLedgerDetailsReqDto dto);
	//tetsing
	String fromGstnDetailedRcmDetailsTest(GetCashLedgerDetailsReqDto dto);
	String fromGstnDetailedNegativeDetailsTest(GetCashLedgerDetailsReqDto dto);
	String fromGstnDetailedNegativeDetailsTest1();
	String fromGstnRcmGetDetailsTest();
	String fromRcmGstnTest(GetCashLedgerDetailsReqDto dto);
	String fromNegativeGstnTest(GetCashLedgerDetailsReqDto dto);
}
