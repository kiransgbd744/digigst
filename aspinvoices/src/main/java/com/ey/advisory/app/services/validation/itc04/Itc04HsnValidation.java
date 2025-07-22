/**
 * 
 */
package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.HsnOrSacRepository;
import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.app.services.strcutvalidation.ValidatorUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Itc04HsnValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	@Autowired
	@Qualifier("hsnOrSacRepository")
	private HsnOrSacRepository hsnOrSacRepository;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		List<Itc04ItemEntity> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Itc04ItemEntity item = items.get(idx);

			String hsn = item.getHsn();
			if (!Strings.isNullOrEmpty(hsn)) {
				if (!ValidatorUtil.isEvenNumber(hsn.length())) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.HSNORSAC);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5833",
							"Invalid HSN.", location));
				} else {

					String subHsn = hsn.substring(0, 2);

					if (GSTConstants.HSNSAC.equalsIgnoreCase(subHsn)) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.HSNORSAC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER5834", "HSN cannot start with 99 code.",
								location));
					} else {
						HsnCache hsnCache = StaticContextHolder
								.getBean("DefaultHsnCache", HsnCache.class);
						int i = hsnCache.findhsn(hsn);

						if (i == 0) {

							String hsnOrSac = item.getHsn();
							hsnOrSacRepository = StaticContextHolder.getBean(
									"hsnOrSacRepository",
									HsnOrSacRepository.class);
							int getHsnOrSac = hsnOrSacRepository
									.findByHsnOrSac(hsnOrSac);

							if (getHsnOrSac == 0) {

								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.HSNORSAC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										idx, errorLocations.toArray());
								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER5833", " Invalid HSN.", location));

							}
						}
					}

				}
			}
		});

		return errors;
	}
}
