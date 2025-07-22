package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr7trans.Gstr7TransDocHeaderRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransHeaderDocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("GSTR7TransCanValidation")
public class GSTR7TransCanValidation
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	@Autowired
	@Qualifier("Gstr7TransDocHeaderRepository")
	private Gstr7TransDocHeaderRepository docRepository;

	@Autowired
	@Qualifier("Gstr7TransHeaderDocKeyGenerator")
	private DocKeyGenerator<Gstr7TransDocHeaderEntity, String> docKeyGen;

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		String docKey = document.getDocKey();
		
		String finYear = document.getFiYear() != null ? document.getFiYear() : "";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR7TransCanValidation Doc Key: '%s' & Supply Type: '%s' & finYear : '%s'",
					docKey, document.getSupplyType(),finYear);

			LOGGER.debug(msg);
		}

		if (!GSTConstants.CAN.equalsIgnoreCase(document.getSupplyType()))
			return errors;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR7TransCanValidation calling generateKey()");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR7TransCanValidation docKeyGen : "+docKeyGen);
		}
		
		try {
			docKey = docKeyGen.generateKey(document);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR7TransCanValidation calling generateKey()");
			}
		} catch (Exception e) {
			LOGGER.error("Exception while generating the docKey {} ", e);
			docKey = document.getDocKey();
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR7TransCanValidation Updated Doc Key: '%s'", docKey);
			LOGGER.debug(msg);
		}
		
		docKey=generateKey(document);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR7TransCanValidation inside Updated Doc Key: '%s'", docKey);
			LOGGER.debug(msg);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR7TransCanValidation docRepository : "+docRepository);
		}

		Optional<Gstr7TransDocHeaderEntity> originalDoc = docRepository
				.findOrgDocByDocKey(docKey);

		if (LOGGER.isDebugEnabled()) {
			if (originalDoc != null)
			LOGGER.debug("Printing originalDoc value : " + originalDoc);
			else
			LOGGER.debug("Printing originalDoc value : is null !");
		}

		if (originalDoc != null && originalDoc.isPresent())
			return errors;

		// Tax Can (active, is_err=false) // Can (is dl=false, is error_true)

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Executed validator '%s'. No: of Results "
							+ "available: '%d'",
					"GSTR7TransCanValidation",
					(originalDoc != null) ? originalDoc : 0);
			LOGGER.debug(msg);
		}

		if (originalDoc == null || !originalDoc.isPresent()) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR7TransCanValidation inside 'originalDoc == null' ");
				LOGGER.debug(msg);
			}

			errorLocations.add(GSTConstants.CAN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					errorLocations.toArray());

			errors.add(
					new ProcessingResult(APP_VALIDATION, "ER63046",
							"Document cannot be cancelled as the "
									+ "same was not reported to ASP System",
							location));
			return errors;
		}

		return errors;

	}
	
   public String generateKey(Gstr7TransDocHeaderEntity doc) {
	   
	    String DOC_KEY_JOINER = "|";
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR7TransCanValidation inside generateKey()");
		}

		String docType = (doc.getDocType() != null) ? doc.getDocType().trim()
				: "";
		String docNo = (doc.getDocNum() != null) ? doc.getDocNum().trim() : "";

		if (docNo != null && docNo.startsWith(GSTConstants.SPE_CHAR)) {
			docNo = docNo.substring(1);
		}

		String deductorGstin = (doc.getDeductorGstin() != null)
				? doc.getDeductorGstin().trim() : "";

		String finYear = doc.getFiYear() != null ? doc.getFiYear() : "";
		
		String generateKey = new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
		.add(finYear).add(docNo).toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GSTR7TransCanValidation generateKey : "+generateKey);
		}

		return new StringJoiner(DOC_KEY_JOINER).add(docType).add(deductorGstin)
				.add(finYear).add(docNo).toString();
	}

}
