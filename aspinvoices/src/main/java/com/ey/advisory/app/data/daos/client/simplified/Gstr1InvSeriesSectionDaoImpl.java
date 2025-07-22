package com.ey.advisory.app.data.daos.client.simplified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.app.caches.NatureOfDocCache;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1InvSeriesSectionDaoImpl")
public class Gstr1InvSeriesSectionDaoImpl implements Gstr1InvSeriesSectionDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr1InvSeriesSectionQueryBuilderImpl")
	private Gstr1InvSeriesSectionQueryBuilder queryBuilder;

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	@Autowired
	NatureOfDocCache natureOfDocCache;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1InvSeriesSectionDaoImpl.class);

	@Override
	public List<Gstr1VerticalDocSeriesRespDto> gstinViewSection(
			Annexure1SummaryReqDto req) {
		String taxPeriodReq = req.getTaxPeriod();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);
		Integer natureOfDocId = req.getDocNatureId();
		String returnType = req.getReturnType();
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		String gstin = null;
		List<String> gstinList = null;
		for (String key : dataSecAttrs.keySet()) {
			if (!dataSecAttrs.isEmpty()) {
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

		StringBuilder build = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND SUPPLIER_GSTIN IN :gstinList");
			}
		}
		if (taxPeriod != 0) {
			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}

		if (natureOfDocId != null && natureOfDocId != 0) {
			build.append(" AND SERIAL_NUM = :docNatureId ");
		}

		String buildQuery = build.toString();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Build Query For Invoice Series Vertical ---->",
					buildQuery);
		}
		String queryStr = null;
		if (returnType != null
				&& returnType.equalsIgnoreCase(APIConstants.GSTR1A))
			queryStr = createGstr1aGstinVerticalVeiwQuery(req, buildQuery);
		
			else
				queryStr = createGstr1GstinVerticalVeiwQuery(req, buildQuery);
			
			
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Invoice Vertical Query ---> " + queryStr);
		}
		List<Gstr1VerticalDocSeriesRespDto> retList = new ArrayList<>();
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}
			if (natureOfDocId != null && natureOfDocId != 0) {
				q.setParameter("docNatureId", natureOfDocId);
			}

			List<Object[]> list = q.getResultList();
			LOGGER.debug("ResultList data Converting to Dto");
			if (req.getDocNatureId() != null && req.getDocNatureId() != 0) {
				retList = list.parallelStream().map(o -> convertVerticalGstn(o))
						.collect(Collectors.toCollection(ArrayList::new));
			} else {
				retList = list.parallelStream().map(o -> convertGstn(o))
						.collect(Collectors.toCollection(ArrayList::new));
			}
			List<Gstr1VerticalDocSeriesRespDto> dtoList = new ArrayList<>();
			if (natureOfDocId == null) {
				Map<Long, Gstr1VerticalDocSeriesRespDto> map = new HashMap<>();
				for (Gstr1VerticalDocSeriesRespDto same : retList) {
					Long docNatureId = same.getDocNatureId();
					map.put(docNatureId, same);
				}
				for (Long i = Long.valueOf(1); i <= 12; i++) {
					if (map.containsKey(i)) {
						dtoList.add(map.get(i));

					} else {
						natureDocTypeRepo = StaticContextHolder.getBean(
								"NatureDocTypeRepo", NatureDocTypeRepo.class);
						NatureOfDocEntity doc = natureDocTypeRepo
								.findNatureDocType(i);
						Gstr1VerticalDocSeriesRespDto dto = new Gstr1VerticalDocSeriesRespDto();
						dto.setDocNatureId(i);
						dto.setDocNature(doc.getNatureDocType());
						dto.setNetIssued(String.valueOf(0));
						dto.setTotal(String.valueOf(0));
						dto.setCancelled(String.valueOf(0));
						dtoList.add(dto);
					}
				}
				return dtoList;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("While Fetching Advanced sections Date getting Error ",
					e);
		}
		return retList;
	}

	/**
	 * @param req
	 * @param buildQuery
	 * @return
	 */
	public String createGstr1GstinVerticalVeiwQuery(Annexure1SummaryReqDto req,
			String buildQuery) {
		String queryStr = null;
		if (req.getDocNatureId() != null && req.getDocNatureId() != 0) {
			queryStr = queryBuilder
					.createGstinVerticalViewQueryString(buildQuery);
		} else {
			queryStr = queryBuilder.createGstinViewQueryString(buildQuery);
		}
		return queryStr;
	}

	public String createGstr1aGstinVerticalVeiwQuery(Annexure1SummaryReqDto req,
			String buildQuery) {
		String queryStr = null;
		if (req.getDocNatureId() != null && req.getDocNatureId() != 0) {
			queryStr = queryBuilder
					.createGstr1aGstinVerticalViewQueryString(buildQuery);
		} else {
			queryStr = queryBuilder.createGstr1aGstinViewQueryString(buildQuery);
		}
		return queryStr;
	}

	private Gstr1VerticalDocSeriesRespDto convertVerticalGstn(Object[] arr) {
		Gstr1VerticalDocSeriesRespDto obj = new Gstr1VerticalDocSeriesRespDto();
		obj.setDocNatureId(
				Long.valueOf(arr[0] != null && !arr[0].toString().isEmpty()
						? arr[0].toString() : null));
		obj.setSeriesFrom(String.valueOf(arr[1]));
		obj.setSeriesTo(String.valueOf(arr[2]));
		obj.setTotal((arr[3] != null && !arr[3].toString().isEmpty()
				? String.valueOf(arr[3].toString()) : null));
		obj.setCancelled((arr[4] != null && !arr[4].toString().isEmpty()
				? String.valueOf(arr[4].toString()) : null));
		obj.setNetIssued((arr[5] != null && !arr[5].toString().isEmpty()
				? String.valueOf(arr[5].toString()) : null));
		obj.setId(Long.valueOf(arr[6] != null && !arr[6].toString().isEmpty()
				? arr[6].toString() : null));
		return obj;
	}

	private Gstr1VerticalDocSeriesRespDto convertGstn(Object[] arr) {
		Gstr1VerticalDocSeriesRespDto obj = new Gstr1VerticalDocSeriesRespDto();

		obj.setDocNatureId(
				Long.valueOf(arr[0] != null && !arr[0].toString().isEmpty()
						? arr[0].toString() : null));
		if (arr[0] != null) {
			String docNatureId = arr[0].toString();

			if (!docNatureId.isEmpty()) {
				int docNatureIntId = Integer.parseInt(docNatureId);
				NatureOfDocEntity docNature = natureOfDocCache
						.findNatureOfDoc(docNatureIntId);

				obj.setDocNature(docNature.getNatureDocType());
			}

		}

		obj.setTotal((arr[1] != null && !arr[1].toString().isEmpty()
				? String.valueOf(arr[1].toString()) : null));
		obj.setCancelled((arr[2] != null && !arr[2].toString().isEmpty()
				? String.valueOf(arr[2].toString()) : null));
		obj.setNetIssued((arr[3] != null && !arr[3].toString().isEmpty()
				? String.valueOf(arr[3].toString()) : null));
		return obj;
	}

}
