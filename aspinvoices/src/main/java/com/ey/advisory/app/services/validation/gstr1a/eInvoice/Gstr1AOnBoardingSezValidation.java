package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;
import static com.ey.advisory.common.GSTConstants.CGST_AMOUNT;
import static com.ey.advisory.common.GSTConstants.SGST_AMOUNT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocLineItem;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.collect.ImmutableList;

@Component("Gstr1AOnBoardingSezValidation")
public class Gstr1AOnBoardingSezValidation
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final String[] FIELD_LOCATIONS = { CGST_AMOUNT, SGST_AMOUNT,
			GSTConstants.CGST_RATE, GSTConstants.SGST_RATE };

	private static final List<String> REGYPE_IMPORTS = ImmutableList
			.of(GSTConstants.SEZD, GSTConstants.SEZU);

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		List<Gstr1AOutwardTransDocLineItem> items = document.getLineItems();
		if (document.getSgstin() == null || document.getSgstin().isEmpty())
			return errors;
		String groupCode = TenantContext.getTenantId();
		ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
				Ehcachegstin.class);

		GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
				document.getSgstin());
		if (gstin != null) {
			if (gstin.getRegistrationType() != null) {
				String regType = gstin.getRegistrationType();

				if (REGYPE_IMPORTS.contains(regType.toUpperCase())) {
					IntStream.range(0, items.size()).forEach(idx -> {

						Gstr1AOutwardTransDocLineItem item = items.get(idx);
						BigDecimal cgstAmount = item.getCgstAmount();
						BigDecimal sgstAmount = item.getSgstAmount();
						BigDecimal cgstRate = item.getCgstRate();
						BigDecimal sgstRate = item.getSgstRate();
						if (cgstAmount == null) {
							cgstAmount = BigDecimal.ZERO;
						}
						if (sgstAmount == null) {
							sgstAmount = BigDecimal.ZERO;
						}
						if (cgstRate == null) {
							cgstRate = BigDecimal.ZERO;
						}
						if (sgstRate == null) {
							sgstRate = BigDecimal.ZERO;
						}
						if (sgstAmount.compareTo(BigDecimal.ZERO) != 0
								|| cgstAmount.compareTo(BigDecimal.ZERO) != 0
								|| cgstRate.compareTo(BigDecimal.ZERO) != 0
								|| sgstRate.compareTo(BigDecimal.ZERO) != 0) {
							TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
									idx, FIELD_LOCATIONS);
							errors.add(new ProcessingResult(APP_VALIDATION,
									"ER0509",
									"CGST / SGST cannot be applied as "
											+ "Supplier is Registered as SEZ.",
									location));
						}

					});
				}
			}
		}
		return errors;
	}

}
