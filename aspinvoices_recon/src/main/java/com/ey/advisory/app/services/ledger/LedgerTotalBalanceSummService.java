package com.ey.advisory.app.services.ledger;

import com.ey.advisory.app.data.entities.client.asprecon.LedgerSaveToGstnRcmEntity;
import com.ey.advisory.app.docs.dto.ledger.LedgerBalanceJobTriggerDto;

public interface LedgerTotalBalanceSummService {

	public void getLedgerTotalBalance(
			LedgerBalanceJobTriggerDto reqDto);
	
	public void saveToGstnRcmLedger(
			LedgerSaveToGstnRcmEntity reqDto,Long id);
}
