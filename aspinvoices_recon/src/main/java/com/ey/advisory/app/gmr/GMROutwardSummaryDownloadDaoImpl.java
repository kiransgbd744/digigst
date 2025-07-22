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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.HsnCache;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("GMROutwardSummaryDownloadDaoImpl")
public class GMROutwardSummaryDownloadDaoImpl
		implements GMROutwardSummaryDownloadDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	HsnCache hsnCache;

	@Override
	public List<GmrOutwardSummaryDownloadDto> find(List<String> gstinList,
			Integer fromTaxPeriod, Integer toTaxPeriod) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside GMROutwardSummaryDownloadDaoImpl");
			LOGGER.debug(msg);
		}
		List<GmrOutwardSummaryDownloadDto> gmrDataList = null;

		try

		{
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_GMR_OUTWARD_REPORT");

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

	private GmrOutwardSummaryDownloadDto convert(Object[] arr) {

		GmrOutwardSummaryDownloadDto obj = new GmrOutwardSummaryDownloadDto();

		obj.setSerialNumber((arr[0] != null) ? arr[0].toString() : null);

		obj.setHsn((arr[2] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[2].toString())
				: null);
		if (arr[2] != null) {
			String hsnCode = arr[2].toString();

			if ("-".equalsIgnoreCase(hsnCode)
					|| "N/A".equalsIgnoreCase(hsnCode)) {
				obj.setOutwardSupplyDetails(
						(arr[1] != null) ? arr[1].toString() : null);
			} else {
				String hsnDesc = hsnCache.findHsnDescription(hsnCode);
				obj.setOutwardSupplyDetails(hsnDesc != null
						? (arr[1] != null
								? arr[1].toString() + hsnDesc : hsnDesc)
						: null);
			}
		}
		obj.setTaxRate((arr[3] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[3].toString())
				: null);
		obj.setGross_taxableValue((arr[4] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[4].toString())
				: null);
		obj.setIgst((arr[5] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[5].toString())
				: null);
		obj.setCgst((arr[6] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[6].toString())
				: null);
		obj.setSgst((arr[7] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[7].toString())
				: null);
		obj.setCess((arr[8] != null)
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[8].toString())
				: null);

		return obj;
	}
}
