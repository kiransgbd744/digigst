package com.ey.advisory.app.services.itc04;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;

public class Itc04SezValidator
		implements Itc04DocRulesValidator<Itc04HeaderEntity> {
	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Override
	public List<ProcessingResult> validate(Itc04HeaderEntity document,
			ProcessingContext context) {

		List<ProcessingResult> errors = new ArrayList<>();
		if (GSTConstants.CAN.equalsIgnoreCase(document.getActionType()))
			return errors;

		String groupCode = TenantContext.getTenantId();

		if (document.getSupplierGstin() != null
				&& !document.getSupplierGstin().isEmpty()) {
			ehcachegstin = StaticContextHolder.getBean("Ehcachegstin",
					Ehcachegstin.class);

			GSTNDetailEntity gstin = ehcachegstin.getGstinInfo(groupCode,
					document.getSupplierGstin());
			String regType = gstin != null ? gstin.getRegistrationType().trim()
					: null;

			BigDecimal cgstAmount = document.getCgstAmount() != null
					? document.getCgstAmount() : BigDecimal.ZERO;
			BigDecimal sgstAmount = document.getSgstAmount() != null
					? document.getSgstAmount() : BigDecimal.ZERO;
			BigDecimal cgstRate = document.getLineItems().get(0)
					.getCgstRate() != null
							? document.getLineItems().get(0).getCgstRate()
							: BigDecimal.ZERO;
			BigDecimal sgstRate = document.getLineItems().get(0)
					.getSgstRate() != null
							? document.getLineItems().get(0).getSgstRate()
							: BigDecimal.ZERO;
			BigDecimal amountTotal = cgstAmount.add(sgstAmount);
			BigDecimal rateTotal = cgstRate.add(sgstRate);

			if (GSTConstants.SEZU.equalsIgnoreCase(regType)
					|| GSTConstants.SEZD.equalsIgnoreCase(regType)) {
				if (amountTotal.compareTo(BigDecimal.ZERO) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.cgstamount);
					errorLocations.add(GSTConstants.sgstamount);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5866",
							"CGST / SGST cannot be applied as SupplierGSTIN is on boarded as SEZ.",
							location));
					// return errors;

				}
				if (rateTotal.compareTo(BigDecimal.ZERO) != 0) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.CGST_RATE);
					errorLocations.add(GSTConstants.SGST_RATE);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(APP_VALIDATION, "ER5866",
							"CGST / SGST cannot be applied as SupplierGSTIN is on boarded as SEZ.",
							location));
					// return errors;

				}
			}

		}

		return errors;
	}

}
