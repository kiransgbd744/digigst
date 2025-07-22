/**
 * 
 */
package com.ey.advisory.processors.test;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.services.einvoice.EinvoiceAsyncService;
import com.ey.advisory.app.data.services.einvoice.GenerateEWBByIrnService;
import com.ey.advisory.app.data.services.ewb.EwbService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Service("GenerateEwayBillProcessor")
public class GenerateEwayBillProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EwbServiceImpl")
	private EwbService ewbService;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("EinvoiceAsyncServiceImpl")
	private EinvoiceAsyncService einvoiceService;

	@Autowired
	@Qualifier("GenerateEWBByIrnServiceImpl")
	private GenerateEWBByIrnService generateEWBByIrn;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("GenerateEwayBillProcessor Begin");
			}
			String jsonString = message.getParamsJson();
			JsonObject requestParams = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Long id = requestParams.get("id").getAsLong();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("GenerateEwayBillProcessor ,calling "
						+ "ewbAsyncService with Id " + id);
			}
			Optional<OutwardTransDocument> transDocuments = docrepo
					.findById(id);

			if (transDocuments.isPresent()) {
			boolean isEinv = isEInvoiceRequest(transDocuments);
				if (isEinv) {
					GenerateEWBByIrnNICReqDto req = einvoiceService.
							convertOutWardtoGenEWBIrn(transDocuments.get());
					generateEWBByIrn.generateEwayIrnRequest(req, transDocuments.get());

				} else {
					ewbService.generateEwb(transDocuments.get(), true, false);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("GenerateEwayBillProcessor Job Completed");
				}

			}
		} catch (Exception e) {
			LOGGER.error(
					"Exception While processing the GenerateEwayBill Einv request",
					e);
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

	private boolean isEInvoiceRequest(Optional<OutwardTransDocument> transDocuments) {
		if (transDocuments.isPresent()) {
			OutwardTransDocument doc = transDocuments.get();
			return !Strings.isNullOrEmpty(doc.getIrnResponse())
					|| (!Strings.isNullOrEmpty(doc.getIrn()) && doc.getIrn().length() == 64);
		}
		return false;
	}

}
