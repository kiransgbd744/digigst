package com.ey.advisory.app.services.reports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.ey.advisory.app.docs.dto.Anx1FileStatusReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;

@Component("Gstr3BProcessedReportDaoImpl")
public class Gstr3BProcessedReportDaoImpl
		implements Gstr3bdownloadProcessedDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BProcessedReportDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr3bdownloadProcessedDto> generatProcessedCsv(
			Anx1FileStatusReportsReqDto request) {

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
		List<Gstr3bdownloadProcessedDto> processedData = list.stream()
				.map(o -> convertProcessed(o))
				.collect(Collectors.toCollection(ArrayList::new));
		List<Gstr3bdownloadProcessedDto> processedList = new ArrayList<>();

		for (Gstr3bdownloadProcessedDto processedDto : processedData) {
			if (processedDto.getTableNumberofGSTR3B()
					.equalsIgnoreCase("4(a)(5)(5.2.a)")) {
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("ddMMyyyy");
				LocalDate newDate = LocalDate.of(2022, 8, 01);
				String tax = "01"
						+ processedDto.getReturnPeriod().substring(1).trim();
				LocalDate returnPeriod = LocalDate.parse(tax, formatter);
				if (!(returnPeriod.compareTo(newDate) < 0)) {
					processedDto.setTableNumberofGSTR3B("4(a)(5)(5.2.a),4(d)(1)");
					processedDto.setDataCategory("All other ITC & ITC reclaim");
				} else {
					processedDto.setTableNumberofGSTR3B("4(a)(5)(5.2.a)");
					processedDto.setDataCategory("All other ITC");
				}
			}
			if (!processedDto.getTableNumberofGSTR3B()
					.equalsIgnoreCase("4(d)(1)")) {
				processedList.add(processedDto);
			}
		}
		return processedList;
	}

	private Gstr3bdownloadProcessedDto convertProcessed(Object[] arr) {
		Gstr3bdownloadProcessedDto obj = new Gstr3bdownloadProcessedDto();

		obj.setTaxpayerGSTIN(arr[0] != null ? arr[0].toString() : null);
		// obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[1] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[1].toString())
				: null);
		obj.setSerialNo(arr[2] != null ? arr[2].toString() : null);
		obj.setDescription(arr[3] != null ? arr[3].toString() : null);
		obj.setiGSTAmount(arr[4] != null ? arr[4].toString() : null);
		obj.setcGSTAmount(arr[5] != null ? arr[5].toString() : null);
		obj.setsGSTAmount(arr[6] != null ? arr[6].toString() : null);
		obj.setCessAmount(arr[7] != null ? arr[7].toString() : null);
		obj.setDataCategory(arr[8] != null ? arr[8].toString() : null);
		String tableNo = arr[9] != null
				? (arr[9].toString().equalsIgnoreCase("4(a)(5)(d)")
						? "4(a)(5)(e)" : arr[9].toString())
				: null;
		obj.setTableNumberofGSTR3B(tableNo);
		obj.setRecordStatus(arr[10] != null ? arr[10].toString() : null);

		return obj;
	}

	private String createProcessedQueryString(String buildQuery) {
		return "SELECT GSTIN,RETURN_PERIOD,SERIAL_NO,"
				+ "DESCRIPTION,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "TABLE_DESCRIPTION,TABLE_SECTION ,CASE WHEN IS_DELETE = TRUE "
				+ "then 'INACTIVE' ELSE 'ACTIVE' END RECORD_STATUS "
				+ "FROM GSTR3B_ITC_AS_PROCESSED  WHERE " + buildQuery;

	}
}
