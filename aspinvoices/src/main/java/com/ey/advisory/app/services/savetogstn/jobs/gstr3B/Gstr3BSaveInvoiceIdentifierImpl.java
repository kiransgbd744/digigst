package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

@Service("Gstr3BSaveInvoiceIdentifierImpl")
public class Gstr3BSaveInvoiceIdentifierImpl
		implements Gstr3BSaveInvoiceIdentifier {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveInvoiceIdentifierImpl.class);

	@Autowired
	@Qualifier("saveGstr3BDataFetcherImpl")
	private Gstr3BSaveDataFetcher saveGstr3BData;

	@Autowired
	@Qualifier("gstr3BRequestDtoConverterImpl")
	private Gstr3BRequestDtoConverterImpl gstr3BConverter;

	@Autowired
	@Qualifier("Gstr3BSaveApiCallHandlerImpl")
	private Gstr3BApiCallHandler gstr3BApiCallHandler;

	@Override
	public void findSaveInvoices(String jsonReq, String groupCode,
			String section) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Executing {} findSaveInvoices method with args {}{}",
					section, jsonReq, groupCode);
		/**
		 * Get all the newly processed plus not sent to gstn documents that are
		 * present in the user selected dates.
		 */
		try {
			List<Object[]> docs = saveGstr3BData.findInvoiceLevelData(jsonReq,
					groupCode);
			// This does the actual Gstr3B SaveToGstn operation for a given
			// section
			// by forming the Json structure as per government published API.
			if (docs != null && !docs.isEmpty()) {
				if (APIConstants.GSTR3B.equals(section)) {
					Gstr3BSavetoGstnDTO batchDto = gstr3BConverter
							.convertToGstr3BObject(docs, section, groupCode);
					gstr3BApiCallHandler.execute(batchDto, groupCode);
				}

			} else {
				String msg = "Zero Docs found to do {} Save to Gstn with args {}";
				LOGGER.warn(msg, section, jsonReq);
			}
		} catch (Exception ex) {
			String msg = "Exception while initiating the 3B Save";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

}
