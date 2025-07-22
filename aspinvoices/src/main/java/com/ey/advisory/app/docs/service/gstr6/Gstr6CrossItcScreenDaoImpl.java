package com.ey.advisory.app.docs.service.gstr6;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.Gstr6CrossItcRequestDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 * 
 */

@Component("Gstr6CrossItcScreenDaoImpl")
@Slf4j
public class Gstr6CrossItcScreenDaoImpl implements Gstr6CrossItcScreenDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("DefaultStateCache")
	private DefaultStateCache statecodeRepository;

	private static final String ACTIVE = "Active";
	private static final String IN_ACTIVE = "Inactive";

	@Override
	public SearchResult<Gstr6CrossItcRequestDto> gstr6CrossItcScreenDetails(
			Anx1ReportSearchReqDto criteria) {
		try {
			Anx1ReportSearchReqDto request = (Anx1ReportSearchReqDto) criteria;

			List<Long> entityId = request.getEntityId();
			String taxPeriod = request.getTaxPeriod();
			List<String> tableType = request.getTableType();
			Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

			String ProfitCenter = null;
			String plant = null;
			String sales = null;
			String division = null;
			String location = null;
			String distChannel = null;
			String ud1 = null;
			String ud2 = null;
			String ud3 = null;
			String ud4 = null;
			String ud5 = null;
			String ud6 = null;
			String GSTIN = null;

			List<String> pcList = null;
			List<String> plantList = null;
			List<String> divisionList = null;
			List<String> locationList = null;
			List<String> salesList = null;
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

					if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
						GSTIN = key;
						if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()
								&& dataSecAttrs.get(OnboardingConstant.GSTIN)
										.size() > 0) {
							gstinList = dataSecAttrs
									.get(OnboardingConstant.GSTIN);
						}
					}

				}
			}

			StringBuilder buildHeader = new StringBuilder();

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && gstinList.size() > 0) {
					buildHeader.append(" ISD_GSTIN IN :gstinList");

				}
			}
			

			if (taxPeriod != null) {
				if (GSTIN != null && !GSTIN.isEmpty()) {
					if (gstinList != null && gstinList.size() > 0) {
						buildHeader.append(" AND RETURN_PERIOD = :taxPeriod");
					}
				} else {
					buildHeader.append("  RETURN_PERIOD = :taxPeriod");
				}
			}
			if (tableType != null && !tableType.isEmpty()) {
				buildHeader.append(" AND DST.DOC_TYPE IN :tableType");
			}
			String queryStr = createApiProcessedQueryString(
					buildHeader.toString());
			Query q = entityManager.createNativeQuery(queryStr);

			if (GSTIN != null && !GSTIN.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}

			if (taxPeriod != null) {
				q.setParameter("taxPeriod", taxPeriod);
			}
			if (tableType != null && !tableType.isEmpty()) {
				q.setParameter("tableType", tableType);
			}
		

			List<Object[]> list = q.getResultList();

			if (list.isEmpty()) {
				gstinList = gstnDetailRepository
						.findgstinByEntityIdWithISD(entityId);
				for (String gstins : gstinList) {
					Object[] dummy = null;
					dummy = new Object[20];
					dummy[0] = gstins;
					dummy[1] = taxPeriod;
					// dummy[2] = N_I;
					dummy[4] = BigDecimal.ZERO;
					dummy[5] = BigDecimal.ZERO;
					dummy[6] = BigDecimal.ZERO;
					dummy[7] = BigDecimal.ZERO;
					dummy[8] = BigDecimal.ZERO;
					dummy[9] = BigDecimal.ZERO;
					dummy[10] = BigDecimal.ZERO;
					dummy[11] = BigDecimal.ZERO;
					dummy[12] = BigDecimal.ZERO;
					dummy[13] = BigDecimal.ZERO;
					dummy[14] = BigDecimal.ZERO;
					dummy[15] = BigDecimal.ZERO;
					dummy[16] = BigDecimal.ZERO;
					dummy[17] = BigDecimal.ZERO;
					dummy[18] = BigDecimal.ZERO;
					dummy[19] = BigDecimal.ZERO;
					list.add(dummy);
				}
			}
			List<Gstr6CrossItcRequestDto> verticalHsnList = Lists
					.newArrayList();
			if (CollectionUtils.isNotEmpty(list)) {
				for (Object arr[] : list) {
					verticalHsnList.add(createStatusConvertion(arr));
				}
			}

			return new SearchResult<>(verticalHsnList);

		} catch (Exception e) {
			LOGGER.debug("Exception Occur in Gstr6DeterminationDaoImpl", e);
			throw new AppException(e);

		}
	}

	private Gstr6CrossItcRequestDto createStatusConvertion(Object[] arr) {

		Gstr6CrossItcRequestDto obj = new Gstr6CrossItcRequestDto();
		String gstin = arr[0] != null ? arr[0].toString() : null;
		obj.setIsdGstin(gstin);
		obj.setTaxPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setDocKey(arr[2] != null && !arr[2].toString().isEmpty()
				? arr[2].toString() : null);
		// String status = arr[3] != null ? arr[3].toString() : null;
		Timestamp status = arr[3] != null && !arr[3].toString().isEmpty()
				? (java.sql.Timestamp) arr[3] : null;
		if (status != null && !status.toString().isEmpty()) {
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("yyyy-MM-dd HH:mm:ss");
			obj.setStatus(FOMATTER.format(
					EYDateUtil.toISTDateTimeFromUTC(status.toLocalDateTime())));
		}
		BigDecimal digiGstIgstIgst = BigDecimal.ZERO;
		if (arr[4] != null && !arr[4].toString().isEmpty()) {
			digiGstIgstIgst = new BigDecimal(arr[4].toString());
			obj.setDigigstIgstigst(digiGstIgstIgst);
		} else {
			obj.setDigigstIgstigst(digiGstIgstIgst);
		}
		BigDecimal digigstIgstCgst = BigDecimal.ZERO;
		if (arr[5] != null && !arr[5].toString().isEmpty()) {
			digigstIgstCgst = new BigDecimal(arr[5].toString());
			obj.setDigigstIgstCgst(digigstIgstCgst);
		} else {
			obj.setDigigstIgstCgst(digigstIgstCgst);
		}

		BigDecimal digigstIgstSgst = BigDecimal.ZERO;
		if (arr[6] != null && !arr[6].toString().isEmpty()) {
			digigstIgstSgst = new BigDecimal(arr[6].toString());
			obj.setDigigstIgstSgst(digigstIgstSgst);
		} else {
			obj.setDigigstIgstSgst(digigstIgstSgst);
		}

		BigDecimal digigstCgstCgst = BigDecimal.ZERO;
		if (arr[7] != null && !arr[7].toString().isEmpty()) {
			digigstCgstCgst = new BigDecimal(arr[7].toString());
			obj.setDigigstCgstCgst(digigstCgstCgst);
		} else {
			obj.setDigigstCgstCgst(digigstCgstCgst);
		}
		BigDecimal digigstCgstigst = BigDecimal.ZERO;
		if (arr[8] != null && !arr[8].toString().isEmpty()) {
			digigstCgstigst = new BigDecimal(arr[8].toString());
			obj.setDigigstCgstigst(digigstCgstigst);
		} else {
			obj.setDigigstCgstigst(digigstCgstigst);
		}

		BigDecimal digigstSgstSgst = BigDecimal.ZERO;
		if (arr[9] != null && !arr[9].toString().isEmpty()) {
			digigstSgstSgst = new BigDecimal(arr[9].toString());
			obj.setDigigstSgstSgst(digigstSgstSgst);
		} else {
			obj.setDigigstSgstSgst(digigstSgstSgst);
		}

		BigDecimal digiGstSgstIgst = BigDecimal.ZERO;
		if (arr[10] != null && !arr[10].toString().isEmpty()) {
			digiGstSgstIgst = new BigDecimal(arr[10].toString());
			obj.setDigigstSgstigst(digiGstSgstIgst);
		} else {
			obj.setDigigstSgstigst(digiGstSgstIgst);
		}

		BigDecimal digigstCesscess = BigDecimal.ZERO;
		if (arr[11] != null && !arr[11].toString().isEmpty()) {
			digigstCesscess = new BigDecimal(arr[11].toString());
			obj.setDigigstCesscess(digigstCesscess);
		} else {
			obj.setDigigstCesscess(digigstCesscess);
		}

		BigDecimal userIgstigst = BigDecimal.ZERO;
		if (arr[12] != null && !arr[12].toString().isEmpty()) {
			userIgstigst = new BigDecimal(arr[12].toString());
			obj.setUserIgstigst(userIgstigst);
		} else {
			obj.setUserIgstigst(userIgstigst);
		}

		BigDecimal userIgstCgst = BigDecimal.ZERO;
		if (arr[13] != null && !arr[13].toString().isEmpty()) {
			userIgstCgst = new BigDecimal(arr[13].toString());
			obj.setUserIgstCgst(userIgstCgst);
		} else {
			obj.setUserIgstCgst(userIgstCgst);
		}

		BigDecimal userIgstSgst = BigDecimal.ZERO;
		if (arr[14] != null && !arr[14].toString().isEmpty()) {
			userIgstSgst = new BigDecimal(arr[14].toString());
			obj.setUserIgstSgst(userIgstSgst);
		} else {
			obj.setUserIgstSgst(userIgstSgst);
		}
		BigDecimal userCgstCgst = BigDecimal.ZERO;
		if (arr[15] != null && !arr[15].toString().isEmpty()) {
			userCgstCgst = new BigDecimal(arr[15].toString());
			obj.setUserCgstCgst(userCgstCgst);
		} else {
			obj.setUserCgstCgst(userCgstCgst);
		}
		BigDecimal userCgstigst = BigDecimal.ZERO;
		if (arr[16] != null && !arr[16].toString().isEmpty()) {
			userCgstigst = new BigDecimal(arr[16].toString());
			obj.setUserCgstigst(userCgstigst);
		} else {
			obj.setUserCgstigst(userCgstigst);
		}

		BigDecimal userSgstSgst = BigDecimal.ZERO;
		if (arr[17] != null && !arr[17].toString().isEmpty()) {
			userSgstSgst = new BigDecimal(arr[17].toString());
			obj.setUserSgstSgst(userSgstSgst);
		} else {
			obj.setUserSgstSgst(userSgstSgst);
		}

		BigDecimal userSgstigst = BigDecimal.ZERO;
		if (arr[18] != null && !arr[18].toString().isEmpty()) {
			userSgstigst = new BigDecimal(arr[18].toString());
			obj.setUserSgstigst(userSgstigst);
		} else {
			obj.setUserSgstigst(userSgstigst);
		}

		BigDecimal userCesscess = BigDecimal.ZERO;
		if (arr[19] != null && !arr[19].toString().isEmpty()) {
			userCesscess = new BigDecimal(arr[19].toString());
			obj.setUserCesscess(userCesscess);
		} else {
			obj.setUserCesscess(userCesscess);
		}
		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(gstin);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				obj.setAuthToken(ACTIVE);
			} else {
				obj.setAuthToken(IN_ACTIVE);
			}
		} else {
			obj.setAuthToken(IN_ACTIVE);
		}
		String regName = gstnDetailRepository.findIsdRegTypeByGstin(gstin);
		String stateName = statecodeRepository
				.getStateName(gstin.substring(0, 2));
		obj.setState(stateName);
		if (regName != null && "isd".equalsIgnoreCase(regName)) {
			obj.setRegType(regName.toUpperCase());
		}
		return obj;
	}

	private String createApiProcessedQueryString(String buildQuery) {

		StringBuilder build = new StringBuilder();
		build.append(
				"SELECT ISD_GSTIN,RETURN_PERIOD,DOC_KEY,MAX(STATUS) STATUS");
		build.append(",SUM(DG_IAMTI) DG_IAMTI");
		build.append(",SUM(DG_IAMTC) DG_IAMTC");
		build.append(",SUM(DG_IAMTS) DG_IAMTS");
		build.append(",SUM(DG_CAMTC) DG_CAMTC");
		build.append(",SUM(DG_CAMTI) DG_CAMTI");
		build.append(",SUM(DG_SAMTS) DG_SAMTS");
		build.append(",SUM(DG_SAMTI) DG_SAMTI");
		build.append(",SUM(DG_CSAMT) DG_CSAMT");

		build.append(",SUM(UE_IAMTI) UE_IAMTI");
		build.append(",SUM(UE_IAMTC) UE_IAMTC");
		build.append(",SUM(UE_IAMTS) UE_IAMTS");
		build.append(",SUM(UE_CAMTC) UE_CAMTC");
		build.append(",SUM(UE_CAMTI) UE_CAMTI");
		build.append(",SUM(UE_SAMTS) UE_SAMTS");
		build.append(",SUM(UE_SAMTI) UE_SAMTI");
		build.append(",SUM(UE_CSAMT) UE_CSAMT ");
		build.append(" FROM( ");
		build.append(" SELECT GSTIN ISD_GSTIN,RETURN_PERIOD,");
		build.append("GSTIN||'|'||RETURN_PERIOD DOC_KEY,CREATED_ON STATUS, ");
		build.append("IFNULL(IAMTI,0) DG_IAMTI,");
		build.append("IFNULL(IAMTC,0) DG_IAMTC,");
		build.append("IFNULL(IAMTS,0) DG_IAMTS,");
		build.append("IFNULL(CAMTC,0) DG_CAMTC,");
		build.append("IFNULL(CAMTI,0) DG_CAMTI,");
		build.append("IFNULL(SAMTS,0) DG_SAMTS,");
		build.append("IFNULL(SAMTI,0) DG_SAMTI,");
		build.append("IFNULL(CESS,0) DG_CSAMT");

		build.append(",0 UE_IAMTI");
		build.append(",0 UE_IAMTC");
		build.append(",0 UE_IAMTS");
		build.append(",0 UE_CAMTC");
		build.append(",0 UE_CAMTI");
		build.append(",0 UE_SAMTS");
		build.append(",0 UE_SAMTI");
		build.append(",0 UE_CSAMT ");

		build.append("FROM GSTR6_CROSS_ITC_SAVE_COMPUTE ");
		build.append("WHERE IS_DELETE = FALSE ");
		build.append("AND IS_DIGI_GST_COMPUTE = TRUE ");
		build.append("AND IS_SAVED_TO_GSTN = TRUE ");

		build.append("UNION ALL ");
		build.append(
				"SELECT ISD_GSTIN,RETURN_PERIOD,ISD_GSTIN||'|'||RETURN_PERIOD DOC_KEY,NULL STATUS ");

		build.append(",0 DG_IAMTI ");
		build.append(",0 DG_IAMTC ");

		build.append(",0 DG_IAMTS ");
		build.append(",0 DG_CAMTC ");

		build.append(",0 DG_CAMTI ");
		build.append(",0 DG_SAMTS ");
		build.append(",0 DG_SAMTI ");
		build.append(",0 DG_CSAMT ");

		build.append(",IFNULL(IGST_USED_AS_IGST,0) UE_IAMTI ");
		build.append(",IFNULL(IGST_USED_AS_CGST,0) UE_IAMTC ");

		build.append(",IFNULL(IGST_USED_AS_SGST,0) UE_IAMTS ");
		build.append(",IFNULL(CGST_USED_AS_CGST,0) UE_CAMTC ");
		build.append(",IFNULL(CGST_USED_AS_IGST,0) UE_CAMTI ");
		build.append(",IFNULL(SGST_USED_AS_SGST,0) UE_SAMTS ");

		build.append(",IFNULL(SGST_USED_AS_IGST,0) UE_SAMTI ");
		build.append(",IFNULL(CESS_USED_AS_CESS,0) UE_CSAMT ");

		build.append("FROM CROSS_ITC_PROCESSED ");
		build.append(" WHERE IS_DELETE = FALSE  ) WHERE ");
		build.append(buildQuery);
		build.append("  GROUP BY ISD_GSTIN,RETURN_PERIOD,DOC_KEY");// ,STATUS

		return build.toString();
	}
}
