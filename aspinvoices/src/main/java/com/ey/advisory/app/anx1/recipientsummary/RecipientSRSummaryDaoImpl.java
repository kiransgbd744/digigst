/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr2.userdetails.EntityServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja 
 * This is DAO Layer this class interact with the Database
 * and get Required data in this class we generate the SQL in by
 * validating the Request Parameters.
 */

@Slf4j
@Repository("RecipientSRSummaryDaoImpl")
public class RecipientSRSummaryDaoImpl implements RecipientSRSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityServiceImpl entityService;
	
	@Override
	public List<RecipientSRSummaryDto> getAllRecipientSRSummary(
			RecipientSummaryRequestDto requestRecipientSummary, String 
			validationQuery) {

		String queryString = createQueryString(requestRecipientSummary, 
				validationQuery);

		if (LOGGER.isDebugEnabled()) {
			String str = String.format(
					"Query created for SRRecipientSummary"
							+ "on FilterConditions Provided By User : %s",
					queryString);
			LOGGER.debug(str);
		}

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("sgstin",
				requestRecipientSummary.getGstins());

		q.setParameter("taxPeriod", requestRecipientSummary.getTaxPeriod());

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
		List<RecipientSRSummaryDto> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));

		return retList;
	}

	private RecipientSRSummaryDto convert(Object[] arr) {

		if (LOGGER.isDebugEnabled()) {
			String str = "Converting generic object to"
					+ " RecipientSRSummaryDto object";
			LOGGER.debug(str);
		}
		RecipientSRSummaryDto obj = new RecipientSRSummaryDto();
		obj.setCPan((String) arr[1]);
		obj.setCgstin((String) arr[0]);
		obj.setTableType((String) arr[3]);
		obj.setDocType((String) arr[2]);
		BigInteger bi = GenUtil.getBigInteger(arr[4]);
		Integer count = bi.intValue();
		obj.setCount(count);
		obj.setTaxableVal((BigDecimal) arr[9]);
		obj.setIgst((BigDecimal) arr[5]);
		obj.setCgst((BigDecimal) arr[6]);
		obj.setSgst((BigDecimal) arr[7]);
		obj.setCess((BigDecimal) arr[8]);
		obj.setDocAmt((BigDecimal) arr[10]);
		obj.setLevel("L3");
		return obj;
	}

	public String createQueryString(RecipientSummaryRequestDto receipientDto,
			String validationQuery) {
		StringBuilder condition1 = new StringBuilder();
		condition1.append(" \"SUPPLIER_GSTIN\" in (:sgstin) ");

		condition1.append(" AND \"RETURN_PERIOD\" = :taxPeriod ");

		if (!CollectionUtils.isEmpty(receipientDto.getCPans())) {
			condition1.append(" AND \"DERIVED_CGSTIN_PAN\" in (:cPans)");
		}

		if (!CollectionUtils.isEmpty(receipientDto.getCgstin())) {
			condition1.append(" AND \"CUST_GSTIN\" in (:cgstin)");
		}

		condition1.append(validationQuery);

		String queryStr = "SELECT IFNULL(\"CUST_GSTIN\",'-') AS CUST_GSTIN, "
			+ "CASE WHEN \"CUST_GSTIN\" IS NOT NULL THEN "
			+ "SUBSTRING(\"CUST_GSTIN\", 3, 10)"
			+ " ELSE '-' END AS \"DERIVED_CGSTIN_PAN\","
			+ "\"DOC_TYPE\",\"AN_TAX_DOC_TYPE\",(IFNULL(COUNT(*),0)),"
			+ "SUM(IFNULL(\"IGST_AMT\",0)) AS "
			+ "\"IGST_AMT\""
			+ ", SUM(IFNULL(\"CGST_AMT\",0)) AS \"CGST_AMT\""
			+ ",SUM(IFNULL(\"SGST_AMT\",0)) AS \"SGST_AMT\" "
			+ ",SUM(IFNULL(\"CESS_AMT_SPECIFIC\",0)+"
			+ "IFNULL(\"CESS_AMT_ADVALOREM\",0)) AS "
			+ "\"CESS\",SUM(IFNULL(\"TAXABLE_VALUE\",0)) AS \"TAXABLE_VALUE\" "
			+ ",SUM(IFNULL(\"DOC_AMT\",0)) AS \"TAX_PAYABLE\""
			+ " FROM \"CLIENT1_GST\""
			+ ".\"ANX_OUTWARD_DOC_HEADER\" WHERE \"AN_TAX_DOC_TYPE\" IN "
			+ "('SEZWT','SEZT','B2B','DXP') AND IS_PROCESSED = TRUE AND  "
			+ "IS_DELETE = FALSE AND " + condition1.toString()
			+ " GROUP BY \"DOC_TYPE\",\"AN_TAX_DOC_TYPE\",\"TABLE_SECTION\","
			+ "IFNULL(\"CUST_GSTIN\", '-') ," 
			+ " CASE WHEN \"CUST_GSTIN\" IS NOT NULL THEN SUBSTRING"
			+"(\"CUST_GSTIN\", 3, 10)  ELSE '-' END";

		
		/*SELECT IFNULL("CUST_GSTIN", '-') AS" CUST_GSTIN", 
		CASE WHEN "CUST_GSTIN" IS NOT NULL THEN SUBSTRING("CUST_GSTIN", 3, 10) ELSE '-' 
		END AS "DERIVED_CGSTIN_PAN",
		"DOC_TYPE","AN_TAX_DOC_TYPE",COUNT(*),SUM("IGST_AMT") AS "IGST_AMT", SUM("CGST_AMT") AS
		 "CGST_AMT",SUM("SGST_AMT") AS "SGST_AMT" ,SUM("CESS_AMT_SPECIFIC"+"CESS_AMT_ADVALOREM") 
		 AS "CESS",SUM("TAXABLE_VALUE") AS "TAXABLE_VALUE" ,SUM("TAX_PAYABLE") AS "TAX_PAYABLE" 
		 FROM "CLIENT1_GST"."ANX_OUTWARD_DOC_HEADER" 
		WHERE "AN_TAX_DOC_TYPE" IN ('SEZWT','SEZT','B2B,DXP')
		GROUP BY "DOC_TYPE","AN_TAX_DOC_TYPE","TABLE_SECTION",IFNULL("CUST_GSTIN", '-') 
		CASE WHEN "CUST_GSTIN" IS NOT NULL THEN SUBSTRING("CUST_GSTIN", 3, 10) 
		 ELSE '-' END;*/
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(queryStr.toString());
		}
		return queryStr;
	}
}
