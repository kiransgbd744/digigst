package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNORSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ALuHsnValidator")
public class Gstr1ALuHsnValidator
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {
	@Autowired
	@Qualifier("DefaultHsnCache")
	private HsnCache hsnCache;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<ProcessingResult>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Gstr1AOutwardTransDocLineItem item = items.get(idx);

			hsnCache = StaticContextHolder.getBean("DefaultHsnCache",
					HsnCache.class);
			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {

				int hsn = Integer.parseInt(item.getHsnSac());
				if (hsn > 0) {
					String hsnOrSac = item.getHsnSac();
					int i = hsnCache.findhsn(hsnOrSac);

					if (i <= 0) {

						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(HSNORSAC);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								idx, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION,
								"ER0069", "Invalid HSN or SAC code", location));

					}
				}
			}

		});

		return errors;
	}

}
