package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsRequestDto;
import com.ey.advisory.app.services.ret1a.Ret1AProcessedRecordsResponseDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */

@Component("Ret1AProcessedRecordsDaoImpl")
public class Ret1AProcessedRecordsDaoImpl implements Ret1AProcessedRecordsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@SuppressWarnings("unchecked")
	@Override
	public List<Ret1AProcessedRecordsResponseDto> fetchRet1AProcessedRecords(
			List<String> gstins, Ret1AProcessedRecordsRequestDto dto) {
		List<String> criteriaList = buildQueryByCriteria(dto);
		String queryStr = buildRet1AQueryString(criteriaList);
		Query q = entityManager.createNativeQuery(queryStr);
		setParameterByCriteria(q, gstins, dto);
		List<Object[]> processedList = q.getResultList();
		return convertObjectIntoRecordsList(processedList);
	}

	private List<Ret1AProcessedRecordsResponseDto> convertObjectIntoRecordsList(
			List<Object[]> processedList) {
		List<Ret1AProcessedRecordsResponseDto> dtos = new ArrayList<>();
		if (processedList != null && processedList.size() > 0) {
			for (Object arr[] : processedList) {
				Ret1AProcessedRecordsResponseDto dto = new Ret1AProcessedRecordsResponseDto();

				String gstin = (String) arr[0];
				dto.setGstin(gstin);
				addStateAndAuthTokenData(dto, gstin);
				dto.setTimestamp(APIConstants.EMPTY);

				dto.setStatus((String) arr[2]);

				dto.setLiability(
						arr[4] == null ? BigDecimal.ZERO : (BigDecimal) arr[4]);
				dto.setRevCharge(
						arr[5] == null ? BigDecimal.ZERO : (BigDecimal) arr[5]);
				dto.setOtherCharge(
						arr[6] == null ? BigDecimal.ZERO : (BigDecimal) arr[6]);
				dto.setItc(
						arr[7] == null ? BigDecimal.ZERO : (BigDecimal) arr[7]);

				dtos.add(dto);
			}
		}
		return dtos;
	}

	public void addStateAndAuthTokenData(Ret1AProcessedRecordsResponseDto dto,
			String gstin) {
		String statecode = gstin.substring(0, 2);
		String statename = statecodeRepository.findStateNameByCode(statecode);
		if (statename == null) {
			dto.setState(APIConstants.EMPTY);
		} else {
			dto.setState(statename);
		}
		List<String> regName = gstnDetailRepository.findRegTypeByGstin(gstin);
		if (regName != null && regName.size() > 0) {
			String regTypeName = regName.get(0);
			if (regTypeName == null || regTypeName.equalsIgnoreCase("normal")
					|| regTypeName.equalsIgnoreCase("regular")) {
				dto.setRegType(APIConstants.EMPTY);
			} else {
				dto.setRegType(regTypeName.toUpperCase());
			}
		} else {
			dto.setRegType(APIConstants.EMPTY);
		}

		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(gstin);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				dto.setAuthToken("Active");
			} else {
				dto.setAuthToken("Inactive");
			}
		} else {
			dto.setAuthToken("Inactive");
		}

	}

	private void setParameterByCriteria(Query q, List<String> gstins,
			Ret1AProcessedRecordsRequestDto dto) {
		q.setParameter("gstins", gstins);
		if (dto.getTaxPeriod() != null
				&& !dto.getTaxPeriod().equals(APIConstants.EMPTY)) {
			q.setParameter("period",
					GenUtil.convertTaxPeriodToInt(dto.getTaxPeriod()));
		}

	}

	private String buildRet1AQueryString(List<String> criteriaList) {
		String query = "SELECT GSTIN,DERIVED_RET_PERIOD,STATUS,MAX(STATUS_DATE_TIME) STATUS_DATE_TIME,SUM(RET1A_TOTAL_TAX_LIABILITY) RET1A_TOTAL_TAX_LIABILITY, "
				+ "SUM(RET1A_REVERSECHARGE) RET1A_REVERSECHARGE, SUM(RET1A_OTHERTHAN_REVERSECHARGE) AS RET1A_OTHERTHAN_REVERSECHARGE, "
				+ "SUM(RET1A_NET_ITC_AVAILABLE) AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM( SELECT GSTIN,DERIVED_RET_PERIOD,STATUS,STATUS_DATE_TIME,RET1A_TOTAL_TAX_LIABILITY, 0 AS RET1A_REVERSECHARGE, "
				+ "0 AS RET1A_OTHERTHAN_REVERSECHARGE, 0 AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM ( SELECT SUPPLIER_GSTIN AS GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' "
				+ "END AS STATUS, MAX(MODIFIED_ON) AS STATUS_DATE_TIME, (IFNULL(SUM"
				+ "(CASE WHEN DOC_TYPE IN ('INV') AND AN_TABLE_SECTION IN ('3A','3C','3D') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+IFNULL(CESS_AMT_ADVALOREM,0) END),0) + "
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') AND AN_TABLE_SECTION IN ('3A','3C','3D') THEN "
				+ "IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') AND AN_TABLE_SECTION IN ('3A','3C','3D') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)) AS RET1A_TOTAL_TAX_LIABILITY "
				+ "FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3A','3C','3D') AND AN_RETURN_TYPE='ANX1A' ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(0) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "SUPPLIER_GSTIN, DERIVED_RET_PERIOD "
				+ "UNION ALL "
				+ "SELECT GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "MAX(MODIFIED_ON) AS STATUS_DATE_TIME, (CASE WHEN RETURN_TYPE IN ('3A4') THEN (IFNULL(SUM(IGST_AMT),0)+"
				+ "IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) - (CASE WHEN RETURN_TYPE IN ('3C1') "
				+ "THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) AS RET1A_TOTAL_TAX_LIABILITY "
				+ "FROM RET_PROCESSED_USERINPUT "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1A' AND RETURN_TABLE IN ('3A4','3C1') ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(1) : APIConstants.EMPTY);
		query = query + "GROUP BY "
				+ "GSTIN, DERIVED_RET_PERIOD,RETURN_TABLE,RETURN_TYPE "
				+ "UNION ALL "
				+ "SELECT CUST_GSTIN AS GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' "
				+ "END AS STATUS, MAX(MODIFIED_ON) AS STATUS_DATE_TIME, "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE='INV' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE='DR' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)) AS RET1A_TOTAL_TAX_LIABILITY "
				+ "FROM ANX_INWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I') AND AN_RETURN_TYPE='ANX1A' ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(2) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "CUST_GSTIN, DERIVED_RET_PERIOD) "
				+ "UNION ALL "
				+ "SELECT GSTIN,DERIVED_RET_PERIOD,STATUS,STATUS_DATE_TIME,0 AS RET1A_TOTAL_TAX_LIABILITY, RET1A_REVERSECHARGE, "
				+ "0 AS RET1A_OTHERTHAN_REVERSECHARGE, 0 AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM (SELECT CUST_GSTIN AS GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' END AS STATUS, MAX(MODIFIED_ON) AS STATUS_DATE_TIME, "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE='INV' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE='DR' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND AN_TABLE_SECTION IN ('3H','3I') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)) AS RET1A_REVERSECHARGE "
				+ "FROM ANX_INWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I') AND AN_RETURN_TYPE='ANX1A' ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(2) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "CUST_GSTIN, DERIVED_RET_PERIOD ) "
				+ "UNION ALL "
				+ "SELECT GSTIN,DERIVED_RET_PERIOD,STATUS,STATUS_DATE_TIME,0 AS RET1A_TOTAL_TAX_LIABILITY, 0 AS RET1A_REVERSECHARGE, "
				+ "RET1A_OTHERTHAN_REVERSECHARGE, 0 AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM (SELECT SUPPLIER_GSTIN AS GSTIN, DERIVED_RET_PERIOD, "
				+ "CASE WHEN(COUNT(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and "
				+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' END AS STATUS, MAX(MODIFIED_ON) AS STATUS_DATE_TIME, "
				+ "(IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') AND AN_TABLE_SECTION IN ('3A','3C','3D') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE IN ('DR') AND AN_TABLE_SECTION IN ('3A','3C','3D') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE IN ('CR') "
				+ "AND AN_TABLE_SECTION IN ('3A','3C','3D') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)) AS RET1A_OTHERTHAN_REVERSECHARGE "
				+ "FROM ANX_OUTWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE "
				+ "AND AN_TABLE_SECTION IN ('3A','3C','3D') AND AN_RETURN_TYPE='ANX1A' ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(0) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "SUPPLIER_GSTIN, DERIVED_RET_PERIOD "
				+ "UNION ALL "
				+ "SELECT GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' "
				+ "END AS STATUS, MAX(MODIFIED_ON) AS STATUS_DATE_TIME, "
				+ "(CASE WHEN RETURN_TABLE IN ('3A4') THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ "
				+ "IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) - (CASE WHEN RETURN_TABLE IN ('3C1') "
				+ "THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) AS RET1A_OTHERTHAN_REVERSECHARGE "
				+ "FROM RET_PROCESSED_USERINPUT "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1A' AND RETURN_TABLE IN ('3A4','3C1') ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(1) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "GSTIN, DERIVED_RET_PERIOD,RETURN_TABLE) "
				+ "UNION ALL "
				+ "SELECT GSTIN,DERIVED_RET_PERIOD,STATUS,STATUS_DATE_TIME,0 AS RET1A_TOTAL_TAX_LIABILITY, 0 AS RET1A_REVERSECHARGE, "
				+ "0 AS RET1A_OTHERTHAN_REVERSECHARGE, RET1A_NET_ITC_AVAILABLE "
				+ "FROM ( SELECT CUST_GSTIN AS GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "MAX(MODIFIED_ON) AS STATUS_DATE_TIME, (IFNULL(SUM(CASE WHEN DOC_TYPE='INV' AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) + IFNULL(SUM(CASE WHEN DOC_TYPE='DR' AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0) - IFNULL(SUM(CASE WHEN DOC_TYPE='CR' AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') "
				+ "THEN IFNULL(IGST_AMT,0)+IFNULL(CGST_AMT,0)+IFNULL(SGST_AMT,0)+IFNULL(CESS_AMT_SPECIFIC,0)+"
				+ "IFNULL(CESS_AMT_ADVALOREM,0) END),0)) AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM ANX_INWARD_DOC_HEADER "
				+ "WHERE IS_PROCESSED = TRUE AND IS_DELETE = FALSE AND AN_TABLE_SECTION IN ('3H','3I','3J','3K') AND AN_RETURN_TYPE='ANX1A' ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(2) : APIConstants.EMPTY);
		query = query + "GROUP BY " + "CUST_GSTIN, DERIVED_RET_PERIOD "
				+ "UNION ALL "
				+ "SELECT GSTIN, DERIVED_RET_PERIOD, CASE WHEN(COUNT"
				+ "(CASE WHEN IS_SENT_TO_GSTN=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0 and COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN=true AND IS_DELETE=FALSE THEN 1 ELSE NULL END) = 0) "
				+ "THEN 'NOT INTTIATED' "
				+ "WHEN (COUNT(CASE WHEN IS_DELETE=FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN GSTN_ERROR=TRUE AND IS_DELETE=FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'FAILED' "
				+ "WHEN (COUNT(CASE when IS_DELETE = FALSE THEN 1 ELSE NULL END) = COUNT"
				+ "(CASE WHEN IS_SAVED_TO_GSTN =TRUE AND IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'SAVED' "
				+ "WHEN (COUNT(CASE WHEN IS_SAVED_TO_GSTN=TRUE AND IS_DELETE = FALSE then 1 ELSE NULL END) < COUNT"
				+ "(CASE WHEN IS_DELETE = FALSE THEN 1 ELSE NULL END)) "
				+ "THEN 'PARTIALLY SAVED' END AS STATUS, "
				+ "MAX(MODIFIED_ON) AS STATUS_DATE_TIME, (CASE WHEN RETURN_TABLE IN ('4A5') "
				+ "THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+"
				+ "IFNULL(SUM(CESS_AMT),0)) END) - (CASE WHEN RETURN_TABLE IN ('4B1','4B2') "
				+ "THEN (IFNULL(SUM(IGST_AMT),0)+IFNULL(SUM(CGST_AMT),0)+ IFNULL(SUM(SGST_AMT),0)+IFNULL(SUM(CESS_AMT),0)) END) AS RET1A_NET_ITC_AVAILABLE "
				+ "FROM RET_PROCESSED_USERINPUT "
				+ "WHERE IS_DELETE = FALSE AND RETURN_TYPE='RET-1A' AND RETURN_TABLE IN ('4A5','4B1','4B2') ";
		query = query + (criteriaList != null && criteriaList.size() > 0
				? criteriaList.get(1) : APIConstants.EMPTY);
		query = query + "GROUP BY "
				+ "GSTIN, DERIVED_RET_PERIOD,RETURN_TABLE) ) " + "GROUP BY "
				+ "GSTIN,DERIVED_RET_PERIOD,STATUS";
		return query;

	}

	private List<String> buildQueryByCriteria(
			Ret1AProcessedRecordsRequestDto dto) {
		String supGstin = " AND SUPPLIER_GSTIN in :gstins ";
		String gstin = " AND GSTIN in :gstins ";
		String custGstin = " AND CUST_GSTIN in :gstins ";
		if (dto.getTaxPeriod() != null
				&& !dto.getTaxPeriod().equals(APIConstants.EMPTY)) {
			supGstin = supGstin + " AND DERIVED_RET_PERIOD = :period ";
			gstin = gstin + " AND DERIVED_RET_PERIOD = :period ";
			custGstin = custGstin + " AND DERIVED_RET_PERIOD = :period ";
		}
		List<String> criteriaList = Arrays.asList(supGstin, gstin, custGstin);
		return criteriaList;
	}

}
