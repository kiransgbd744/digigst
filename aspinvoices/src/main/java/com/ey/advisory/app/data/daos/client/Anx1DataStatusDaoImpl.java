package com.ey.advisory.app.data.daos.client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Anx1DataStatusEntity;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Component("Anx1DataStatusDaoImpl")
public class Anx1DataStatusDaoImpl implements Anx1DataStatusDao {

	public static final String DATATYPE = "dataType";
	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1DataStatusDaoImpl.class);
	
	@Override
	public List<Anx1DataStatusEntity> dataAnx1StatusSection(String sectionType,
			DataStatusSearchReqDto req, String buildQuery, String dataType,
			Object dataRecvFrom, Object dataRecvTo) {

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
		String GSTIN = null;
		String plant = null;
		String sales = null;
		String division = null;
		String location = null;
		String purchase = null;
		String distChannel = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;

		List<String> pcList = null;
		List<String> gstinList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> purcList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (dataSecAttrs.get(OnboardingConstant.PC) != null
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (dataSecAttrs.get(OnboardingConstant.PLANT) != null
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					GSTIN = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (dataSecAttrs.get(OnboardingConstant.DIVISION) != null
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (dataSecAttrs.get(OnboardingConstant.LOCATION) != null
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (dataSecAttrs.get(OnboardingConstant.SO) != null
							&& dataSecAttrs.get(OnboardingConstant.SO)
									.size() > 0) {
						salesList = dataSecAttrs.get(OnboardingConstant.SO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (dataSecAttrs.get(OnboardingConstant.PO) != null
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purcList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					if (dataSecAttrs.get(OnboardingConstant.DC) != null
							&& dataSecAttrs.get(OnboardingConstant.DC)
									.size() > 0) {
						distList = dataSecAttrs.get(OnboardingConstant.DC);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}

		}

		String queryStr = createQueryString(sectionType, dataType, buildQuery);
		Query q = entityManager.createNativeQuery(queryStr);

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && !pcList.isEmpty() && pcList.size() > 0) {
				q.setParameter("pcList", pcList);
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && !plantList.isEmpty()
					&& plantList.size() > 0) {
				q.setParameter("plantList", plantList);
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && !salesList.isEmpty()
					&& salesList.size() > 0) {
				q.setParameter("salesList", salesList);
			}
		}
		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && !divisionList.isEmpty()
					&& divisionList.size() > 0) {
				q.setParameter("divisionList", divisionList);
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && !locationList.isEmpty()
					&& locationList.size() > 0) {
				q.setParameter("locationList", locationList);
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && !purcList.isEmpty()
					&& purcList.size() > 0) {
				q.setParameter("purcList", purcList);
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && !distList.isEmpty()
					&& distList.size() > 0) {
				q.setParameter("distList", distList);
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && !ud1List.isEmpty() && ud1List.size() > 0) {
				q.setParameter("ud1List", ud1List);
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && !ud2List.isEmpty() && ud2List.size() > 0) {
				q.setParameter("ud2List", ud2List);
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && !ud3List.isEmpty() && ud3List.size() > 0) {
				q.setParameter("ud3List", ud3List);
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && !ud4List.isEmpty() && ud4List.size() > 0) {
				q.setParameter("ud4List", ud4List);
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && !ud5List.isEmpty() && ud5List.size() > 0) {
				q.setParameter("ud5List", ud5List);
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && !ud6List.isEmpty() && ud6List.size() > 0) {
				q.setParameter("ud6List", ud6List);
			}
		}
		if ("DATA_RECEIVED".equals(sectionType)) {
			q.setParameter("recivedFromDate", dataRecvFrom);
			q.setParameter("recivedToDate", dataRecvTo);
		} else {

			int firstDerRetPeriod = GenUtil
					.convertTaxPeriodToInt(dataRecvFrom.toString());
			int secondDerRetPeriod = GenUtil
					.convertTaxPeriodToInt(dataRecvTo.toString());
			q.setParameter("retunPeriodFrom", firstDerRetPeriod);
			q.setParameter("retunPeriodTo", secondDerRetPeriod);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		LOGGER.debug("Converting ResultSet To List Inward and Outward Data");
		LOGGER.debug("Datastatus --> Before Converting ResultSet to "
				+ "Dto Data in list -----> "+list);
		List<Anx1DataStatusEntity> retList = list.parallelStream()
				.map(o -> convert(o, sectionType))
				.collect(Collectors.toCollection(ArrayList::new));
		LOGGER.debug("Datastatus --> After Converting ResultSet to "
				+ "Dto Data in list -----> "+retList);
		return retList;
	}

	private Anx1DataStatusEntity convert(Object[] arr, String sectionType) {

		Anx1DataStatusEntity obj = new Anx1DataStatusEntity();

		java.sql.Date sqlRecieveDate = (java.sql.Date) arr[0];

		if (sqlRecieveDate != null) {
			obj.setReceivedDate(sqlRecieveDate.toLocalDate());
		}
		if ((Integer) arr[1] != null) {
			obj.setSapTotal((Integer) arr[1]);
		}

		BigInteger b = GenUtil.getBigInteger(arr[2]);
		if (b != null) {
			int totalActRecords = b.intValue();
			obj.setTotalRecords(totalActRecords);
		}
		if ((Integer) arr[3] != null) {
			obj.setSapTotal((Integer) arr[3]);
		}

		BigInteger actProc = GenUtil.getBigInteger(arr[4]);
		if (actProc != null) {
			int actProcess = actProc.intValue();
			obj.setProcessedActive(actProcess);
		}

		BigInteger inActProc = GenUtil.getBigInteger(arr[5]);
		if (inActProc != null) {
			int inActProcess = inActProc.intValue();
			obj.setProcessedInactive(inActProcess);
		}

		BigInteger aErr = GenUtil.getBigInteger(arr[6]);
		if (aErr != null) {
			int actErrors = aErr.intValue();
			obj.setErrorActive(actErrors);
		}

		BigInteger inaErr = GenUtil.getBigInteger(arr[7]);
		if (inaErr != null) {
			int inactErrors = inaErr.intValue();
			obj.setErrorInactive(inactErrors);
		}

		BigInteger actInfo = GenUtil.getBigInteger(arr[8]);
		if (actInfo != null) {
			int actInformation = actInfo.intValue();
			obj.setInfoActive(actInformation);
		}
		BigInteger inActInfo = GenUtil.getBigInteger(arr[9]);
		if (inActInfo != null) {
			int inActInformation = inActInfo.intValue();
			obj.setInfoInactive(inActInformation);
		}
		return obj;
	}

	private String createQueryString(String sectionType, String dataType,
			String buildQuery) {
		// String viewName = "";
		String queryStr = null;

		if (dataType.equalsIgnoreCase(OUTWARD)) {
			LOGGER.debug("Executing  Outward Query BEGIN");
			queryStr = "SELECT HDR.RECEIVED_DATE AS RECEIVED_DATE,"
					+ "0 as SAPTOTAL,count(1) as TOTALRECORDS,"
					+ "0 as DIFFERENCE_INCOUNT,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND "
					+ "IS_DELETE = FALSE then 1 else NULL END) "
					+ "AS ACT_PROCESSED,COUNT(case when "
					+ "IS_PROCESSED = TRUE AND IS_DELETE = TRUE then "
					+ "1 else NULL END) AS INACT_PROCESSED,"
					+ "COUNT(case when IS_ERROR = TRUE AND "
					+ "IS_DELETE = FALSE then 1 else NULL END) AS ACT_ERRORS,"
					+ "COUNT(case when IS_ERROR = TRUE AND "
					+ "IS_DELETE = TRUE then 1 else NULL END) "
					+ "AS INACT_ERRORS,COUNT(case when "
					+ "IS_INFORMATION = TRUE AND IS_PROCESSED = "
					+ "TRUE AND IS_DELETE = FALSE then 1 else NULL END)AS "
					+ "ACT_INFORMATION,COUNT(case when "
					+ "IS_INFORMATION = TRUE AND IS_PROCESSED = TRUE AND "
					+ "IS_DELETE = TRUE then 1 else NULL END) "
					+ "as INACT_INFORMATION FROM ANX_OUTWARD_DOC_HEADER hdr "
					+ "WHERE HDR.DATAORIGINTYPECODE IN ('A','AI') AND " 
					+ buildQuery
					+ " GROUP BY HDR.RECEIVED_DATE ORDER BY "
					+ "HDR.RECEIVED_DATE DESC";
			LOGGER.debug("Datastatus OutWard--> Executed Query  -----> "+queryStr);
			LOGGER.debug("Executing  Outward Query END");
		} else {
			LOGGER.debug("INWARD Query Executing BEGIN");
			queryStr = "SELECT HDR.RECEIVED_DATE AS RECEIVED_DATE,"
					+ "0 as SAPTOTAL,count(1) as TOTALRECORDS,"
					+ "0 as DIFFERENCE_INCOUNT,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_PROCESSED,"
					+ "COUNT(case when IS_PROCESSED = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_PROCESSED,"
					+ "COUNT(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "FALSE then 1 else NULL END) AS ACT_ERRORS,"
					+ "COUNT(case when IS_ERROR = TRUE AND IS_DELETE = "
					+ "TRUE then 1 else NULL END) AS INACT_ERRORS,"
					+ "COUNT(case when IS_INFORMATION = TRUE AND IS_PROCESSED ="
					+ " TRUE AND IS_DELETE = FALSE then 1 else NULL END) "
					+ "AS ACT_INFORMATION,COUNT(case when IS_INFORMATION = "
					+ "TRUE AND IS_PROCESSED = TRUE AND IS_DELETE = TRUE then "
					+ "1 else NULL END) as INACT_INFORMATION "
					+ "FROM ANX_INWARD_DOC_HEADER hdr "
					+ "WHERE HDR.DATAORIGINTYPECODE IN ('A','AI') AND " 
					+ buildQuery
					+ " GROUP BY HDR.RECEIVED_DATE ORDER BY "
					+ "HDR.RECEIVED_DATE DESC";
			LOGGER.debug("Datastatus InWard--> Executed Query  -----> "+queryStr);
			LOGGER.debug("INWARD Query Executing END");
		}
		return queryStr;
	}
}
