package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;

public interface LedgerBalanceParser {

	GetSummarizedLedgerBalanceEntity parseLedgerBalanceData(String gstin,
			String taxPeriod, String apiResp, String apiLibResp, Long jobId,String apiCrRevAndCrReclaimResp
			,String apiRcmDetailsResp,String apiNegativeDetailsResp);
}
