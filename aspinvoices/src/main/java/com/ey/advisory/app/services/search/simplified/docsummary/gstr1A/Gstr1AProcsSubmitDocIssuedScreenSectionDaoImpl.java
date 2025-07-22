/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AProcsSubmitDocIssuedScreenSectionDaoImpl")
public class Gstr1AProcsSubmitDocIssuedScreenSectionDaoImpl
		implements BasicGstr1ADocIssuedSummaryScreenSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@Override
	public List<Gstr1SummaryDocSectionDto> loadBasicSummarySection(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodFrom1 = req.getTaxPeriodFrom();
		String taxPeriodTo1 = req.getTaxPeriodTo();

		int taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom1);
		int taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo1);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
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
		String gstin = null;

		List<String> pcList = null;
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
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
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
		StringBuilder build = new StringBuilder();
		StringBuilder buildForGet = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildForGet.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND PLANT_CODE IN :plantList");
			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND USERACCESS2 IN :ud2List");
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND USERACCESS4 IN :ud4List");
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND USERACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND USERACCESS6 IN :ud6List");

			}
		}
		if (StringUtils.isNotEmpty(taxPeriodFrom1)
				&& StringUtils.isNotEmpty(taxPeriodTo1)) {

			build.append(
					" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			buildForGet.append(
					" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

		}

		String buildQuery = build.toString().substring(4);
		String buildQueryGet = buildForGet.toString().substring(4);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Prepared where Condition and apply in DocIssued Query BEGIN");
		}

		String queryStr = createQueryString(buildQuery, buildQueryGet);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Executing DocIssued Query For Sections is -->" + queryStr);
		}

		try {
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
			if (gstin != null && !gstin.isEmpty()) {
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
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
			if (StringUtils.isNotEmpty(taxPeriodFrom1)
					&& StringUtils.isNotEmpty(taxPeriodTo1)) {
				q.setParameter("taxPeriodFrom", taxPeriodFrom);
				q.setParameter("taxPeriodTo", taxPeriodTo);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummaryDocSectionDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution DocIssued Query "
					+ "getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryDocSectionDto convert(Object[] arr) {
		Gstr1SummaryDocSectionDto obj = new Gstr1SummaryDocSectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		obj.setAspTotal(convertBigIntegerToInteger(arr[1]));
		obj.setAspCancelled(convertBigIntegerToInteger(arr[2]));
		obj.setAspNetIssued(convertBigIntegerToInteger(arr[3]));
		obj.setGstnTotal(convertBigIntegerToInteger(arr[4]));
		obj.setGstnCancelled(convertBigIntegerToInteger(arr[5]));
		obj.setGstnNetIssued(convertBigIntegerToInteger(arr[6]));

		return obj;
	}

	private Integer convertBigIntegerToInteger(Object obj) {
		if (obj instanceof BigInteger) {
			return ((BigInteger) obj).intValue();
		}
		return (Integer) obj; // assuming obj is already an Integer
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery, String buildQueryGet) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = "SELECT TAX_DOC_TYPE,SUM(ASP_TOT_NUM) ASP_TOT_NUM,"
				+ "SUM(ASP_CANCELED) ASP_CANCELED,SUM(ASP_NET_NUM) ASP_NET_NUM,"
				+ "SUM(GSTN_TOT_NUM) GSTN_TOT_NUM,SUM(GSTN_CANCELED) GSTN_CANCELED,"
				+ "SUM(GSTN_NET_NUM) GSTN_NET_NUM FROM ( "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ "TAX_DOC_TYPE,ASP_TOT_NUM,ASP_CANCELED,ASP_NET_NUM,0 GSTN_TOT_NUM,"
				+ "0 GSTN_CANCELED,0 GSTN_NET_NUM " + "FROM ( "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ "'DOC_ISSUED' TAX_DOC_TYPE,IFNULL(SUM(TOT_NUM),0) ASP_TOT_NUM,"
				+ "IFNULL(SUM(CANCELED),0) ASP_CANCELED,IFNULL(SUM(NET_NUM),0) ASP_NET_NUM "
				+ "FROM GSTR1A_PROCESSED_INV_SERIES "
				+ "WHERE IS_DELETE=FALSE AND " + buildQuery
				+ "GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD )"
				+ "UNION ALL "
				+ "SELECT SUPPLIER_GSTIN,RETURN_PERIOD,DERIVED_RET_PERIOD,"
				+ "TAX_DOC_TYPE,0 ASP_TOT_NUM,0 ASP_CANCELED,0 ASP_NET_NUM,"
				+ "GSTN_TOT_NUM, GSTN_CANCELED, GSTN_NET_NUM " + "FROM ( "
				+ "SELECT HDR.GSTIN AS SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "HDR.DERIVED_RET_PERIOD,"
				+ "'DOC_ISSUED' TAX_DOC_TYPE,IFNULL(SUM(TOT_NUM),0) GSTN_TOT_NUM,"
				+ "IFNULL(SUM(CANCEL),0) GSTN_CANCELED,IFNULL(SUM(NET_ISSUE),0) GSTN_NET_NUM "
				+ "FROM GETGSTR1A_DOC_ISSUED HDR "
				+ "LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "WHERE  HDR.IS_DELETE=FALSE AND BT.IS_DELETE=FALSE "
				+ "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD) WHERE "
				+ buildQueryGet + " ) GROUP BY  TAX_DOC_TYPE";

		LOGGER.debug("DocIssued Section Query Execution END ");
		return queryStr;
	}

	/**
	 * Fetching the Data For Nil Section From Query
	 */

	@Override
	public List<Gstr1SummaryNilSectionDto> loadBasicSummarySectionNil(
			Annexure1SummaryReqDto request) {
		// TODO Auto-generated method stub

		Annexure1SummaryReqDto req = basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(request);

		String taxPeriodFrom1 = req.getTaxPeriodFrom();
		String taxPeriodTo1 = req.getTaxPeriodTo();

		int taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom1);
		int taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo1);

		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();

		String ProfitCenter = null;
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
		String gstin = null;

		List<String> pcList = null;
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
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					if (!dataSecAttrs.get(OnboardingConstant.SO).isEmpty()
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
		StringBuilder build = new StringBuilder();

		StringBuilder buildHdr = new StringBuilder();
		// StringBuilder buildVertical = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
				buildHdr.append(" AND SUPPLIER_GSTIN IN :gstinList");
				// buildVertical.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				build.append(" AND HDR.PROFIT_CENTRE IN :pcList");
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				build.append(" AND HDR.PLANT_CODE IN :plantList");

			}
		}
		if (sales != null && !sales.isEmpty()) {
			if (salesList != null && salesList.size() > 0) {
				build.append(" AND HDR.SALES_ORGANIZATION IN :salesList");
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purcList != null && purcList.size() > 0) {
				build.append(" AND HDR.PURCHASE_ORGANIZATION IN :purcList");
			}
		}
		if (distChannel != null && !distChannel.isEmpty()) {
			if (distList != null && distList.size() > 0) {
				build.append(" AND HDR.DISTRIBUTION_CHANNEL IN :distList");
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				build.append(" AND HDR.DIVISION IN :divisionList");
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				build.append(" AND HDR.LOCATION IN :locationList");
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				build.append(" AND HDR.USERACCESS1 IN :ud1List");
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				build.append(" AND HDR.USERACCESS2 IN :ud2List");

			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				build.append(" AND HDR.USERACCESS3 IN :ud3List");
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				build.append(" AND HDR.USERACCESS4 IN :ud4List");

			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				build.append(" AND HDR.USERACCESS5 IN :ud5List");

			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				build.append(" AND HDR.USERACCESS6 IN :ud6List");

			}
		}
		String multiSupplyTypeAns = groupConfigPrmtRepository
				.findAnswerForMultiSupplyType();
		if (StringUtils.isNotEmpty(taxPeriodFrom1)
				&& StringUtils.isNotEmpty(taxPeriodTo1)) {

			build.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

			buildHdr.append(
					" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");

		}

		String buildQuery = build.toString().substring(4);
		String buildQueryForGet = buildHdr.toString().substring(4);
		// String buildQueryVert = buildVertical.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Prepared where Condition and apply in "
					+ "Nil,Non Exempted Query BEGIN");
		}

		String queryStr = createQueryStringNil(buildQuery, buildQueryForGet,
				multiSupplyTypeAns);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Nil,Non Exempted Query For Sections is -->"
					+ queryStr);
		}

		try {
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
			if (gstin != null && !gstin.isEmpty()) {
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
				if (ud1List != null && !ud1List.isEmpty()
						&& ud1List.size() > 0) {
					q.setParameter("ud1List", ud1List);
				}
			}
			if (ud2 != null && !ud2.isEmpty()) {
				if (ud2List != null && !ud2List.isEmpty()
						&& ud2List.size() > 0) {
					q.setParameter("ud2List", ud2List);
				}
			}
			if (ud3 != null && !ud3.isEmpty()) {
				if (ud3List != null && !ud3List.isEmpty()
						&& ud3List.size() > 0) {
					q.setParameter("ud3List", ud3List);
				}
			}
			if (ud4 != null && !ud4.isEmpty()) {
				if (ud4List != null && !ud4List.isEmpty()
						&& ud4List.size() > 0) {
					q.setParameter("ud4List", ud4List);
				}
			}
			if (ud5 != null && !ud5.isEmpty()) {
				if (ud5List != null && !ud5List.isEmpty()
						&& ud5List.size() > 0) {
					q.setParameter("ud5List", ud5List);
				}
			}
			if (ud6 != null && !ud6.isEmpty()) {
				if (ud6List != null && !ud6List.isEmpty()
						&& ud6List.size() > 0) {
					q.setParameter("ud6List", ud6List);
				}
			}
			if (StringUtils.isNotEmpty(taxPeriodFrom1)
					&& StringUtils.isNotEmpty(taxPeriodTo1)) {
				q.setParameter("taxPeriodFrom", taxPeriodFrom);
				q.setParameter("taxPeriodTo", taxPeriodTo);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			List<Gstr1SummaryNilSectionDto> retList = list.parallelStream()
					.map(o -> convertNilSection(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("After Execution Nil,Exempted Query "
					+ "getting the data ----->" + retList);
			return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Getting error Executing NILNoNEXMPT Query ", e);
			throw new AppException("Unexpected error in query execution.", e);
		}
	}

	private Gstr1SummaryNilSectionDto convertNilSection(Object[] arr) {
		Gstr1SummaryNilSectionDto obj = new Gstr1SummaryNilSectionDto();
		LOGGER.debug("Array data Setting to Dto");
		obj.setTaxDocType((String) arr[0]);
		// obj.setDocType((String) arr[0]);
		obj.setAspNitRated((BigDecimal) arr[1]);
		obj.setAspExempted((BigDecimal) arr[2]);
		obj.setAspNonGst((BigDecimal) arr[3]);
		obj.setGstnNitRated((BigDecimal) arr[5]);
		obj.setGstnExempted((BigDecimal) arr[6]);
		obj.setGstnNonGst((BigDecimal) arr[7]);

		return obj;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	public static void main(String[] args) {

		StringBuilder build = new StringBuilder();

		StringBuilder buildHdr = new StringBuilder();

		build.append(" AND SUPPLIER_GSTIN IN ('33GSPTN0482G1Z9')");
		buildHdr.append(" AND SUPPLIER_GSTIN IN  ('33GSPTN0482G1Z9')");

		String multiSupplyTypeAns = "A";

		build.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN  202401 AND 202408 ");

		buildHdr.append(" AND DERIVED_RET_PERIOD BETWEEN  202401 AND 202408 ");

		String buildQuery = build.toString().substring(4);
		String buildQueryForGet = buildHdr.toString().substring(4);
		String queryStr = createQueryStringNil(buildQuery, buildQueryForGet,
				multiSupplyTypeAns);
		System.out.println(queryStr);
	}

	private static String createQueryStringNil(String buildQuery,
			String buildQueryForGet, String multiSupplyTypeAns) {
		// TODO Auto-generated method stub
		LOGGER.debug("Outward Query Execution BEGIN ");

		String queryStr = null;

		queryStr = "SELECT TAX_DOC_TYPE "
				+ "	,SUM(ASP_NIL_AMT) AS ASP_NIL_AMT "
				+ "	,SUM(ASP_EXPT_AMT) AS ASP_EXPT_AMT "
				+ "	,SUM(ASP_NONGST_SUP_AMT) AS ASP_NONGST_SUP_AMT "
				+ "	,SUM(GSTN_COUNT) GSTN_COUNT "
				+ "	,SUM(GSTN_NIL_AMT) AS GSTN_NIL_AMT "
				+ "	,SUM(GSTN_EXPT_AMT) AS GSTN_EXPT_AMT "
				+ "	,SUM(GSTN_NONGST_SUP_AMT) AS GSTN_NONGST_SUP_AMT "
				+ "FROM ( " + "	SELECT * " + "	FROM ( "
				+ "		SELECT SUPPLIER_GSTIN " + "			,RETURN_PERIOD "
				+ "			,DERIVED_RET_PERIOD " + "			,TAX_DOC_TYPE "
				+ "			,ASP_COUNT " + "			,ASP_NIL_AMT "
				+ "			,ASP_EXPT_AMT " + "			,ASP_NONGST_SUP_AMT "
				+ "			,0 GSTN_COUNT " + "			,0 GSTN_NIL_AMT "
				+ "			,0 GSTN_EXPT_AMT "
				+ "			,0 GSTN_NONGST_SUP_AMT " + "		FROM ( "
				+ "			SELECT SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TAX_DOC_TYPE "
				+ "				,SUM(ASP_COUNT) ASP_COUNT "
				+ "				,SUM(NIL_RATED_SUPPLIES) AS ASP_NIL_AMT "
				+ "				,SUM(EXMPTED_SUPPLIES) AS ASP_EXPT_AMT "
				+ "				,SUM(NON_GST_SUPPLIES) AS ASP_NONGST_SUP_AMT "
				+ "			FROM ( " + "				SELECT SUPPLIER_GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,'NILEXTNON' AS TAX_DOC_TYPE "
				+ "					,0 ASP_COUNT " + "					,( "
				+ "						CASE "
				+ "							WHEN SUPPLY_TYPE = 'NIL' "
				+ "								THEN IFNULL(SUM(TAXABLE_VALUE), 0) "
				+ "							END "
				+ "						) NIL_RATED_SUPPLIES "
				+ "					,( " + "						CASE "
				+ "							WHEN SUPPLY_TYPE = 'EXT' "
				+ "								THEN IFNULL(SUM(TAXABLE_VALUE), 0) "
				+ "							END "
				+ "						) AS EXMPTED_SUPPLIES "
				+ "					,( " + "						CASE "
				+ "							WHEN SUPPLY_TYPE IN ('NON') "
				+ "								THEN IFNULL(SUM(TAXABLE_VALUE), 0) "
				+ "							END "
				+ "						) NON_GST_SUPPLIES "
				+ "				FROM GSTR1A_SUMMARY_NILEXTNON "
				+ "				WHERE IS_DELETE = FALSE "
				+ "				GROUP BY SUPPLIER_GSTIN "
				+ "					,RETURN_PERIOD "
				+ "					,DERIVED_RET_PERIOD "
				+ "					,TAXABLE_VALUE "
				+ "					,SUPPLY_TYPE " + "				 "
				+ "				UNION ALL " + "				 "
				+ "				SELECT SUPPLIER_GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "					,'NILEXTNON' AS TAX_DOC_TYPE "
				+ "					,0 ASP_COUNT "
				+ "					,SUM(IFNULL((CASE WHEN ITM.SUPPLY_TYPE ='NIL' "
				+ "		 THEN (CASE WHEN DOC_TYPE IN ('C') AND ITM.SUPPLY_TYPE ='NIL'THEN -1*ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
				+ "		 END),0)) AS NIL_RATED_SUPPLIES "
				+ "		 ,SUM(IFNULL((CASE WHEN ITM.SUPPLY_TYPE ='EXT' "
				+ "		 THEN (CASE WHEN DOC_TYPE IN ('C') AND ITM.SUPPLY_TYPE ='EXT'THEN -1*ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
				+ "		 END),0)) AS EXMPTED_SUPPLIES "
				+ "		  ,SUM(IFNULL((CASE WHEN ITM.SUPPLY_TYPE ='NON' "
				+ "		 THEN (CASE WHEN DOC_TYPE IN ('C') AND ITM.SUPPLY_TYPE ='NON'THEN -1*ITM.TAXABLE_VALUE ELSE ITM.TAXABLE_VALUE END) "
				+ "		 END),0)) AS NON_GST_SUPPLIES "
				+ "				FROM ANX_OUTWARD_DOC_HEADER_1A HDR "
				+ "				INNER JOIN ANX_OUTWARD_DOC_ITEM_1A ITM ON HDR.ID = ITM.DOC_HEADER_ID "
				+ "					AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "				WHERE HDR.IS_DELETE = FALSE "
				+ "					AND HDR.TAX_DOC_TYPE IN ('NILEXTNON') "
				+ "					AND ASP_INVOICE_STATUS = 2 "
				+ "					AND COMPLIANCE_APPLICABLE = TRUE "
				+ "					AND IS_DELETE = FALSE "
				+ "					AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ "					AND RETURN_TYPE = 'GSTR1A' AND " + buildQuery
				+ "				GROUP BY SUPPLIER_GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				) " + "			GROUP BY SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TAX_DOC_TYPE " + "			) " + "		 "
				+ "		UNION ALL " + "		 " + "		SELECT SUPPLIER_GSTIN "
				+ "			,RETURN_PERIOD "
				+ "			,DERIVED_RET_PERIOD " + "			,TAX_DOC_TYPE "
				+ "			,0 ASP_COUNT " + "			,0 ASP_NIL_AMT "
				+ "			,0 ASP_EXPT_AMT "
				+ "			,0 ASP_NONGST_SUP_AMT " + "			,GSTN_COUNT "
				+ "			,GSTN_NIL_AMT " + "			,GSTN_EXPT_AMT "
				+ "			,GSTN_NONGST_SUP_AMT " + "		FROM ( "
				+ "			SELECT SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TAX_DOC_TYPE "
				+ "				,SUM(GSTN_COUNT) GSTN_COUNT "
				+ "				,SUM(NIL_AMT) AS GSTN_NIL_AMT "
				+ "				,SUM(EXPT_AMT) AS GSTN_EXPT_AMT "
				+ "				,SUM(NONGST_SUP_AMT) AS GSTN_NONGST_SUP_AMT "
				+ "			FROM ( "
				+ "				SELECT HDR.GSTIN AS SUPPLIER_GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "					,'NILEXTNON' AS TAX_DOC_TYPE "
				+ "					,0 GSTN_COUNT "
				+ "					,IFNULL(SUM(NIL_AMT), 0) NIL_AMT "
				+ "					,IFNULL(SUM(EXPT_AMT), 0) EXPT_AMT "
				+ "					,IFNULL(SUM(NONGST_SUP_AMT), 0) NONGST_SUP_AMT "
				+ "				FROM GETGSTR1A_NILEXTNON HDR "
				+ "				LEFT JOIN GETANX1_BATCH_TABLE BT ON HDR.BATCH_ID = BT.ID "
				+ "				WHERE HDR.IS_DELETE = FALSE "
				+ "					AND BT.IS_DELETE = FALSE "
				+ "				GROUP BY HDR.GSTIN "
				+ "					,HDR.RETURN_PERIOD "
				+ "					,HDR.DERIVED_RET_PERIOD "
				+ "				) " + "			GROUP BY SUPPLIER_GSTIN "
				+ "				,RETURN_PERIOD "
				+ "				,DERIVED_RET_PERIOD "
				+ "				,TAX_DOC_TYPE " + "			) " + "		) "
				+ "	WHERE " + buildQueryForGet + "	) "
				+ "GROUP BY TAX_DOC_TYPE";

		LOGGER.debug("Nil Section Query Execution END ");
		return queryStr;
	}

}
