/*package com.ey.advisory.app.services.validation.eInvoice;

import java.util.ArrayList;
import java.util.List;
import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;
import com.ey.advisory.app.data.entities.client.GSTConstants;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.google.common.collect.ImmutableList;

*//**
 * @author Siva.Nandam
 *
 *//*
public class EinvoiceDocTypeValidation 
                    implements DocRulesValidator<OutwardTransDocument> { 

	private static final List<String> E_DOCTYPE = ImmutableList.of(
			GSTConstants.INV, GSTConstants.CR, GSTConstants.DR,
			GSTConstants.SLF);
	private static final List<String> E_CATEGORY = ImmutableList.of(
			GSTConstants.B2B, GSTConstants.B2G, GSTConstants.ISD);
	
	private static final List<String> E_SUPPLYTYPE = ImmutableList.of(
			GSTConstants.EXPT, GSTConstants.EXPWT);
	
	private static final List<String> DOCTYPE = ImmutableList.of(
			GSTConstants.INV, GSTConstants.CR, GSTConstants.DR);
	@Override
	public List<ProcessingResult> validate(OutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		if (document.getDocType() == null || document.getDocType().isEmpty())
			return errors;
		
		if (document.getCategory() == null || document.getCategory().isEmpty())
			return errors;
		if (document.getSupplyType() == null || document.getSupplyType().isEmpty())
			return errors;

		if ((E_DOCTYPE.contains(trimAndConvToUpperCase(document.getDocType()))
				&& E_CATEGORY.contains(
						trimAndConvToUpperCase(document.getCategory()))
				&& !E_SUPPLYTYPE.contains(
						trimAndConvToUpperCase(document.getSupplyType())))
				|| (DOCTYPE
						.contains(trimAndConvToUpperCase(document.getDocType()))
						&& GSTConstants.EXP
								.equalsIgnoreCase(document.getCategory())
						&& E_SUPPLYTYPE.contains(trimAndConvToUpperCase(
								document.getSupplyType())))) {
			document.setEinvoice(true);
		}
		
		return errors;
	}



}
*/