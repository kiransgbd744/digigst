/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1SaveToGstnDependencyRetryReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1BatchMaker;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SaveToGstnDependentRetryHandler")
public class Gstr1SaveToGstnDependentRetryHandler {

	private static final String LOG1 = "Gstr1 {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr1 {} SaveToGstn section has completed";

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("DocRepositoryGstr1A")
	private DocRepositoryGstr1A docRepositoryGstr1A;

	@Autowired
	@Qualifier("gstr1BatchMakerImpl")
	private Gstr1BatchMaker gstr1BatchMaker;

	public List<SaveToGstnBatchRefIds> reSaveSpecificErorrCodesOfBatchInvoices(
			String jsonReq, String groupCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr1SaveToGstnDependencyRetryReqDto dto = gson.fromJson(
					requestObject, Gstr1SaveToGstnDependencyRetryReqDto.class);

			Long batchId = dto.getBatchId();
			Long retryCount = dto.getRetryCount();
			Long userRequestId = dto.getUserRequestId();
			Set<String> errorCodes = dto.getErrorCodes();
			String section = dto.getSection() != null
					? dto.getSection().toLowerCase() : null;
			SaveToGstnOprtnType operationType = dto.getOperationType() != null
					? dto.getOperationType() : SaveToGstnOprtnType.SAVE;

			String returnType = Strings.isNullOrEmpty(dto.getReturnType()) ? 
					APIConstants.GSTR1.toUpperCase():APIConstants.GSTR1A.toUpperCase();		
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, section);

			List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
			List<Object[]> docs = new ArrayList<>();
			/**
			 * Get all the processed, gstn specific error sent to gstn documents
			 * that are present in a batch.
			 */
			if (APIConstants.GSTR1A.toUpperCase()
							.equalsIgnoreCase(dto.getReturnType())) {
				docs = docRepositoryGstr1A
						.findGstr1DependentRetryInvoiceLevelData(section,
								batchId,			errorCodes);
			} else {
				docs = docRepository
						.findGstr1DependentRetryInvoiceLevelData(section,
								batchId,			errorCodes);
			}
			
			ProcessingContext context = new ProcessingContext();
			context.seAttribute(APIConstants.RETURN_TYPE_STR,
					returnType);
			// This does the actual Gstr1 SaveToGstn operation for a given
			// section
			// by forming the Json structure as per government published API.
			if (docs != null && !docs.isEmpty()) {
				respList = gstr1BatchMaker.saveGstr1Data(groupCode, section,
						docs, operationType, null, retryCount, userRequestId,
						null, context);
			} else {
				String msg = "Zero Docs found to do {} Save to Gstn with args {}";

				LOGGER.error(msg, section, batchId, errorCodes);
			}

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, section);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr1 SaveToGstn Job has Finshed");
			}

			return respList;

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}
}
