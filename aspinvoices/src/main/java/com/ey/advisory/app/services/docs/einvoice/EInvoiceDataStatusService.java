package com.ey.advisory.app.services.docs.einvoice;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.EInvoiceDataStatusDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;

/**
 * 
 * @author Umesha.M
 *
 *         This Interface Represent get E Invoice Data Status values to Data
 *         Status Screen
 */
public interface EInvoiceDataStatusService {

	public List<EInvoiceDataStatusDto> getDataStatusForEInvoice(
			final DataStatusSearchReqDto reqDto);

}
