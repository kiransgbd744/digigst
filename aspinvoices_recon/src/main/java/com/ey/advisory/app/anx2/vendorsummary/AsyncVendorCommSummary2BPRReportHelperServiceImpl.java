package com.ey.advisory.app.anx2.vendorsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Service("AsyncVendorCommSummary2BPRReportHelperServiceImpl")
public class AsyncVendorCommSummary2BPRReportHelperServiceImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public Pair<List<VendorCommSummaryReportDto>, String> get2BPRSummaryDataFromSPandConvertToDto(
			Long reqId, String vendorGstin) {

		List<Object[]> list = null;
		List<String> rlist = null;
		List<VendorCommSummaryReportDto> reportDtos = null;
		String procName = null;
		String rGstinprocName = null;
		try {

			procName = "vendorComm2BPRSummaryReport";
			rGstinprocName = "vendorComm2BPRRgstinReport";

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
							"Record count after converting object array to DTO %d",
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
							"Recipient Gstin Record count : %d ", rlist.size());
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
