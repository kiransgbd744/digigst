package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;

/**
 * 
 * @author Anand3.M
 *
 */
public interface Gstr1EInvoiceDataParser {

	public List<GetGstr1EInvoicesB2bHeaderEntity> parseEInvoiceB2bData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1EInvoicesCdnrHeaderEntity> parseEInvoiceCdnrData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1EInvoicesCdnurHeaderEntity> parseEInvoiceCdnurData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

	public List<GetGstr1EInvoicesExpHeaderEntity> parseEInvoiceExpData(
			Gstr1GetInvoicesReqDto dto, String apiResp);

}
