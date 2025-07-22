package com.ey.advisory.app.anx1.recipientsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr2.userdetails.EntityServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 **/
@Slf4j
@Component("RecipientResponseSummaryDetailDaoImpl")
public class RecipientResponseSummaryDetailDaoImpl
		implements RecipientResponseSummaryDetailDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityServiceImpl entityService;

	@Override
	public List<RecipientDBResponseDto> getAllResponseSummaryDetails(
			String reqData,
			RecipientSummaryRequestDto requestRecipientSummary) {

		String queryString = createQueryString(reqData,
				requestRecipientSummary);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Response Summary : %s", queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("sgstin",
				requestRecipientSummary.getGstins());

		q.setParameter("taxPeriod", requestRecipientSummary.getTaxPeriod());
		
		if (!CollectionUtils.isEmpty(requestRecipientSummary.getCPans())) {
			q.setParameter("cPans", requestRecipientSummary.getCPans());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getCgstin())) {
			q.setParameter("cgstin", requestRecipientSummary.getCgstin());
		}

		if (!(requestRecipientSummary.getStartDocDate() == null
				&& requestRecipientSummary.getEndDocDate() == null)) {
			q.setParameter("startDocDate",
					requestRecipientSummary.getStartDocDate());
			q.setParameter("docEndDate",
					requestRecipientSummary.getEndDocDate());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getProfitCentres())) {
			q.setParameter("profitCenters",
					requestRecipientSummary.getProfitCentres());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getPlants())) {
			q.setParameter("plants", requestRecipientSummary.getPlants());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getDivisions())) {
			q.setParameter("divisions", requestRecipientSummary.getDivisions());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getLocations())) {
			q.setParameter("locations", requestRecipientSummary.getLocations());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getSalesOrgs())) {
			q.setParameter("salesOrganization",
					requestRecipientSummary.getSalesOrgs());
		}
		
		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getPurchaseOrgs())) {
			q.setParameter("purchaseOrgs",
					requestRecipientSummary.getPurchaseOrgs());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getDistributionChannels())) {
			q.setParameter("distributionChannels",
					requestRecipientSummary.getDistributionChannels());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess1())) {
			q.setParameter("userAccess1",
					requestRecipientSummary.getUserAccess1());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess2())) {
			q.setParameter("userAccess2",
					requestRecipientSummary.getUserAccess2());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess3())) {
			q.setParameter("userAccess3",
					requestRecipientSummary.getUserAccess3());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess4())) {
			q.setParameter("userAccess4",
					requestRecipientSummary.getUserAccess4());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess5())) {
			q.setParameter("userAccess5",
					requestRecipientSummary.getUserAccess5());
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess6())) {
			q.setParameter("userAccess6",
					requestRecipientSummary.getUserAccess6());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		List<RecipientDBResponseDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;

	}

	private RecipientDBResponseDto convert(Object[] o) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " RecipientDBResponseDto object";
			LOGGER.debug(str);
		}

		RecipientDBResponseDto obj = new RecipientDBResponseDto("L3");
		obj.setCgstin((String) o[0]);
		obj.setAction((String) o[1]);
		obj.setDocType((String) o[2]);
		obj.setTableSection((String) o[3]);
		BigInteger bi = GenUtil.getBigInteger(o[4]);
		Integer count = bi.intValue();
		obj.setCount(count);
		obj.setTaxableVal((BigDecimal) o[5]);
		obj.setTaxAmt((BigDecimal) o[6]);
		obj.setCPan((String) o[7]);

		return obj;
	}

	private static String createQueryString(String condition,
			RecipientSummaryRequestDto requestRecipientSummary) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Creating query for Response summary.";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();
		StringBuilder condition2 = new StringBuilder();

		condition1.append(condition);
		condition2.append(condition);

		condition1.append(" AND \"GSTIN\" in (:sgstin) ");
		condition2.append(" AND \"SUPPLIER_GSTIN\" in (:sgstin) ");

		condition1.append(" AND \"TAX_PERIOD\" = :taxPeriod ");
		condition2.append(" AND \"RETURN_PERIOD\" = :taxPeriod ");

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getCgstin())) {
			condition1.append(" AND \"CTIN\" in (:cgstin)");
			condition2.append(" AND \"CUST_GSTIN\" in (:cgstin)");
		}

		if (!CollectionUtils.isEmpty(requestRecipientSummary.getCPans())) {
			condition2.append(" AND \"DERIVED_CGSTIN_PAN\" in (:cPans)");
		}

		String queryStr = "select IFNULL(CTIN,'-') as CTIN,gbh.ACTION,"
				+ "gbh.DOC_TYPE," + "'B2B' AS TABLE_SECTION ,COUNT(*) AS CNT,"
				+ "sum(IFNULL(gbh.TAXABLE_VALUE, 0)) as TAXABLE_VALUE,"
				+ "sum(IFNULL(gbh.IGST_AMT, 0) + IFNULL(gbh.CGST_AMT, 0) + "
				+ "IFNULL(gbh.SGST_AMT, 0) + IFNULL(gbh.CESS_AMT, 0))"
				+ " as TOTAL_TAX " + ",IFNULL(gbh.CGSTIN_PAN, '-') as CPAN "
				+ "from GETANX1_B2B_HEADER gbh inner join "
				+ "ANX_OUTWARD_DOC_HEADER odh on "
				+ "gbh.DOCKEY = odh.DOC_KEY " + "where "
				+ "gbh.is_delete=false " + condition1.toString()
				+ " GROUP BY IFNULL(gbh.CTIN,'-'),gbh.ACTION,gbh.DOC_TYPE,"
				+ " IFNULL(gbh.CGSTIN_PAN, '-') " + "union "
				+ "select IFNULL(CTIN,'-') as CTIN,gdh.ACTION,"
				+ "gdh.DOC_TYPE," + "'DE' AS TABLE_SECTION ,COUNT(*) AS CNT,"
				+ "sum(IFNULL(gdh.TAXABLE_VALUE, 0)) as TAXABLE_VALUE,"
				+ "sum(IFNULL(gdh.IGST_AMT, 0) + IFNULL(gdh.CGST_AMT, 0) + "
				+ "IFNULL(gdh.SGST_AMT, 0) + IFNULL(gdh.CESS_AMT, 0))"
				+ "as TOTAL_TAX," + "IFNULL(gdh.CGSTIN_PAN,'-') as CPAN "
				+ "from GETANX1_DE_HEADER gdh inner join "
				+ "ANX_OUTWARD_DOC_HEADER odh on "
				+ "gdh.DOCKEY = odh.DOC_KEY where " + "gdh.is_delete=false "
				+ condition1.toString() + " GROUP BY IFNULL(gdh.CTIN, '-'), "
				+ "gdh.ACTION, gdh.DOC_TYPE, IFNULL(gdh.CGSTIN_PAN,'-') "
				+ "union " + "select IFNULL(CTIN, '-'),"
				+ " gsh.ACTION,gsh.DOC_TYPE,"
				+ "'SEZWT' AS TABLE_SECTION ,COUNT(*) AS CNT,"
				+ "sum(IFNULL(gsh.TAXABLE_VALUE, 0)) as "
				+ "TAXABLE_VALUE,0 as TOTAL_TAX ,"
				+ "IFNULL(gsh.CGSTIN_PAN,'-') as CPAN "
				+ "from GETANX1_SEZWOP_HEADER  gsh inner join "
				+ "ANX_OUTWARD_DOC_HEADER odh on "
				+ "gsh.DOCKEY = odh.DOC_KEY  where " + "gsh.is_delete=false "
				+ condition1.toString() + " GROUP BY IFNULL(gsh.CTIN, '-'),"
				+ "gsh.ACTION,gsh.DOC_TYPE,IFNULL(gsh.CGSTIN_PAN, '-') "
				+ "union " + "select IFNULL(CTIN, '-') AS CTIN ,gsph.ACTION,"
				+ "gsph.DOC_TYPE,"
				+ "'SEZT' AS TABLE_SECTION ,COUNT(*) AS CNT,"
				+ "sum(IFNULL(gsph.TAXABLE_VALUE, 0)) as TAXABLE_VALUE,"
				+ "sum(IFNULL(gsph.IGST_AMT, 0) + "
				+ "IFNULL(gsph.CESS_AMT, 0)) as TOTAL_TAX ,"
				+ "IFNULL(gsph.CGSTIN_PAN, '-') as CPAN "
				+ "from GETANX1_SEZWP_HEADER gsph inner join "
				+ "ANX_OUTWARD_DOC_HEADER odh on "
				+ "gsph.DOCKEY = odh.DOC_KEY where " + "gsph.is_delete=false "
				+ condition1.toString()
				+ " GROUP BY IFNULL(gsph.CTIN,'-'),gsph.ACTION,gsph.DOC_TYPE,"
				+ "IFNULL(gsph.CGSTIN_PAN,'-') " + "union "
				+ "select IFNULL(CUST_GSTIN,'-') as CTIN,"
				+ "'NS' as ACTION,DOC_TYPE,AN_TAX_DOC_TYPE as TABLE_SECTION,"
				+ "COUNT(*) AS CNT,sum(IFNULL(TAXABLE_VALUE, 0)) as "
				+ "TAXABLE_VALUE,"
				+ "sum(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT,0) + "
				+ "IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT_SPECIFIC, 0) + "
				+ "IFNULL(CESS_AMT_ADVALOREM, 0)) as TOTAL_TAX , "
				+ "IFNULL(DERIVED_CGSTIN_PAN,'-') as CPAN "
				+ "from ANX_OUTWARD_DOC_HEADER "
				+ "where AN_TAX_DOC_TYPE in ('B2B', 'SEZT', 'SEZWT', 'DXP') and"
				+ " IS_PROCESSED= true and IS_SAVED_TO_GSTN = false "
				+ condition2.toString() + " and IS_DELETE = false "
				+ "GROUP BY IFNULL(CUST_GSTIN,'-'), DOC_TYPE,AN_TAX_DOC_TYPE,"
				+ "IFNULL(DERIVED_CGSTIN_PAN,'-') " + " union "
				+ "select IFNULL(CUST_GSTIN,'-') as CTIN,'S' as "
				+ "ACTION,DOC_TYPE,"
				+ "AN_TAX_DOC_TYPE AS TABLE_SECTION ,COUNT(*) AS CNT,"
				+ "sum(IFNULL(TAXABLE_VALUE, 0)) as TAXABLE_VALUE,"
				+ "sum(IFNULL(IGST_AMT, 0) + IFNULL(CGST_AMT,0) + "
				+ "IFNULL(SGST_AMT, 0) + IFNULL(CESS_AMT_SPECIFIC, 0) + "
				+ "IFNULL(CESS_AMT_ADVALOREM, 0)) as TOTAL_TAX , "
				+ "IFNULL(DERIVED_CGSTIN_PAN,'-') as CPAN "
				+ "from ANX_OUTWARD_DOC_HEADER where " 
				+ "AN_TAX_DOC_TYPE in ('B2B', 'SEZT', 'SEZWT', 'DXP') and "
				+ "IS_PROCESSED = true and IS_SAVED_TO_GSTN = true "
				+ condition2.toString() + " and IS_DELETE = false "
				+ "GROUP BY IFNULL(CUST_GSTIN,'-'), DOC_TYPE,AN_TAX_DOC_TYPE,"
				+ "IFNULL(DERIVED_CGSTIN_PAN,'-')";

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for Response Summary : %s", queryStr);
			LOGGER.debug(str);
		}

		return queryStr;
	}

}
