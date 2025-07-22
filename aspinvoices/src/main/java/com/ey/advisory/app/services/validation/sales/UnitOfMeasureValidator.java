package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.HSNSAC;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocLineItem;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

@Component("unitOfMeasureValidator")
public class UnitOfMeasureValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private static final List<String> DOC_TYPES_REQUIRING_IMPORTS = ImmutableList
			.of(GSTConstants.INV, GSTConstants.RNV);

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<OutwardTransDocLineItem> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			OutwardTransDocLineItem item = items.get(idx);

			if (item.getHsnSac() != null && !item.getHsnSac().isEmpty()) {
				if (document.getDocType() != null
						&& !document.getDocType().isEmpty()) {
					if (item.getHsnSac().length() > 1) {
						if (!(item.getHsnSac().substring(0, 2).equals(HSNSAC))
								&& (DOC_TYPES_REQUIRING_IMPORTS
										.contains(document.getDocType()))) {
							if ((!(item.getUom() != null))
									|| item.getUom().isEmpty()) {

								Set<String> errorLocations = new HashSet<>();
								errorLocations.add(GSTConstants.UQC);
								TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
										null, errorLocations.toArray());

								errors.add(new ProcessingResult(APP_VALIDATION,
										"ER073", "Invalid Unit of Measurement",
										location));

							}
						}

					}
				}
			}

		});

		return errors;

	}

}
