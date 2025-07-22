/**
 * 
 */
package com.ey.advisory.app.data.services.Gstr1A;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.NatureOfDocEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.app.data.views.client.Gstr1GstnInvoiceSeriesTableDto;
import com.ey.advisory.app.docs.dto.GstnConsolidatedReqDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 * 
 */

@Component("Gstr1AGetInvSeriesTablesDaoImpl")
@Slf4j
public class Gstr1AGetInvSeriesTablesDaoImpl implements Gstr1AaGetDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("batchSaveStatusRepository") private Gstr1BatchRepository
	 * gstr1BatchRepository;
	 */

	static Integer cutoffPeriod = null;
	private static final String OLDFARMATTER = "yyyy-MM-dd";
	private static final String NEWFARMATTER = "dd-MM-yyyy";

	@Override
	public List<Object> getGstnConsolidatedReports(SearchCriteria criteria) {
		GstnConsolidatedReqDto request = (GstnConsolidatedReqDto) criteria;
		String taxPeriod = request.getTaxPeriod();

		List<String> gstr1aSections = request.getGstr1aSections();
		String gstin = request.getGstin();

		StringBuilder buildQuery = new StringBuilder();

		if (StringUtils.isNotBlank(gstin)) {
			buildQuery.append(" WHERE GSTIN IN :gstin");
		}

		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			buildQuery.append(" AND TABLE_TYPE IN :gstr1aSections");
		}

		if (taxPeriod != null) {
			buildQuery.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
		}

		String queryStr = creategstnInvQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (StringUtils.isNotBlank(gstin)) {
			q.setParameter("gstin", Lists.newArrayList(gstin));
		}
		if (taxPeriod != null) {
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(request.getTaxPeriod());
			q.setParameter("taxPeriod", derivedRetPeriod);

		}
		if (CollectionUtils.isNotEmpty(gstr1aSections)) {
			q.setParameter("gstr1aSections", gstr1aSections);
		}

		List<Object[]> list = q.getResultList();
		List<Object> verticalHsnList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<NatureOfDocEntity> hsnOrSacMasterEntities = natureDocTypeRepo
					.findAll();
			Map<Long, String> hsnMap = new HashMap<Long, String>();
			hsnOrSacMasterEntities.forEach(entity -> {
				hsnMap.put(entity.getId(), entity.getNatureDocType());
			});

			for (Object arr[] : list) {
				verticalHsnList.add(convertgstnInvoiceSeries(arr, hsnMap));
			}
		}
		return verticalHsnList;
	}

	private Gstr1GstnInvoiceSeriesTableDto convertgstnInvoiceSeries(
			Object[] arr, Map<Long, String> hsnMap) {
		Gstr1GstnInvoiceSeriesTableDto obj = new Gstr1GstnInvoiceSeriesTableDto();

		obj.setSupplierGSTIN(arr[1] != null ? arr[1].toString() : null);
		obj.setReturnPeriod(arr[2] != null ? arr[2].toString() : null);
		String doctype = null;
		Long serialno = (arr[3] != null ? Long.valueOf(arr[3].toString()) : null);

		if (serialno!=null) {
			doctype = serialno + "-" + hsnMap.get(serialno);
		}
		obj.setDocumentType(doctype);
		//obj.setDocumentType(arr[4] != null ? arr[4].toString() : null);
		obj.setFromserialNumber(arr[5] != null ? arr[5].toString() : null);
		obj.setToserialNumber(arr[6] != null ? arr[6].toString() : null);
		obj.setTotalNumber(arr[7] != null ? arr[7].toString() : null);
		obj.setCancelled(arr[8] != null ? arr[8].toString() : null);
		obj.setNetIssued(arr[9] != null ? arr[9].toString() : null);
		obj.setSerialNumber(arr[12] != null ? arr[12].toString() : null);

		return obj;
	}

	private String creategstnInvQueryString(String buildQuery) {

		return "select ID,GSTIN,RETURN_PERIOD,DOC_NUM,DOC_TYPE,"
				+ "FROM_SERIAL_NUM, TO_SERIAL_NUM,TOT_NUM,CANCEL,"
				+ "NET_ISSUE,DERIVED_RET_PERIOD, TABLE_TYPE,"
				+ "(ROW_NUMBER () OVER (ORDER BY GSTIN)) AS  SNO "
				+ "from (SELECT HDR.ID,GSTIN,RETURN_PERIOD, DOC_NUM,"
				+ " '' DOC_TYPE, FROM_SERIAL_NUM,TO_SERIAL_NUM,TOT_NUM,"
				+ "CANCEL,NET_ISSUE, HDR.DERIVED_RET_PERIOD,"
				+ " 'INV SERIES' AS TABLE_TYPE "
				+ "FROM GETGSTR1A_DOC_ISSUED HDR "
				+ "WHERE HDR.IS_DELETE=FALSE " + " GROUP BY "
				+ "HDR.ID,GSTIN,RETURN_PERIOD, DOC_NUM, "
				+ "FROM_SERIAL_NUM,TO_SERIAL_NUM,TOT_NUM,CANCEL,"
				+ "NET_ISSUE,HDR.DERIVED_RET_PERIOD)" + buildQuery;
	}
}
