package com.ey.advisory.app.services.validation.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.app.services.itc04.Itc04DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;


public class Itc04ProductDescriptionValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {


	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		List<Itc04ItemEntity> items = document.getLineItems();

		IntStream.range(0, items.size()).forEach(idx -> {
			Itc04ItemEntity item = items.get(idx);
			
					String prodDesc = item.getProductDescription();
					String regex = "^[a-zA-Z0-9 /-]+$";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(prodDesc);
					if (!matcher.matches()) {
						Set<String> errorLocations = new HashSet<>();
						errorLocations.add(GSTConstants.PRODUCT_DES);
						TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
								null, errorLocations.toArray());
						errors.add(new ProcessingResult(APP_VALIDATION, "ER1315",
								"Invalid Product Description.", location));
						//return errors;
					}
			
			
		});
		return errors;
	}
}
