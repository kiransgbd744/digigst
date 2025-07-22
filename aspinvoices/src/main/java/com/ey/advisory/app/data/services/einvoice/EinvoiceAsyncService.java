/**
 * 
 */
package com.ey.advisory.app.data.services.einvoice;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;

/**
 * @author Khalid1.Khan
 *
 */
public interface EinvoiceAsyncService {

	String generateIrn(Long id, String isEWBReq);

	String generateIrnAsync(List<Long> docId, String isEWBReq);

	public String cancelEInvSync(CancelIrnReqDto canelEInvReqDto);

	public String cancelEInvASync(CancelIrnReqList canelEInvReqList);

	public String generateEWBByIrn(Long id);

	public GenerateEWBByIrnNICReqDto convertOutWardtoGenEWBIrn(
			OutwardTransDocument ot);

}
