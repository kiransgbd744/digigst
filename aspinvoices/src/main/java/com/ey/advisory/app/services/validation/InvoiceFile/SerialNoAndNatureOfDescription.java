package com.ey.advisory.app.services.validation.InvoiceFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.app.caches.NatureOfDocCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class SerialNoAndNatureOfDescription
		implements B2csBusinessRuleValidator<Gstr1AsEnteredInvEntity> {

	@Autowired
	@Qualifier("DefaultNatureOfDocCache")
	private NatureOfDocCache natureOfDocCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredInvEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		Set<String> errorLocations = new HashSet<>();

		Integer serialNumber = Integer.parseInt(document.getSerialNo());
		String natureOfDes = document.getNatureOfDocument();

		if (serialNumber != null && serialNumber > 0) {
			if (natureOfDes != null && !natureOfDes.isEmpty()) {
				natureOfDocCache = StaticContextHolder.getBean(
						"DefaultNatureOfDocCache", NatureOfDocCache.class);
				NatureOfDocEntity natureOfDoc = natureOfDocCache
						.findNatureOfDoc(serialNumber);

				natureOfDes = natureOfDoc != null
						? natureOfDes.replaceAll("\\s", "").toUpperCase()
						: null;
				String natureOfDocs = natureOfDoc.getNatureDocType()
						.replaceAll("\\s", "").toUpperCase();

				if (!natureOfDes.equalsIgnoreCase(natureOfDocs)) {
					errorLocations.add(GSTConstants.NatureOfDocument);
					errorLocations.add(GSTConstants.SERIAL_NUMBER);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER5517",
							"Mapping of Serial Number and Nature Of Document is incorrect.",
							location));
				}
			}
		}
		return errors;
	}

}
