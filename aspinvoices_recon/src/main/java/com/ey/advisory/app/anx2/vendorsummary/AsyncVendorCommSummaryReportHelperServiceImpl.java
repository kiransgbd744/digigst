package com.ey.advisory.app.anx2.vendorsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("AsyncVendorCommSummaryReportHelperServiceImpl")
public class AsyncVendorCommSummaryReportHelperServiceImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public Pair<List<VendorCommSummaryReportDto>, String> getSummaryDataFromSPandConvertToDto(
			Long reqId, String vendorGstin, boolean isAPOpted) {

		List<Object[]> list = null;
		List<String> rlist = null;
		List<VendorCommSummaryReportDto> reportDtos = null;
		String procName = null;
		String rGstinprocName = null;
		try {

			if (isAPOpted) {
				procName = "vendorCommSummReportDataAPOpted";
				rGstinprocName = "vendorCommSummRGstinDataAPOpted";
			} else {
				procName = "vendorCommSummReportDataNonAP";
				rGstinprocName = "vendorCommSummRGstinDataNonAp";
			}

			StoredProcedureQuery reportDataProc = entityManager
					.createNamedStoredProcedureQuery(procName);

			reportDataProc.setParameter("P_REQUEST_ID", reqId);
			reportDataProc.setParameter("P_VENDOR_GSTIN", vendorGstin);
			list = reportDataProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Executed Stored proc to get Data and "
								+ "got resultset of size: %d", list.size());
				LOGGER.debug(msg);
			}

			if (!list.isEmpty()) {
				reportDtos = list.stream().map(o -> convertToDto(o))
						.collect(Collectors.toCollection(ArrayList::new));

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Record count after converting object array to DTO ",
							reportDtos.size());
					LOGGER.debug(msg);
				}
			}
			StoredProcedureQuery rGstinDataProc = entityManager
					.createNamedStoredProcedureQuery(rGstinprocName);

			rGstinDataProc.setParameter("P_REQUEST_ID", reqId);
			rGstinDataProc.setParameter("P_VENDOR_GSTIN", vendorGstin);
			rlist = rGstinDataProc.getResultList();

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Executed Stored proc to get Data and "
								+ "got resultset of size: %d", list.size());
				LOGGER.debug(msg);
			}
			String rGstin = null;
			if (!rlist.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Recipient Gstin Record count : ", rlist.size());
					LOGGER.debug(msg);
				}
				rGstin = String.join(",", rlist);
			}
			return new Pair<>(reportDtos, rGstin);
		} catch (Exception ex) {
			String msg = "Exception occured while fetching data from storedProc";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

	private VendorCommSummaryReportDto convertToDto(Object[] obj) {
		try {
			VendorCommSummaryReportDto dto = new VendorCommSummaryReportDto();

			dto.setReportType(obj[0] != null ? obj[0].toString() : null);
			dto.setPrCountOfTrans(obj[1] != null ? obj[1].toString() : null);
			dto.setPrTaxableValue(obj[2] != null ? obj[2].toString() : null);
			dto.setPrTotalTax(obj[3] != null ? obj[3].toString() : null);
			dto.setGstr2ACountOfTrans(
					obj[4] != null ? obj[4].toString() : null);
			dto.setGstr2ATaxableValue(
					obj[5] != null ? obj[5].toString() : null);
			dto.setGstr2ATotalTax(obj[6] != null ? obj[6].toString() : null);
			dto.setDiffCountOfTrans(obj[7] != null ? obj[7].toString() : null);
			dto.setDiffTaxableValue(obj[8] != null ? obj[8].toString() : null);
			dto.setDiffTotalTax(obj[9] != null ? obj[9].toString() : null);
			return dto;
		} catch (Exception ee) {
			String msg = String.format(
					"Exception occered while converting obj to Dto %s", obj);
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}
}
