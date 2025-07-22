/*
package com.ey.advisory.app.services.validation.sales;
import static com.ey.advisory.app.data.entities.client.GSTConstants.
																APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.SUPPLY_TYPE;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.InvRnvValidationRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;

*//**
 * 
 * @author Balakrishna.S
 * 
 *  BR_OUTWARD_50
 *
 *//*
@Component("canDocValidation")
public class CanDocValidator
		implements DocRulesValidator<OutwardTransDocument> {

	private InvRnvValidationRepository invRnvValidationRepo;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		List<String> errorLocations = new ArrayList<>();
		String supplyType = document.getSupplyType();
		String sgstin = document.getSgstin();
		String docNum = document.getDocNo();
		LocalDate docDate = document.getDocDate();
		String cgstin = document.getCgstin();
		if (CAN.equals(supplyType)) {
			invRnvValidationRepo = StaticContextHolder.getBean(
					"invRnvValidationRepository", 
					InvRnvValidationRepository.class);

			int count = invRnvValidationRepo.findByCgstin(sgstin, docNum,
					docDate, cgstin);

			if (count <= 0) {
				errorLocations.add(SUPPLY_TYPE);
				TransDocProcessingResultLoc location = 
				new TransDocProcessingResultLoc(errorLocations.toArray());
				errors.add(new ProcessingResult(APP_VALIDATION, "ERRXXX",
						"Original Document is not reported in the current "
						+ "tax period",location));
			}
		}
		return errors;
	}
}
*/