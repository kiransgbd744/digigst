package com.ey.advisory.app.services.structuralvalidation.gstr9;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.common.BusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * 
 * @author Anand3.M
 *
 */
public class Gstr9HsnValidator
		implements BusinessRuleValidator<Gstr9HsnAsEnteredEntity> {

	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnRep;

	@Override
	public List<ProcessingResult> validate(Gstr9HsnAsEnteredEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();

		if (document.getHsn() != null && !document.getHsn().isEmpty()) {

			if (!ValidatorUtil.isEvenNumber(document.getHsn().length())) {
				Set<String> errorLocations = new HashSet<>();
				errorLocations.add(HSN);
				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ER6173",
						"Invalid HSN", location));
			} else {

				hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
						HsnCache.class);
				String hsnOrSac = document.getHsn();
				int i = hsnCache.findhsn(hsnOrSac);

				if (i == 0) {
					hsnRep = StaticContextHolder.getBean(
							"HsnOrSacRepositoryMaster",
							HsnOrSacRepository.class);

					int findByHsnOrSac = hsnRep.findByHsnOrSac(hsnOrSac);

					if (findByHsnOrSac == 0) {

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(HSN);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER6186", "Invalid HSN", location));

					}

				}
			}
		}

		return errors;
	}

}
