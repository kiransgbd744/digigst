package com.ey.advisory.app.anx1.taxamountrecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 **/

@Component("TaxAmountReconDaoImpl")
@Slf4j
public class TaxAmountReconDaoImpl implements TaxAmountReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<TaxAmountReconDto> getAllTaxAmountReconDetails(
			TaxAmountReconRequestDto requestDto) {

		String queryString = createQueryString(requestDto);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Query for tax recon : %s",queryString);
			LOGGER.debug(msg);
		}

		Query q = entityManager.createNativeQuery(queryString);
		setParams(q, requestDto);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Result set for tax recon : %s",
					list.toString());
			LOGGER.debug(msg);
		}
		
		List<TaxAmountReconDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("List for Tax amount : %s",retList);
			LOGGER.debug(msg);
		}

		return retList;
	}

	private void setParams(Query q, TaxAmountReconRequestDto requestDto) {

		q.setParameter("taxPeriod", requestDto.getTaxPeriod());
		q.setParameter("sgstin", requestDto.getGstins());

		if (!(requestDto.getStartDocDate() == null
				&& requestDto.getEndDocDate() == null)) {
			q.setParameter("startDocDate", requestDto.getStartDocDate());
			q.setParameter("docEndDate", requestDto.getEndDocDate());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getProfitCentres()))) {
			q.setParameter("profitCenters", requestDto.getProfitCentres());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getPlants()))) {
			q.setParameter("plants", requestDto.getPlants());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getDivisions()))) {
			q.setParameter("divisions", requestDto.getDivisions());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getLocations()))) {
			q.setParameter("locations", requestDto.getLocations());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getSalesOrgs()))) {
			q.setParameter("salesOrganization", requestDto.getSalesOrgs());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getPurchaseOrgs()))) {
			q.setParameter("purchaseOrganization",
					requestDto.getPurchaseOrgs());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getDistributionChannels()))) {
			q.setParameter("distributionChannels",
					requestDto.getDistributionChannels());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess1()))) {
			q.setParameter("userAccess1", requestDto.getUserAccess1());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess2()))) {
			q.setParameter("userAccess2", requestDto.getUserAccess2());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess3()))) {
			q.setParameter("userAccess3", requestDto.getUserAccess3());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess4()))) {
			q.setParameter("userAccess4", requestDto.getUserAccess4());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess5()))) {
			q.setParameter("userAccess5", requestDto.getUserAccess5());
		}

		if (!(CollectionUtils.isEmpty(requestDto.getUserAccess6()))) {
			q.setParameter("userAccess6", requestDto.getUserAccess6());
		}

	}

	private TaxAmountReconDto convert(Object[] arr) {

		TaxAmountReconDto obj = new TaxAmountReconDto();
		obj.setGstin(arr[0] == null ? "" : (String) arr[0]);
		obj.setUpdIgstAmt(
				arr[1] == null ? BigDecimal.ZERO : (BigDecimal) arr[1]);
		obj.setUpdCgstAmt(
				arr[2] == null ? BigDecimal.ZERO : (BigDecimal) arr[2]);
		obj.setUpdSgstAmt(
				arr[3] == null ? BigDecimal.ZERO : (BigDecimal) arr[3]);
		obj.setMemoIgstAmt(
				arr[4] == null ? BigDecimal.ZERO : (BigDecimal) arr[4]);
		obj.setMemoCgstAmt(
				arr[5] == null ? BigDecimal.ZERO : (BigDecimal) arr[5]);
		obj.setMemoSgstAmt(
				arr[6] == null ? BigDecimal.ZERO : (BigDecimal) arr[6]);

		return obj;
	}

	private String createQueryString(TaxAmountReconRequestDto reqDto) {

		String condition = queryCondition(reqDto);
		String strQuery = "select \"SUPPLIER_GSTIN\" as \"SGSTIN\","
				+ "sum(\"IGST_AMT\") as \"UPLOADED_IGST_AMT\","
				+ "sum(\"CGST_AMT\") as \"UPLOADED_CGST_AMT\","
				+ "sum(\"SGST_AMT\") as \"UPLOADED_SGST_AMT\","
				+ "sum(\"MEMO_VALUE_IGST\") as \"MEMO_IGST_AMT\","
				+ "sum(\"MEMO_VALUE_CGST\") as \"MEMO_CGST_AMT\","
				+ "sum(\"MEMO_VALUE_SGST\") as \"MEMO_SGST_AMT\""
				+ " from \"CLIENT1_GST\".\"ANX_OUTWARD_DOC_HEADER\" "
				+ " where \"IS_PROCESSED\"= true " 
				+ condition.toString() + " AND "
				+ "\"IS_DELETE\"=false  AND AN_TABLE_SECTION IN "
				+ " ('3B','3C','3E','3G')  AND DOC_TYPE IN " + " ('INV','RNV')"
				+ " group by \"SUPPLIER_GSTIN\" ";

		return strQuery;
	}

	private String queryCondition(TaxAmountReconRequestDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin TaxAmountReconRequestDto" + ".queryCondition ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();

		condition1.append(" AND \"SUPPLIER_GSTIN\" in :sgstin");
		condition1.append(" AND \"RETURN_PERIOD\" = :taxPeriod ");

		if (!(req.getStartDocDate() == null && req.getEndDocDate() == null)) {
			condition1.append(" AND \"DOC_DATE\" BETWEEN :startDocDate AND "
					+ ":docEndDate ");
		}
		if (!CollectionUtils.isEmpty(req.getProfitCentres())) {
			condition1.append(" AND \"PROFIT_CENTRE\" in (:profitCenters)");
		}

		if (!CollectionUtils.isEmpty(req.getPlants())) {
			condition1.append(" AND \"PLANT_CODE\" in (:plants)");
		}

		if (!CollectionUtils.isEmpty(req.getDivisions())) {
			condition1.append(" AND \"DIVISION\" in (:divisions)");
		}

		if (!CollectionUtils.isEmpty(req.getLocations())) {
			condition1.append(" AND \"LOCATION\" in (:locations)");
		}

		if (!CollectionUtils.isEmpty(req.getSalesOrgs())) {
			condition1.append(
					" AND \"SALES_ORGANIZATION\" in " + "(:salesOrganization)");

		}

		if (!CollectionUtils.isEmpty(req.getPurchaseOrgs())) {
			condition1.append(" AND \"PURCHASE_ORGANIZATION\" in "
					+ "(:purchaseOrganization)");

		}

		if (!CollectionUtils.isEmpty(req.getDistributionChannels())) {
			condition1.append(" AND \"DISTRIBUTION_CHANNEL\" in "
					+ "(:distributionChannels)");

		}

		if (!CollectionUtils.isEmpty(req.getUserAccess1())) {
			condition1.append(" AND \"USERACCESS1\" in " + "(:userAccess1)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess2())) {
			condition1.append(" AND \"USERACCESS2\" in " + "(:userAccess2)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess3())) {
			condition1.append(" AND \"USERACCESS3\" in " + "(:userAccess3)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess4())) {
			condition1.append(" AND \"USERACCESS4\" in " + "(:userAccess4)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess5())) {
			condition1.append(" AND \"USERACCESS5\" in " + "(:userAccess5)");
		}

		if (!CollectionUtils.isEmpty(req.getUserAccess6())) {
			condition1.append(" AND \"USERACCESS6\" in " + "(:userAccess6)");
		}

		return condition1.toString();
	}

}
