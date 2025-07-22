package com.ey.advisory.app.services.ledger;

import java.util.List;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;

public interface FindLedgerSummaryAtGstin {
    
	public List<GetSummarizedLedgerBalanceEntity> 
				findLedgerSummAtGstinLevl(LedgerBalanceJobTriggerDto reqDto);
}
