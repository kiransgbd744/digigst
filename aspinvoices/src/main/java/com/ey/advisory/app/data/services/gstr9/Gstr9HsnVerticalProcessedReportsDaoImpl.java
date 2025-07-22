package com.ey.advisory.app.data.services.gstr9;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnProcessedRecords;
import com.ey.advisory.app.services.reports.Gstr7VerticalDao;
import com.ey.advisory.common.CommonUtility;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr9HsnVerticalProcessedReportsDaoImpl")
public class Gstr9HsnVerticalProcessedReportsDaoImpl
		implements Gstr7VerticalDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr9HsnVerticalProcessedReportsDaoImpl.class);

	@Override
	public List<Object> getGstr7VerticalReports(
			Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HI.FILE_ID= :fileId ");
		}
		String queryStr = createVerticalTotalQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading query " + queryStr);
		}

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading Resultset" + list);
		}
		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Gstr9HsnProcessedRecords convertVerticalTotal(Object[] arr) {
		Gstr9HsnProcessedRecords obj = new Gstr9HsnProcessedRecords();

		obj.setGstin(arr[0] != null ? arr[0].toString() : null);
		obj.setFy(arr[1] != null ? arr[1].toString() : null);
		obj.setTableNumber(arr[2] != null ? arr[2].toString() : null);
		obj.setHsn(arr[3] != null ? arr[3].toString() : null);
		obj.setDescription(arr[4] != null ? arr[4].toString() : null);
		obj.setRateofTax(arr[5] != null ? arr[5].toString() : null);
		obj.setUqc(arr[6] != null ? arr[6].toString() : null);

		Object total = CommonUtility.exponentialAndZeroCheck(arr[7]);
		obj.setTotalQuantity(String.valueOf(total));
		Object taxValue = CommonUtility.exponentialAndZeroCheck(arr[8]);
		obj.setTaxableValue(String.valueOf(taxValue));
		obj.setConcessionalRateFlag(arr[9] != null ? arr[9].toString() : null);
		Object igst = CommonUtility.exponentialAndZeroCheck(arr[10]);
		obj.setIgst(String.valueOf(igst));
		Object cgst = CommonUtility.exponentialAndZeroCheck(arr[11]);
		obj.setCgst(String.valueOf(cgst));
		Object sgst = CommonUtility.exponentialAndZeroCheck(arr[12]);
		obj.setSgst(String.valueOf(sgst));
		Object cess = CommonUtility.exponentialAndZeroCheck(arr[13]);
		obj.setCess(String.valueOf(cess));

		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		return "SELECT GSTIN,FY,TABLE_NUMBER,HSN,DESCRIPTION,RATE_OF_TAX,UQC, TOTAL_QNT,TAXABLE_VALUE,CON_RATE_FLAG,"
				+ "IGST,CGST,SGST,CESS FROM GSTR9_HSN_PROCESSED HI LEFT OUTER JOIN "
				+ "FILE_STATUS FIL ON HI.FILE_ID = FIL.ID WHERE HI.IS_DELETE = FALSE "
				+ buildQuery;

	}
}
