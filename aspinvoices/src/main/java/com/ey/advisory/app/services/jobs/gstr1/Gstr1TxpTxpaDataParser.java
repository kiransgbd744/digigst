package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1TxpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1TxpaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1TxpTxpaDataParser {

	/*
	 * public List<GetGstr1TXPInvoicesEntity> gstr1TxpTxpDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1TxpHeaderEntity> parseTxpData(Gstr1GetInvoicesReqDto dto,
			String apiResp);

	public List<GetGstr1TxpaHeaderEntity> parseTxpaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
