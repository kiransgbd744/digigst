package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.Gstr2bVsGstr3bProcessSummaryReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2bVsGstr3bReviewSummaryFetchService")
public class Gstr2bVsGstr3bReviewSummaryFetchService {

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public List<Gstr2aVsGstr3bReviewSummaryRespDto> gstr2bVsGstr3bReviewSummaryRecords(
			Gstr2bVsGstr3bProcessSummaryReqDto req) throws Exception {

		List<Object[]> list = getGstr2bvs3bProcessedProcRecords(req);

		List<Gstr2aVsGstr3bReviewSummaryRespDto> finalResp = convertProcessedData(
				list);

		return finalResp;
	}

	public List<Object[]> getGstr2bvs3bProcessedProcRecords(
			Gstr2bVsGstr3bProcessSummaryReqDto dto) {

		String taxPeriodFrom = dto.getTaxPeriodFrom();
		Integer derivedTaxPeriodFrom = GenUtil
				.getDerivedTaxPeriod(taxPeriodFrom);
		String taxPeriodTo = dto.getTaxPeriodTo();
		Integer derivedTaxPeriodTo = GenUtil.getDerivedTaxPeriod(taxPeriodTo);
		String gstin = dto.getGstin();

		StoredProcedureQuery storedProc = procCall(gstin, derivedTaxPeriodFrom,
				derivedTaxPeriodTo);
		List<Object[]> list = storedProc.getResultList();

		return list;
	}

	private StoredProcedureQuery procCall(String gstin, Integer taxPeriodFrom,
			Integer taxPeriodTo) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_2BVS3B_COMPUTE_GSTIN");

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("About to execute GSTR6 SP with gstin :%s", gstin);
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("FROM_TAX_PERIOD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("FROM_TAX_PERIOD", taxPeriodFrom);

		storedProc.registerStoredProcedureParameter("TO_TAX_PERIOD",
				Integer.class, ParameterMode.IN);

		storedProc.setParameter("TO_TAX_PERIOD", taxPeriodTo);

		return storedProc;
	}

	private List<Gstr2aVsGstr3bReviewSummaryRespDto> convertProcessedData(
			List<Object[]> arrs) {
		List<Gstr2aVsGstr3bReviewSummaryRespDto> objs = new ArrayList<>();
		if (arrs != null && !arrs.isEmpty()) {
			for (Object[] arr : arrs) {
				Gstr2aVsGstr3bReviewSummaryRespDto obj = new Gstr2aVsGstr3bReviewSummaryRespDto();

				String description = (String) arr[0];
				obj.setDescription(description);
				String section = arr[1] != null ? String.valueOf(arr[1]) : null;
				obj.setCalFeild(section);
				obj.setIgst((BigDecimal) arr[2]);
				obj.setCgst((BigDecimal) arr[3]);
				obj.setSgst((BigDecimal) arr[4]);
				obj.setCess((BigDecimal) arr[5]);

				objs.add(obj);
			}
		}
		return objs;
	}
}
