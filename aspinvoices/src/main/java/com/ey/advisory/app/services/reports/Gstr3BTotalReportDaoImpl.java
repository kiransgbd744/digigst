package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr3bdownloadProcessedDto;
import com.ey.advisory.app.data.views.client.Gstr3bdownloadTotalDto;
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

@Component("Gstr3BTotalReportDaoImpl")
public class Gstr3BTotalReportDaoImpl implements Gstr3BTotalDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(Gstr3BTotalReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr3bdownloadProcessedDto> generateTotalCsv(Anx1FileStatusReportsReqDto request) {

		Long fileId = request.getFileId();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" FILE_ID= :fileId ");
		}
		String queryStr = createProcessedQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DB data," + list);
		}
		return list.stream().map(o -> convertTotal(o)).collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr3bdownloadProcessedDto convertTotal(Object[] arr) {
		Gstr3bdownloadProcessedDto obj = new Gstr3bdownloadProcessedDto();

		obj.setTaxpayerGSTIN(arr[0] != null ? arr[0].toString() : null);
		//obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(
				arr[1] != null ? DownloadReportsConstant.CSVCHARACTER
						.concat(arr[1].toString()) : null);
		obj.setSerialNo(arr[2] != null ? arr[2].toString() : null);
		obj.setDescription(arr[3] != null ? arr[3].toString() : null);
		obj.setiGSTAmount(arr[4] != null ? arr[4].toString() : null);
		obj.setcGSTAmount(arr[5] != null ? arr[5].toString() : null);
		obj.setsGSTAmount(arr[6] != null ? arr[6].toString() : null);
		obj.setCessAmount(arr[7] != null ? arr[7].toString() : null);
		obj.setRecordStatus(arr[8] != null ? arr[8].toString() : null);

		return obj;
	}

	private String createProcessedQueryString(String buildQuery) {
		return "SELECT GSTIN,RETURN_PERIOD, SERIAL_NO,DESCRIPTION, "
				+ "IGST_AMT,CGST_AMT, SGST_AMT,CESS_AMT, CASE WHEN IS_DELETE = TRUE "
				+ "then 'INACTIVE' ELSE 'ACTIVE' END RECORD_STATUS FROM "
				+ "GSTR3B_ITC_AS_ENTERED WHERE  " + buildQuery;

	}
}
