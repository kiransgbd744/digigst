package com.ey.advisory.app.services.validation.gstr9.inoutward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.master.NatureOfSupEntity;
import com.ey.advisory.app.caches.NatureOfSupCache;
import com.ey.advisory.app.data.entities.client.Gstr9OutwardInwardAsEnteredEntity;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Slf4j
public class Gstr9InOutwardTaleNumber implements
		B2csBusinessRuleValidator<Gstr9OutwardInwardAsEnteredEntity> {

	@Autowired
	@Qualifier("DefaultNatureOfSuppliesCache")
	private NatureOfSupCache natureOfSupCache;

	@Override
	public List<ProcessingResult> validate(
			Gstr9OutwardInwardAsEnteredEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getTableNumber() != null
				&& !document.getTableNumber().isEmpty()) {
			natureOfSupCache = StaticContextHolder.getBean(
					"DefaultNatureOfSuppliesCache", NatureOfSupCache.class);

			NatureOfSupEntity findNatureOfSupp = natureOfSupCache
					.findNatureOfSupp(
							document.getTableNumber().trim().toUpperCase());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"findNatureOfSupp: [{}] and TableNumber: [{}]",
						findNatureOfSupp, document.getTableNumber().trim().toUpperCase());
			}
			if (findNatureOfSupp == null) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(GSTConstants.TABLE_NUMBER);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
						"ER6154", "Invalid Table Number.", location));
				return errors;
			}
		}
		return errors;
	}
}
