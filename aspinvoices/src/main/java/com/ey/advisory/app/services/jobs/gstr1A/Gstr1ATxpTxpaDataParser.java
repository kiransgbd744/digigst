package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ATxpHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ATxpaHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

public interface Gstr1ATxpTxpaDataParser {

	/*
	 * public List<GetGstr1TXPInvoicesEntity> gstr1TxpTxpDataParser(
	 * Gstr1GetInvoicesReqDto dto, String apiResp);
	 */

	public List<GetGstr1ATxpHeaderEntity> parseTxpData(Gstr1GetInvoicesReqDto dto,
			String apiResp);

	public List<GetGstr1ATxpaHeaderEntity> parseTxpaData(
			Gstr1GetInvoicesReqDto dto, String apiResp);
}
