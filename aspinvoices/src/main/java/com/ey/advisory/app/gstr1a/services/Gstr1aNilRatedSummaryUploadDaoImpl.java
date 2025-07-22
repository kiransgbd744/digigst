package com.ey.advisory.app.gstr1a.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1AspVerticalNilDto;
import com.ey.advisory.app.data.views.client.NilNonProcessedRecordsDto;
import com.ey.advisory.app.services.reports.Gstr1OutwardVerticalProcessNilDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.search.SearchCriteria;


	@Component("Gstr1aNilRatedSummaryUploadDaoImpl")
	public class Gstr1aNilRatedSummaryUploadDaoImpl
			implements Gstr1OutwardVerticalProcessNilDao {

		private static final Logger LOGGER = LoggerFactory
				.getLogger(Gstr1aNilRatedSummaryUploadDaoImpl.class);

		@PersistenceContext(unitName = "clientDataUnit")
		private EntityManager entityManager;

		@Override
		public List<Object> getGstr1RSReports(SearchCriteria criteria) {
			Gstr1ReviwSummReportsReqDto request = (Gstr1ReviwSummReportsReqDto) criteria;

			String dataType = request.getDataType();
			String taxperiod = request.getTaxperiod();

			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

			
			String GSTIN = null;

			
			List<String> gstinList = null;

			if (!dataSecAttrs.isEmpty()) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						GSTIN = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
						}
					}

					
				}
			}

			StringBuilder buildQuery = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildQuery.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList");
				}
			}
			

			if (taxperiod != null && !taxperiod.isEmpty()) {
				buildQuery.append(" AND NEN.DERIVED_RET_PERIOD = :taxperiod ");
				/*buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");*/
			}

			String queryStr = createNilRatedTotalSummQueryString(
					buildQuery.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			if (taxperiod != null && !taxperiod.isEmpty()) {
				int derivedRetPeriod = GenUtil
						.convertTaxPeriodToInt(request.getTaxperiod());
				q.setParameter("taxperiod", derivedRetPeriod);
			}
			

			List<Object[]> list = q.getResultList();
			return list.parallelStream().map(o -> convertProcessedNil(o))
					.collect(Collectors.toCollection(ArrayList::new));
		}

		private NilNonProcessedRecordsDto convertProcessedNil(Object[] arr) {
			NilNonProcessedRecordsDto obj = new NilNonProcessedRecordsDto();

			obj.setgSTIN(arr[0] != null ? arr[0].toString() : null);
			obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
			obj.setHsn(arr[2] != null ? arr[2].toString() : null);
			obj.setDescription(arr[3] != null ? arr[3].toString() : null);
			obj.setUqc(arr[4] != null ? arr[4].toString() : null);
			obj.setQuantity(arr[5] != null ? arr[5].toString() : null);
			obj.setnILInterStateRegistered(
					arr[6] != null ? arr[6].toString() : null);
			obj.setnILIntraStateRegistered(
					arr[7] != null ? arr[7].toString() : null);
			obj.setnILInterStateUnRegistered(
					arr[8] != null ? arr[8].toString() : null);
			obj.setnILIntraStateUnRegistered(
					arr[9] != null ? arr[9].toString() : null);
			obj.setExtInterStateRegistered(
					arr[10] != null ? arr[10].toString() : null);
			obj.seteXTIntraStateRegistered(
					arr[11] != null ? arr[11].toString() : null);
			obj.seteXTInterStateUnRegistered(
					arr[12] != null ? arr[12].toString() : null);
			obj.seteXTIntraStateUnRegistered(
					arr[13] != null ? arr[13].toString() : null);
			obj.setnONInterStateRegistered(
					arr[14] != null ? arr[14].toString() : null);
			obj.setnONIntraStateRegistered(
					arr[15] != null ? arr[15].toString() : null);
			obj.setnONInterStateUnRegistered(
					arr[16] != null ? arr[16].toString() : null);
			obj.setnONIntraStateUnRegistered(
					arr[17] != null ? arr[17].toString() : null);
			obj.setaSPinformationCode(arr[18] != null ? arr[18].toString() : null);
			obj.setaSPinformationDescription(arr[19] != null ? arr[19].toString() : null);
			/*obj.setGSTR1Table(arr[20] != null ? arr[20].toString() : null);
			obj.setGSTR3BTable(arr[21] != null ? arr[21].toString() : null);*/
			obj.setSaveStatus(arr[20] != null ? arr[20].toString() : null);
			obj.setgSTNRefID(arr[21] != null ? arr[21].toString() : null);
			obj.setgSTNRefIDTime(arr[22] != null ? arr[22].toString() : null);
			obj.setgSTNErrorCode(arr[23] != null ? arr[23].toString() : null);
			obj.setgSTNErrorDescription(arr[24] != null ? arr[24].toString() : null);
			obj.setSourceID(arr[25] != null ? arr[25].toString() : null);
			obj.setFileName(arr[26] != null ? arr[26].toString() : null);
			obj.setaSPDateTime(arr[27] != null ? arr[27].toString() : null);

			return obj;
		}
		
		public static void main(String[] args) {
			StringBuilder buildQuery = new StringBuilder();

	
					buildQuery.append(" AND NEN.SUPPLIER_GSTIN IN :gstinList");
		
				buildQuery.append(" AND NEN.DERIVED_RET_PERIOD = :taxperiod ");
				/*buildQuery.append(" AND ITM.DERIVED_RET_PERIOD = :taxperiod ");*/
		
			System.out.println(createNilRatedTotalSummQueryString(buildQuery.toString()));;
		}

		private static String createNilRatedTotalSummQueryString(String buildQuery) {

			return "SELECT NEN.SUPPLIER_GSTIN, NEN.RETURN_PERIOD,"
					+ "ITM_HSNSAC HSNSAC, ITM_DESCRIPTION DESCRIPTION,"
					+ "ITM_UQC UQC,ITM_QTY QTY, NIL_INTERSTATE_REG,"
					+ "NIL_INTRASTATE_REG,NIL_INTERSTATE_UNREG,NIL_INTRASTATE_UNREG, "
					+ "EXT_INTERSTATE_REG,EXT_INTRASTATE_REG ,EXT_INTERSTATE_UNREG ,"
					+ "EXT_INTRASTATE_UNREG, NON_INTERSTATE_REG,"
					+ "NON_INTRASTATE_REG,NON_INTERSTATE_UNREG,NON_INTRASTATE_UNREG, "
					+ "TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') ) "
					+ "AS INFO_ERROR_CODE_ASP, "
					+ "TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) "
					+ "AS INFO_ERROR_DESCRIPTION_ASP, (CASE "
					+ "WHEN NEN.IS_SAVED_TO_GSTN = TRUE THEN 'IS_SAVED' "
					+ "WHEN NEN.IS_SAVED_TO_GSTN = FALSE AND NEN.GSTN_ERROR = TRUE "
					+ "THEN 'IS_ERROR' WHEN NEN.IS_SAVED_TO_GSTN = FALSE "
					+ "AND NEN.GSTN_ERROR = FALSE THEN 'NOT_SAVED' END) "
					+ "AS SAVE_STATUS, GSTNBATCH.GSTN_SAVE_REF_ID AS GSTIN_REF_ID, "
					+ "GSTNBATCH.BATCH_DATE AS GSTIN_REF_ID_TIME, "
					+ " '' AS GSTN_ERROR_CODE,'' AS GSTN_ERROR_DESCRIPTION, "
					+ "FIL.CREATED_BY AS SOURCE_ID,FIL.FILE_NAME AS FILE_NAME, "
					+ "NEN.CREATED_ON AS ASP_DATE_TIME FROM "
					+ "GSTR1A_PROCESSED_NILEXTNON NEN LEFT OUTER JOIN "
					+ " (SELECT COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE ,"
					+ "STRING_AGG(INFO_ERROR_CODE_ASP,',') INFO_ERROR_CODE_ASP ,"
					+ "STRING_AGG(INFO_ERROR_DESCRIPTION_ASP,',') "
					+ "INFO_ERROR_DESCRIPTION_ASP FROM ( SELECT COMMON_ID,VAL_TYPE,"
					+ "FILE_ID,INV_KEY,TABLE_TYPE, CASE WHEN ERROR_TYPE='INFO' "
					+ "THEN ERROR_CODE END AS INFO_ERROR_CODE_ASP, CASE "
					+ "WHEN ERROR_TYPE='INFO' THEN ERROR_DESCRIPTION END "
					+ "AS INFO_ERROR_DESCRIPTION_ASP FROM ANX_VERTICAL_ERROR) "
					+ " GROUP BY COMMON_ID, VAL_TYPE,FILE_ID,INV_KEY,TABLE_TYPE) GSTRERR ON "
					+ "NEN.AS_ENTERED_ID= GSTRERR.COMMON_ID "
					+ "AND NEN.FILE_ID=GSTRERR.FILE_ID "
					+ "AND GSTRERR.TABLE_TYPE='NILNONEXMPT' LEFT OUTER JOIN "
					+ "FILE_STATUS FIL  ON (NEN.FILE_ID=FIL.ID OR GSTRERR.FILE_ID=FIL.ID)  "
					+ " LEFT OUTER JOIN "
					+ "GSTR1_GSTN_SAVE_BATCH GSTNBATCH ON GSTNBATCH.ID = NEN.BATCH_ID "
					+ "WHERE NEN.IS_DELETE=FALSE "
					+ buildQuery ;
		}
	}

