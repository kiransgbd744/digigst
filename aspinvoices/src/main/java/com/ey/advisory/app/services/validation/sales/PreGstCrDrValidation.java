/*package com.ey.advisory.app.services.validation.sales;

import static com.ey.advisory.app.data.entities.client.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.app.data.entities.client.GSTConstants.CR;
import static com.ey.advisory.app.data.entities.client.GSTConstants.DR;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
*//**
 * This class is Responsible for Except PreGST CR/DR all GST related DR/CR 
 * notes to be reported alongwith an Original Invoice that is
 *  available in ASP/GST regime as per BR_Outward_8 . 
 *  Multiple Credit Notes/Debit Notes can be reported for a
 *   Single Invoice within same or different tax period(s)
 *//*


*//**
	 * 
	 * @author Siva.Nandam
	 *
	 *         BR_OUTWARD_48
	 *//*
@Component("PreGstCrDrValidation")
public class PreGstCrDrValidation
		implements DocRulesValidator<OutwardTransDocument> {
	private static final List<String> CR_DR_NOT_REQUIRING_IMPORTS = ImmutableList
			.of(CR, DR);

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();

		String groupCode = TenantContext.getTenantId();
		if (document.getOrigDocDate() != null) {
			if (document.getSgstin() != null
					&& !document.getSgstin().isEmpty()) {
				if (document.getOrigDocNo() != null
						&& !document.getOrigDocNo().isEmpty()) {
					docRepository = StaticContextHolder.getBean("DocRepository",
							DocRepository.class);
					String sgstin = document.getSgstin();
					String origDocNo = document.getOrigDocNo();
					LocalDate origDocDate = document.getOrigDocDate();
					int i = docRepository.findInvoices(sgstin, origDocNo,
							origDocDate);
					if (document.getDocType() != null
							&& !document.getDocType().isEmpty()) {
						if (CR_DR_NOT_REQUIRING_IMPORTS.contains(
								trimAndConvToUpperCase(document.getDocType()))) {

							if (document.isCrDrPreGst() == false) {
								if (i <= 0) {
									List<String> locations = new ArrayList<>();
									locations.add(GSTConstants.ORIGINAL_DOC_NO);
									TransDocProcessingResultLoc location 
									       = new TransDocProcessingResultLoc(
											null, locations.toArray());

									errors.add(new ProcessingResult(
											APP_VALIDATION, "ERXXX",
											"Original Document No. "
													+ "was never reported",
											location));

								}
							}
						}
					}

				}
			}
		}
		return errors;
	}

}
*/