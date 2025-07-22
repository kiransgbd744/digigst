package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.pdfreader.PDFReaderServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("PdfUploadReaderProcessor")
public class PdfUploadReaderProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("PDFReaderServiceImpl")
	PDFReaderServiceImpl pdfReadService;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			
			Long id = json.get("id").getAsLong();
			String extension = json.get("fileType").getAsString();
			pdfReadService.saveAndPersistPdfSummary(id, extension);

		} catch (Exception e) {
			String errMsg = "Error Occured in QrCodeValidatorProcessor";
			LOGGER.error(errMsg, e);

			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

	}

}

