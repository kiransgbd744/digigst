package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */

@Slf4j
@Component("Gstr2InitiateReconReportDownloadDaoImpl")
public class Gstr2InitiateReconReportDownloadDaoImpl
		implements Gstr2InitiateReconReportDownloadDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr2InitiateReconReportDownloadDto> gstr2InitiateRecon(
			Gstr2InitiateReconDto request) {

		Integer toTaxPeriodPR = request.getToTaxPeriodPR() != null
				&& request.getToTaxPeriodPR() != ""
				&& !request.getToTaxPeriodPR().isEmpty()
						? Integer.parseInt(request.getToTaxPeriodPR()) : 999999;

		Integer fromTaxPeriodPR = request.getFromTaxPeriodPR() != null
				&& request.getFromTaxPeriodPR() != ""
				&& !request.getFromTaxPeriodPR().isEmpty()
						? Integer.parseInt(request.getFromTaxPeriodPR())
						: 999999;
		Integer toTaxPeriod2A = request.getToTaxPeriod2A() != null
				&& request.getToTaxPeriod2A() != ""
				&& !request.getToTaxPeriod2A().isEmpty()
						? Integer.parseInt(request.getToTaxPeriod2A()) : 999999;

		Integer fromTaxPeriod2A = request.getFromTaxPeriod2A() != null
				&& request.getFromTaxPeriod2A() != ""
				&& !request.getFromTaxPeriod2A().isEmpty()
						? Integer.parseInt(request.getFromTaxPeriod2A())
						: 999999;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String defaultDate = "9999-12-31";

		Date toDocDate = null;
		Date fromDocDate = null;
		try {
			toDocDate = request.getToDocDate() != null
					&& request.getToDocDate() != ""
					&& !request.getToDocDate().isEmpty()
							? formatter.parse(request.getToDocDate())
							: formatter.parse(defaultDate);

			fromDocDate = request.getFromDocDate() != null
					&& request.getFromDocDate() != ""
					&& !request.getFromDocDate().isEmpty()
							? formatter.parse(request.getFromDocDate())
							: formatter.parse(defaultDate);

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {

					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		try {

			LOGGER.info("Invoking USP_PRE_RECON_SUMMARY Stored Proc");
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_PRE_RECON_SUMMARY");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_FROM_TXPRD",
					Integer.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_TO_TXPRD",
					Integer.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_A2_FROM_TXPRD",
					Integer.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_A2_TO_TXPRD",
					Integer.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_FROM_DATE",
					Date.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("P_TO_DATE", Date.class,
					ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST",
					String.join(",", gstinList));

			storedProc.setParameter("P_FROM_TXPRD", fromTaxPeriodPR);

			storedProc.setParameter("P_TO_TXPRD", toTaxPeriodPR);

			storedProc.setParameter("P_A2_FROM_TXPRD", fromTaxPeriod2A);

			storedProc.setParameter("P_A2_TO_TXPRD", toTaxPeriod2A);

			storedProc.setParameter("P_FROM_DATE", fromDocDate);

			storedProc.setParameter("P_TO_DATE", toDocDate);

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<Gstr2InitiateReconReportDownloadDto> retList = list.stream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet into List in ReconEntity ");
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr2InitiateReconReportDownloadDto convert(Object[] arr) {
		Gstr2InitiateReconReportDownloadDto initiateRecon = new Gstr2InitiateReconReportDownloadDto();

		initiateRecon.setPartiCulars(arr[0] != null ? arr[0].toString() : null);
		initiateRecon.setPrCount(arr[1] != null ? arr[1].toString() : null);
		initiateRecon
				.setPrTaxableValue(arr[2] != null ? arr[2].toString() : null);
		initiateRecon.setPrIgst(arr[3] != null ? arr[3].toString() : null);
		initiateRecon.setPrCgst(arr[4] != null ? arr[4].toString() : null);
		initiateRecon.setPrSgst(arr[5] != null ? arr[5].toString() : null);
		initiateRecon.setPrCess(arr[6] != null ? arr[6].toString() : null);
		initiateRecon.setA2Count(arr[12] != null ? arr[12].toString() : null);
		initiateRecon
				.setA2TaxableValue(arr[13] != null ? arr[13].toString() : null);
		initiateRecon.setA2Igst(arr[14] != null ? arr[14].toString() : null);
		initiateRecon.setA2Cgst(arr[15] != null ? arr[15].toString() : null);
		initiateRecon.setA2Sgst(arr[16] != null ? arr[16].toString() : null);
		initiateRecon.setA2Cess(arr[17] != null ? arr[17].toString() : null);

		return initiateRecon;
	}

}
