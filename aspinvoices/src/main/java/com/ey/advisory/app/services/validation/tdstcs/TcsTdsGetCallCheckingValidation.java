package com.ey.advisory.app.services.validation.tdstcs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.entities.client.Gstr2XExcelTcsTdsEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTdsAndTdsaInvoicesEntity;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.app.services.validation.B2csBusinessRuleValidator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Mahesh.Golla
 *
 */

public class TcsTdsGetCallCheckingValidation
		implements B2csBusinessRuleValidator<Gstr2XExcelTcsTdsEntity> {

	private static final List<String> CATEGORY = ImmutableList.of("TCS",
			"TCSA");

	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xGetTDSAndTDSADetailsAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	private Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xGetTCSAndTCSADetailsAtGstnRepository;

	@Override
	public List<ProcessingResult> validate(Gstr2XExcelTcsTdsEntity document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String category = document.getRecordType();

		String recordType = document.getRecordType();
		String gstin = document.getGstin();
		String retPeriod = document.getRetPeriod();
		String ctin = document.getCtin();
		String deductorUplMonth = document.getDeductorUplMonth();
		String orgDeductorUplMonth = document.getOrgDeductorUplMonth();
		if (category != null && !category.isEmpty()) {
			category.trim().toUpperCase();
			if (CATEGORY.contains(category)) {

				gstr2xGetTCSAndTCSADetailsAtGstnRepository = StaticContextHolder
						.getBean("Gstr2xGetTCSAndTCSADetailsAtGstnRepository",
								Gstr2xGetTCSAndTCSADetailsAtGstnRepository.class);

				List<GetGstr2xTcsAndTcsaInvoicesEntity> findAllTcsTcsa = new ArrayList<>();

				if (orgDeductorUplMonth != null) {
					findAllTcsTcsa = gstr2xGetTCSAndTCSADetailsAtGstnRepository
							.findRecords(recordType, gstin, retPeriod, ctin,
									deductorUplMonth, orgDeductorUplMonth);
				} else {
					findAllTcsTcsa = gstr2xGetTCSAndTCSADetailsAtGstnRepository
							.findRecordsWithOrgDrUplMonth(recordType, gstin,
									retPeriod, ctin, deductorUplMonth);
				}
				if (findAllTcsTcsa == null || findAllTcsTcsa.isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.GSTR_2X_GET_RECORDS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER5934",
							"Record is not available in the Get call data to provide response.",
							location));
					return errors;
				}
			} else {
				gstr2xGetTDSAndTDSADetailsAtGstnRepository = StaticContextHolder
						.getBean("Gstr2xGetTDSAndTDSADetailsAtGstnRepository",
								Gstr2xGetTDSAndTDSADetailsAtGstnRepository.class);
				List<GetGstr2xTdsAndTdsaInvoicesEntity> findAllTdsTdsa = new ArrayList<>();

				if (orgDeductorUplMonth != null) {
					findAllTdsTdsa = gstr2xGetTDSAndTDSADetailsAtGstnRepository
							.findRecords(recordType, gstin, retPeriod, ctin,
									deductorUplMonth, orgDeductorUplMonth);
				} else {
					findAllTdsTdsa = gstr2xGetTDSAndTDSADetailsAtGstnRepository
							.findRecordsWithOrgDrUplMonth(recordType, gstin,
									retPeriod, ctin, deductorUplMonth);
				}

				if (findAllTdsTdsa == null || findAllTdsTdsa.isEmpty()) {
					Set<String> errorLocations = new HashSet<>();
					errorLocations.add(GSTConstants.GSTR_2X_GET_RECORDS);
					TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
							null, errorLocations.toArray());
					errors.add(new ProcessingResult(GSTConstants.APP_VALIDATION,
							"ER5934",
							"Record is not available in the Get call data to provide response.",
							location));
					return errors;
				}

			}
		}
		return errors;
	}
}
