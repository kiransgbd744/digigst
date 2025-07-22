/**
 * 
 */
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
import com.google.common.collect.ImmutableList;

/**
 * @author Laxmi.Salukuti
 *
 */
public class Itc04NatureOfJwValidation
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {

	private static final List<String> TABLE_NUM = ImmutableList.of(
			GSTConstants.TABLE_NUMBER_5A, GSTConstants.TABLE_NUMBER_5B,
			GSTConstants.TABLE_NUMBER_5C);

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;
		
		List<Itc04ItemEntity> items = document.getLineItems();
		String tableNum = document.getTableNumber();

		IntStream.range(0, items.size()).forEach(idx -> {
			Itc04ItemEntity item = items.get(idx);
			if (TABLE_NUM.contains(tableNum)) {
				if (item.getNatureOfJw() == null
						|| item.getNatureOfJw().isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.NATURE_OF_JW);
					TransDocProcessingResultLoc location =
							new TransDocProcessingResultLoc(
							idx, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5832",
							"NatureOfJW cannot be left blank.", location));
				}
			}
			if (item.getNatureOfJw() != null && !item.getNatureOfJw().isEmpty()) {
				String natureofJw = item.getNatureOfJw();
				String regex = "^[a-zA-Z0-9 /-]+$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(natureofJw);
				if (!matcher.matches()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.NATURE_OF_JW);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(null,
							errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER1314", 
							"Invalid NatureOfJW", location));
					// return errors;
				}
			}
		});
		return errors;
	}
}
