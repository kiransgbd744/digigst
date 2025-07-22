package com.ey.advisory.app.services.docs.gstr2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.gstr2.InwardDocSaveFinalRespDto;
import com.ey.advisory.app.services.bifurcation.DocBifurcator;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.validation.DocRulesValidationResult;
import com.ey.advisory.app.services.validation.purchase.PurchaseDocRulesValidatorService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.multitenancy.TenantContext;

@Service("DefaultAnx2InwardDocSaveService")
public class DefaultAnx2InwardDocSaveService implements InwardDocSaveService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultAnx2InwardDocSaveService.class);

	@Autowired
	@Qualifier("InwardDocSave")
	private InwardDocSave inwardDocSave;

	@Autowired
	@Qualifier("InwardDocError")
	private InwardDocError inwardDocError;

	@Autowired
	@Qualifier("PurchaseDocRulesValidatorService")
	private PurchaseDocRulesValidatorService purchaseDocRulesValidatorService;

	@Autowired
	private DocKeyGenerator<InwardTransDocument, String> docKeyGen;

	@Autowired
	@Qualifier("SimplifiedInwardTransDocBifurcator")
	private DocBifurcator<InwardTransDocument> simplifiedBifurcator;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository docHeaderRepository;

	@Override
	public InwardDocSaveFinalRespDto saveDocuments(
			List<InwardTransDocument> documents, String companyCode, String headerPayloadId) {

		InwardDocSaveFinalRespDto docSaveResponse = null;
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug("Entered Inward saveDocuments for groupCode " + groupCode);
		ProcessingContext context = new ProcessingContext();
		context.seAttribute("groupCode", groupCode);

		// Set Supply Type to Header
		// inwardDocSave.setSupplyTypeToHeader(documents);

		// Invoke the validation service and the processing results.
		DocRulesValidationResult<String> valResult = purchaseDocRulesValidatorService
				.validate(documents, context);
		Map<String, List<ProcessingResult>> processingResults = valResult
				.getProcessingResults();

		// Bifurcate Inward Documents
		bifurcateInwardDoc(documents, processingResults, groupCode);

		// Get the list of document ids for hte existing documetns and keep
		// it aside, so that we can use this to populate the 'oldDocId'
		// value
		// while creating the response.
		// LocalDateTime beforeSavingTime = LocalDateTime.now();
		LocalDateTime beforeSavingTime = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		documents.forEach(document -> {
			document.setBeforeSavingOn(beforeSavingTime);
		});

		List<InwardTransDocument> savedDocs = docHeaderRepository
				.saveAll(documents);

		// This is for locating the error
		Map<String, InwardTransDocument> errDocMap = inwardDocError
				.locateDocErrors(documents, processingResults, valResult,
						docKeyGen);

		// Keep the list of errors ready.
		Map<String, List<InwardTransDocError>> errorMap = inwardDocError
				.convertErrors(processingResults, errDocMap);

		// Add all the errors into a single list to save to the DB.
		inwardDocError.saveErrors(errorMap, savedDocs, docKeyGen);

		return docSaveResponse;
	}

	private void bifurcateInwardDoc(List<InwardTransDocument> documents,
			Map<String, List<ProcessingResult>> processingResults,
			String groupCode) {

		// Initialize an empty processing context and pass it to the
		// bifurcator.
		ProcessingContext bifContext = new ProcessingContext();
		documents.forEach(doc -> {
			doc = simplifiedBifurcator.bifurcate(doc, bifContext);
			if (!simplifiedBifurcator.isBifurcated(doc)) {
				String docKey = docKeyGen.generateKey(doc);
				List<ProcessingResult> prList = processingResults.get(docKey);
				ProcessingResult pr = new ProcessingResult("ASP", "ER0501",
						"Transaction cannot be mapped to any of the Tables "
								+ "of the Annexure-2 Return Form");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Bifurcation for Document" + doc
							+ "  for groupCode " + groupCode);
				}
				// If the document does not have any validation errors, then
				// the prList will be null. In that case we need to create a
				// new list and add the errors to the list.
				if (prList != null) {
					prList.add(pr);
				} else {
					prList = new ArrayList<>();
					prList.add(pr);
					processingResults.put(docKey, prList);
				}
			}
		});
	}
}
