package com.ey.advisory.app.inward.einvoice;

import org.springframework.stereotype.Component;

/**
 * @author vishal.verma
 *
 */
@Component("InwardEInvoiceRevIntgService")
public interface InwardEInvoiceRevIntgService {

	public Integer getInwardEInvoiceErpPush(InwardEInvoiceRevIntgReqDto dto);
}
