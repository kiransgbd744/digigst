/**
 * 
 */
package com.ey.advisory.app.gmr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("GMRInwardSummaryDownloadDaoImpl")
public class GMRInwardSummaryDownloadDaoImpl
		implements GMRInwardSummaryDownloadDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<GmrInwardSummaryDownloadDto> find(List<String> gstinList,
			Integer fromTaxPeriod, Integer toTaxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside GMRInwardSummaryDownloadDaoImpl");
			LOGGER.debug(msg);
		}
		List<GmrInwardSummaryDownloadDto> gmrDataList = null;

		try

		{
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GMR_INWARD_REPORT");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST",
					String.join(",", gstinList));

			storedProc.registerStoredProcedureParameter("P_FROM_DATE",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_FROM_DATE", fromTaxPeriod);

			storedProc.registerStoredProcedureParameter("P_TO_DATE",
					Integer.class, ParameterMode.IN);
			storedProc.setParameter("P_TO_DATE", toTaxPeriod);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"call stored proc with fromTaxPeriod '%s' and toTaxperiod %s",
						fromTaxPeriod, toTaxPeriod);
				LOGGER.debug(msg);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> records = storedProc.getResultList();

			if (records == null || records.isEmpty()) {

				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Converting records to dto");
				LOGGER.debug(msg);
			}
			gmrDataList = records.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			LOGGER.error("Exception while executing the query for "
					+ "Report Type :{}", ex);
		}

		return gmrDataList;
	}

	private GmrInwardSummaryDownloadDto convert(Object[] arr) {

		GmrInwardSummaryDownloadDto obj = new GmrInwardSummaryDownloadDto();

		obj.setSerialNumber((arr[0] != null) ? arr[0].toString() : null);

		obj.setInwardSupplyDetails(
						(arr[1] != null) ? arr[1].toString() : null);
		obj.setGross_taxableValue((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		obj.setIgst((arr[3] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setCgst((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setSgst((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setCess((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);

		return obj;
	}
}
