package com.ey.advisory.app.services.vendorcomm;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VendorCommDAOImpl")
public class VendorCommDAOImpl implements VendorCommDAO {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDistinctCombFromLink2APR(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList, boolean isApOpted) {
		String queryStr = null;

		if (isApOpted) {
			queryStr = createQueryString2APRDistinctCombAPOpted();
		} else {
			queryStr = createQueryString2APRDistinctCombNonAP();
		}

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter(1, derivedFromTaxPeriod);
		q.setParameter(2, derivedToTaxPeriod);
		q.setParameter(3, derivedFromTaxPeriod);
		q.setParameter(4, derivedToTaxPeriod);
		q.setParameter(5, reportTypeList);

		List<Object[]> resultList = q.getResultList();

		LOGGER.debug("DistinctCombFromLink2APR size ::", resultList.size());

		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDistinctCombFromLink2BPR(
			Integer derivedFromTaxPeriod, Integer derivedToTaxPeriod,
			List<String> reportTypeList) {
		String queryStr = null;

		queryStr = createQueryString2BPRDistinctComb();

		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter(1, derivedFromTaxPeriod);
		q.setParameter(2, derivedToTaxPeriod);
		q.setParameter(3, derivedFromTaxPeriod);
		q.setParameter(4, derivedToTaxPeriod);
		q.setParameter(5, reportTypeList);

		List<Object[]> resultList = q.getResultList();

		LOGGER.debug("DistinctCombFromLink2APR size ::", resultList.size());

		return resultList;
	}

	private String createQueryString2APRDistinctCombNonAP() {
		return "" + "SELECT DISTINCT IFNULL(LK.PR_SUPPLIER_GSTIN,A2_SUPPLIER_GSTIN), "
				+ "IFNULL(LK.PR_RECIPIENT_GSTIN,LK.A2_RECIPIENT_GSTIN) FROM "
				+ "TBL_AUTO_2APR_LINK AS LK  INNER JOIN "
				+ "TBL_RECON_REPORT_GSTIN_DETAILS AS GD "
				+ "ON (LK.PR_RECIPIENT_GSTIN = GD.GSTIN "
				+ "OR LK.A2_RECIPIENT_GSTIN = GD.GSTIN ) AND LK.IS_ACTIVE=TRUE "
				+ "INNER JOIN TBL_RECON_REPORT_CONFIG C "
				+ "ON C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
				+ "WHERE ((LK.PR_RET_PERIOD >= ?1 "
				+ "AND LK.PR_RET_PERIOD <=?2 ) OR (LK.A2_RET_PERIOD >=?3 "
				+ "AND LK.A2_RET_PERIOD <= ?4))AND GD.IS_ACTIVE = TRUE "
				+ "AND LK.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID "
				+ "AND LK.CURRENT_REPORT_TYPE IN (?5) AND C.RECON_TYPE IN ('NON_AP_M_2APR');";
	}

	private String createQueryString2APRDistinctCombAPOpted() {
		return "" + "SELECT DISTINCT  IFNULL(LK.PR_SUPPLIER_GSTIN,A2_SUPPLIER_GSTIN), "
				+ "IFNULL(LK.PR_RECIPIENT_GSTIN,LK.A2_RECIPIENT_GSTIN) FROM "
				+ "TBL_AUTO_2APR_LINK AS LK  INNER JOIN "
				+ "TBL_RECON_REPORT_GSTIN_DETAILS AS GD "
				+ "ON (LK.PR_RECIPIENT_GSTIN = GD.GSTIN OR "
				+ "LK.A2_RECIPIENT_GSTIN = GD.GSTIN ) AND LK.IS_ACTIVE=TRUE "
				+ "INNER JOIN TBL_RECON_REPORT_CONFIG C "
				+ "ON C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
				+ "WHERE ((LK.PR_RET_PERIOD >= ?1 "
				+ "AND LK.PR_RET_PERIOD <= ?2 ) OR (LK.A2_RET_PERIOD >=?3 "
				+ "AND LK.A2_RET_PERIOD <= ?4))AND GD.IS_ACTIVE = TRUE "
				+ "AND LK.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID "
				+ "AND LK.CURRENT_REPORT_TYPE IN (?5) AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR');";
	}

	private String createQueryString2BPRDistinctComb() {
		return "SELECT DISTINCT  IFNULL(LK.PR_SUPPLIER_GSTIN,LK.B2_SUPPLIER_GSTIN), " 
				 + "IFNULL(LK.PR_RECIPIENT_GSTIN,LK.B2_RECIPIENT_GSTIN) FROM "
				+ "TBL_LINK_2B_PR AS LK  INNER JOIN "
				+ "TBL_RECON_REPORT_GSTIN_DETAILS AS GD "
				+ "ON (LK.PR_RECIPIENT_GSTIN = GD.GSTIN OR LK.B2_RECIPIENT_GSTIN = GD.GSTIN ) "
				+ "INNER JOIN TBL_RECON_REPORT_CONFIG C ON C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
				+ "WHERE ((LK.PR_RET_PERIOD >= ?1 AND LK.PR_RET_PERIOD <= ?2  ) "
				+ "OR (LK.B2_RET_PERIOD >=?3  AND LK.B2_RET_PERIOD <= ?4)) "
				+ "AND GD.IS_ACTIVE = TRUE "
				+ "AND LK.RECON_REPORT_CONFIG_ID = GD.RECON_REPORT_CONFIG_ID "
				+ "AND LK.CURRENT_REPORT_TYPE IN (?5) "
				+ "AND C.RECON_TYPE IN ('2BPR');";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getVendorNamePR(List<String> vendorGstinList) {
		String queryStr = createQueryStringVendorNamePR();
		Query q = entityManager.createNativeQuery(queryStr);

		q.setParameter(1, vendorGstinList);

		List<Object[]> resultList = q.getResultList();

		LOGGER.debug("getVendorNamePR size ::", resultList.size());

		return resultList;
	}

	private String createQueryStringVendorNamePR() {
		return ""
				+ "select SUPPLIER_GSTIN ,CUST_SUPP_NAME from ANX_INWARD_DOC_HEADER "
				+ "where SUPPLIER_GSTIN in (?1) and IS_DELETE = false  ;";
	}

}
