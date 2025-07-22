package com.ey.advisory.app.services.validation.HsnSacSummery;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.NewHsnOrSac;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.master.HsnOrSacRepository;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredHsnEntity;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

public class HsnOrSac
		implements B2csBusinessRuleValidator<Gstr1AsEnteredHsnEntity> {
	
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Autowired
	@Qualifier("HsnOrSacRepositoryMaster")
	private HsnOrSacRepository hsnRep;

	@Override
	public List<ProcessingResult> validate(Gstr1AsEnteredHsnEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		if (document.getHsn() != null && !document.getHsn().isEmpty()) {
			String hsnOrSac = document.getHsn();

			hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
					HsnCache.class);

				if (!ValidatorUtil.isEvenNumber(hsnOrSac.length())) {
					// Set<String> errorLocations = new HashSet<>();
					errorLocations.add(NewHsnOrSac);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5705",
							"Invalid HSNorSAC", location));
				} else {

					hsnRep = StaticContextHolder.getBean(
							"HsnOrSacRepositoryMaster",
							HsnOrSacRepository.class);

					int i = hsnCache.findhsn(hsnOrSac);

					if (i == 0) {

						int findByHsnOrSac = hsnRep.findByHsnOrSac(hsnOrSac);

						if (findByHsnOrSac == 0) {

							errorLocations.add(NewHsnOrSac);
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									null, errorLocations.toArray());
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER5705", "Invalid HSNorSAC", location));

						}
					}
				}
			

		}

		return errors;
	}

}
