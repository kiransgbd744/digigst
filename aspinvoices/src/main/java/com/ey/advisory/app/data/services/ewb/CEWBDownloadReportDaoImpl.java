/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.CewbRepository;
import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportRequest;
import com.ey.advisory.app.docs.dto.einvoice.CEWBDownloadReportResponse;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("CEWBDownloadReportDaoImpl")
public class CEWBDownloadReportDaoImpl implements CEWBDownloadReportDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("CewbRepository")
	private CewbRepository cewbRepository;

	@Autowired
	@Qualifier("ConsolidateEWBNums")
	private ConsolidateEWBNums consolidateEWBNums;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");

	@Override
	public List<CEWBDownloadReportResponse> getCEWBCsvReports(
			SearchCriteria criteria) {
		CEWBDownloadReportRequest request = (CEWBDownloadReportRequest) criteria;

		String cewbNum = request.getCewbNum();
		LocalDate cewbDateFrom = request.getCewbFrom();
		LocalDate cewbDateTo = request.getCewbTo();
		String previousCewbNum = request.getPreviousCewbNo();

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

			}
		}
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				buildQuery.append(" AND GSTIN IN :gstinList");
			}
		}
		if (cewbDateFrom != null && cewbDateTo != null) {
			buildQuery.append(" AND CONSOLIDATED_EWB_DATE BETWEEN ");
			buildQuery.append("(:cewbDateFrom) AND (:cewbDateTo)");
		}

		if (cewbNum != null && !cewbNum.isEmpty()) {
			buildQuery.append(" AND CONSOLIDATED_EWB_NUM = :cewbNum");
		}

		/*
		 * if (previousCewbNum != null && !previousCewbNum.isEmpty()) {
		 * buildQuery.append(" AND HDR. IN :previousCewbNum"); }
		 */
		String queryStr = createQueryString(buildQuery.toString());

		Query q = entityManager.createNativeQuery(queryStr);

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (cewbDateFrom != null && cewbDateTo != null) {
			q.setParameter("cewbDateFrom", cewbDateFrom.atStartOfDay());
			q.setParameter("cewbDateTo", cewbDateTo.atTime(23, 59, 59));
		}

		if (cewbNum != null && !cewbNum.isEmpty()) {
			q.setParameter("cewbNum", cewbNum);
		}

		/*
		 * if (previousCewbNum != null && !previousCewbNum.isEmpty()) {
		 * q.setParameter("previousCewbNum", previousCewbNum); }
		 */

		List<Object[]> list = q.getResultList();
		return list.stream().map(o -> convertObj(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private CEWBDownloadReportResponse convertObj(Object[] arr) {
		CEWBDownloadReportResponse obj = new CEWBDownloadReportResponse();

		BigInteger SNo = GenUtil.getBigInteger(arr[0]);
		Long serialNo = SNo.longValue();
		obj.setCewbNum(arr[1] != null ? arr[1].toString() : null);
		if (arr[2] != null) {
			Timestamp timeStamp1 = (Timestamp) arr[2];

			LocalDateTime cewbTimeStmp = timeStamp1.toLocalDateTime();
			String dateTime = formatter1
					.format(EYDateUtil.toISTDateTimeFromUTC(cewbTimeStmp))
					.toString();

			obj.setCewbTimestamp(dateTime);
		} else {
			obj.setCewbTimestamp(null);
		}
		obj.setVehicleNo(arr[3] != null ? arr[3].toString() : null);
		obj.setTansportMode(arr[4] != null ? arr[4].toString() : null);
		obj.setGstin(arr[5] != null ? arr[5].toString() : null);
		String errorCode = arr[6] != null ? arr[6].toString() : null;
		obj.setErrorCode(errorCode);
		obj.setErrorDesc(arr[7] != null ? arr[7].toString() : null);
		// obj.setPreviousCewbNo(previousCewbNo);
		if (errorCode != null) {
			obj.setCewbStatus("ERROR");
			obj.setErrorPoint("CEWB");
		} else {
			obj.setCewbStatus("ACTIVE");
		}
		BigInteger filId = GenUtil.getBigInteger(arr[8]);
		Long fileId = filId.longValue();

		List<String> ewbNums = consolidateEWBNums.getCEWBNum(serialNo, fileId);
		Integer count = ewbNums.size();
		obj.setSerialNo(serialNo);
		obj.setFileId(fileId);
		// obj.setEwbNo(ewbNums);
		obj.setNoOfEwb(count);
		obj.setEwbNum(arr[9] != null
				? DownloadReportsConstant.CSVCHARACTER.concat(arr[9].toString())
				: null);
		obj.setFromState(arr[10] != null ? arr[10].toString() : null);
		obj.setFromPlace(arr[11] != null ? arr[11].toString() : null);
		obj.setTransDocNum(arr[12] != null ? arr[12].toString() : null);
		obj.setTransDocDate(arr[13] != null ? arr[13].toString() : null);
		obj.setPreviousCewbNo(null);

		return obj;
	}

	private String createQueryString(String buildQuery) {
		return "select SL_NO,CONSOLIDATED_EWB_NUM,CONSOLIDATED_EWB_DATE,"
				+ "VEHICLE_NO,TRANS_MODE, GSTIN,ERROR_CODE,ERROR_DESC,FILE_ID,"
				+ "EWB_NO,FROM_STATE,FROM_PLACE,TRANS_DOC_NUM,TRANS_DOC_DATE from "
				+ "CEWB_PROCESSED WHERE IS_DELETE = FALSE " + buildQuery
				+ " ORDER BY FILE_ID DESC";
	}

}
