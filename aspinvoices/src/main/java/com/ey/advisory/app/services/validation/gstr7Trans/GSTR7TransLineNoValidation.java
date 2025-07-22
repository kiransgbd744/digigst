package com.ey.advisory.app.services.validation.gstr7Trans;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocHeaderEntity;
import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocItemEntity;
import com.ey.advisory.app.services.docs.gstr7.Gstr7TransDocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("GSTR7TransLineNoValidation")
public class GSTR7TransLineNoValidation
		implements Gstr7TransDocRulesValidator<Gstr7TransDocHeaderEntity> {

	@Override
	public List<ProcessingResult> validate(Gstr7TransDocHeaderEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();

		List<Gstr7TransDocItemEntity> items = document.getLineItems();
		Set<Integer> set = new HashSet<>();
		IntStream.range(0, items.size()).forEach(idx -> {

			Gstr7TransDocItemEntity item = items.get(idx);
			Integer lineNo = item.getLineItemNumber();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Line No {} ", lineNo);
			}
			if (!set.add(lineNo)) {

				TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
						null, errorLocations.toArray());

				errorLocations.add(GSTConstants.LINE_NO);

				errors.add(new ProcessingResult(APP_VALIDATION, "ER63044",
						"Line number in a document cannot be repeated",
						location));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Got Errored");
				}

			}

		});

		return errors;
	}
}
