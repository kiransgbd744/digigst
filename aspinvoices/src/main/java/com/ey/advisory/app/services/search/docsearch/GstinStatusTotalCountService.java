/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.einvoice.GstinStatusDocSearchReqDto;
import com.ey.advisory.app.util.OnboardingConstant;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("GstinStatusTotalCountService")
public class GstinStatusTotalCountService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public Integer getgstinStatusCount(GstinStatusDocSearchReqDto criteria) {

		GstinStatusDocSearchReqDto request = (GstinStatusDocSearchReqDto) criteria;

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		String returnPeriod = request.getReturnPeriod();
		String tableType = request.getTableType();
		String supplyType = getSupplyType(tableType, request.getSupplyType());
		String docType = getDocType(request.getDocType());
		String requestedDocType = request.getDocType();
		List<String> docNoList = request.getDocNums();
		docNoList.replaceAll(String::toUpperCase);
		String docDate = request.getDocDate();
		String recipientGstin = request.getRecipientGstin();

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
		Integer totalCount = 0;

		if (tableType.equalsIgnoreCase("B2B")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				totalCount = getB2BCountData(gstinList, returnPeriod, docNoList,
						docDate, docType, recipientGstin, GSTIN, supplyType,
						tableType);
			}
		}

		else if (tableType.equalsIgnoreCase("B2BA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				totalCount = getB2BCountData(gstinList, returnPeriod, docNoList,
						docDate, docType, recipientGstin, GSTIN, supplyType,
						tableType);
			}
		} else if (tableType.equalsIgnoreCase("CDNR")) {
			if ((requestedDocType == null
					|| requestedDocType.equalsIgnoreCase("CR")
					|| requestedDocType.equalsIgnoreCase("DR"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				totalCount = getCDNRCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("CDNRA")) {
			if ((requestedDocType == null
					|| requestedDocType.equalsIgnoreCase("RCR")
					|| requestedDocType.equalsIgnoreCase("RDR"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				totalCount = getCDNRCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("EXP")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("WPAY")
							|| supplyType.equalsIgnoreCase("WOPAY"))
					&& (recipientGstin == null)) {
				totalCount = getEXPCountData(gstinList, returnPeriod, docNoList,
						docDate, docType, recipientGstin, GSTIN, supplyType,
						tableType);
			}
		} else if (tableType.equalsIgnoreCase("EXPA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("WPAY")
							|| supplyType.equalsIgnoreCase("WOPAY"))
					&& (recipientGstin == null)) {
				totalCount = getEXPCountData(gstinList, returnPeriod, docNoList,
						docDate, docType, recipientGstin, GSTIN, supplyType,
						tableType);
			}
		} else if (tableType.equalsIgnoreCase("CDNUR")) {
			if ((docType == null || requestedDocType.equalsIgnoreCase("CR")
					|| requestedDocType.equalsIgnoreCase("DR"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("B2CL")
							|| supplyType.equalsIgnoreCase("EXPWOP")
							|| supplyType.equalsIgnoreCase("EXPWP"))
					&& (recipientGstin == null)) {
				totalCount = getCDNURCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("CDNURA")) {
			if ((requestedDocType == null
					|| requestedDocType.equalsIgnoreCase("RCR")
					|| requestedDocType.equalsIgnoreCase("RDR"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("B2CL")
							|| supplyType.equalsIgnoreCase("EXPWOP")
							|| supplyType.equalsIgnoreCase("EXPWP"))
					&& (recipientGstin == null)) {
				totalCount = getCDNURCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("B2CL")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("TAX")
							|| supplyType.equalsIgnoreCase("EXPWOP"))
					&& (recipientGstin == null)) {
				totalCount = getB2CLCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("B2CLA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("TAX")
							|| supplyType.equalsIgnoreCase("EXPWOP"))
					&& (recipientGstin == null)) {
				totalCount = getB2CLCountData(gstinList, returnPeriod,
						docNoList, docDate, docType, recipientGstin, GSTIN,
						supplyType, tableType);

			}
		}
		return totalCount;

	}

	private String createB2BCountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_B2B_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createB2BACountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_B2BA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createCDNRCountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_CDNR_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createCDNRACountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_CDNRA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createEXPCountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_EXP_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createEXPACountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_EXPA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createCDNURCountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_CDNUR_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createCDNURACountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_CDNURA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createB2CLCountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_B2CL_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	private String createB2CLACountQueryString(String buildQuery) {

		return "SELECT COUNT(ID) "
				+ "FROM GETGSTR1_B2CLA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery;
	}

	public Integer getB2BCountData(List<String> gstinList, String returnPeriod,
			List<String> docNoList, String docDate, String docType,
			String recipientGstin, String GSTIN, String supplyType,
			String tableType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" AND GSTIN IN :gstinList ");
		}

		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD =:returnPeriod ");
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			buildQuery.append(" AND INV_NUM IN :docNoList ");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND INV_TYPE =:supplyType");
		}
		if (docDate != null) {
			buildQuery.append(" AND INV_DATE =:docDate");
		}
		if (recipientGstin != null && !recipientGstin.isEmpty()) {
			buildQuery.append(" AND  CTIN =:recipientGstin");
		}

		String queryStr = null;
		if (tableType.equalsIgnoreCase("B2B")) {
			queryStr = createB2BCountQueryString(buildQuery.toString());
		} else if (tableType.equalsIgnoreCase("B2BA")) {
			queryStr = createB2BACountQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}

			if (returnPeriod != null) {
				q.setParameter("returnPeriod", returnPeriod);
			}

			if (supplyType != null && !supplyType.isEmpty()) {
				q.setParameter("supplyType", supplyType);
			}
			if (docNoList != null && !docNoList.isEmpty()) {
				q.setParameter("docNoList", docNoList);
			}
			if (docDate != null) {
				q.setParameter("docDate", docDate);
			}
			if (recipientGstin != null) {
				q.setParameter("recipientGstin", recipientGstin);
			}
		}

		return ((Number) q.getSingleResult()).intValue();
	}

	public Integer getCDNRCountData(List<String> gstinList, String returnPeriod,
			List<String> docNoList, String docDate, String docType,
			String recipientGstin, String GSTIN, String supplyType,
			String tableType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" AND GSTIN IN :gstinList ");
		}

		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD =:returnPeriod ");
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			buildQuery.append(" AND NOTE_NUM IN :docNoList ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND NOTE_TYPE =:docType");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND INV_TYPE =:supplyType");
		}
		if (docDate != null) {
			buildQuery.append(" AND NOTE_DATE =:docDate");
		}
		if (recipientGstin != null && !recipientGstin.isEmpty()) {
			buildQuery.append(" AND  CTIN =:recipientGstin");
		}

		String queryStr = null;
		if (tableType.equalsIgnoreCase("CDNR")) {
			queryStr = createCDNRCountQueryString(buildQuery.toString());
		} else if (tableType.equalsIgnoreCase("CDNRA")) {
			queryStr = createCDNRACountQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}

			if (returnPeriod != null) {
				q.setParameter("returnPeriod", returnPeriod);
			}

			if (supplyType != null && !supplyType.isEmpty()) {
				q.setParameter("supplyType", supplyType);
			}
			if (docType != null && !docType.isEmpty()) {
				q.setParameter("docType", docType);
			}
			if (docNoList != null && !docNoList.isEmpty()) {
				q.setParameter("docNoList", docNoList);
			}
			if (docDate != null) {
				q.setParameter("docDate", docDate);
			}
			if (recipientGstin != null) {
				q.setParameter("recipientGstin", recipientGstin);
			}
		}

		return ((Number) q.getSingleResult()).intValue();
	}

	public Integer getEXPCountData(List<String> gstinList, String returnPeriod,
			List<String> docNoList, String docDate, String docType,
			String recipientGstin, String GSTIN, String supplyType,
			String tableType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" AND GSTIN IN :gstinList ");
		}

		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD =:returnPeriod ");
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			buildQuery.append(" AND INV_NUM IN :docNoList ");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND EXPORT_TYPE =:supplyType");
		}
		if (docDate != null) {
			buildQuery.append(" AND INV_DATE =:docDate");
		}

		String queryStr = null;
		if (tableType.equalsIgnoreCase("EXP")) {
			queryStr = createEXPCountQueryString(buildQuery.toString());
		} else if (tableType.equalsIgnoreCase("EXPA")) {
			queryStr = createEXPACountQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}

			if (returnPeriod != null) {
				q.setParameter("returnPeriod", returnPeriod);
			}
			if (docNoList != null && !docNoList.isEmpty()) {
				q.setParameter("docNoList", docNoList);
			}
			if (supplyType != null && !supplyType.isEmpty()) {
				q.setParameter("supplyType", supplyType);
			}
			if (docDate != null) {
				q.setParameter("docDate", docDate);
			}
		}

		return ((Number) q.getSingleResult()).intValue();
	}

	public Integer getCDNURCountData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String docType, String recipientGstin, String GSTIN,
			String supplyType, String tableType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" AND GSTIN IN :gstinList ");
		}

		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD =:returnPeriod ");
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			buildQuery.append(" AND NOTE_NUM IN :docNoList ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND NOTE_TYPE =:docType");
		}
		if (supplyType != null && !supplyType.isEmpty()) {
			buildQuery.append(" AND TYPE =:supplyType");
		}
		if (docDate != null) {
			buildQuery.append(" AND NOTE_DATE =:docDate");
		}

		String queryStr = null;
		if (tableType.equalsIgnoreCase("CDNUR")) {
			queryStr = createCDNURCountQueryString(buildQuery.toString());
		} else if (tableType.equalsIgnoreCase("CDNURA")) {
			queryStr = createCDNURACountQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}

			if (returnPeriod != null) {
				q.setParameter("returnPeriod", returnPeriod);
			}

			if (supplyType != null && !supplyType.isEmpty()) {
				q.setParameter("supplyType", supplyType);
			}
			if (docType != null && !docType.isEmpty()) {
				q.setParameter("docType", docType);
			}
			if (docNoList != null && !docNoList.isEmpty()) {
				q.setParameter("docNoList", docNoList);
			}
			if (docDate != null) {
				q.setParameter("docDate", docDate);
			}
		}

		return ((Number) q.getSingleResult()).intValue();
	}

	public Integer getB2CLCountData(List<String> gstinList, String returnPeriod,
			List<String> docNoList, String docDate, String docType,
			String recipientGstin, String GSTIN, String supplyType,
			String tableType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstinList != null && gstinList.size() > 0) {
			buildQuery.append(" AND GSTIN IN :gstinList ");
		}

		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			buildQuery.append(" AND RETURN_PERIOD =:returnPeriod ");
		}
		if (docNoList != null && !docNoList.isEmpty()) {
			buildQuery.append(" AND INV_NUM IN :docNoList ");
		}

		if (docDate != null) {
			buildQuery.append(" AND INV_DATE =:docDate");
		}

		String queryStr = null;
		if (tableType.equalsIgnoreCase("B2CL")) {
			queryStr = createB2CLCountQueryString(buildQuery.toString());
		} else if (tableType.equalsIgnoreCase("B2CLA")) {
			queryStr = createB2CLACountQueryString(buildQuery.toString());
		}
		Query q = entityManager.createNativeQuery(queryStr);

		if (GSTIN != null && !GSTIN.isEmpty()) {
			if (gstinList != null && !gstinList.isEmpty()
					&& gstinList.size() > 0) {
				q.setParameter("gstinList", gstinList);
			}

			if (returnPeriod != null) {
				q.setParameter("returnPeriod", returnPeriod);
			}
			if (docNoList != null && !docNoList.isEmpty()) {
				q.setParameter("docNoList", docNoList);
			}
			if (docDate != null) {
				q.setParameter("docDate", docDate);
			}
		}

		return ((Number) q.getSingleResult()).intValue();
	}

	public String getSupplyType(String tableType, String supplyType) {
		if (supplyType != null && tableType != null) {
			if (tableType.equalsIgnoreCase("B2B")
					|| tableType.equalsIgnoreCase("B2BA")
					|| tableType.equalsIgnoreCase("CDNR")
					|| tableType.equalsIgnoreCase("CDNRA")) {
				if (supplyType.equalsIgnoreCase("TAX"))
					return "R";
				else if (supplyType.equalsIgnoreCase("SEZWOP"))
					return "SEWOP";
				else if (supplyType.equalsIgnoreCase("SEZWP"))
					return "SEWP";
				else if (supplyType.equalsIgnoreCase("DXP"))
					return "DE";
				else
					return supplyType;
			} else if (tableType.equalsIgnoreCase("CDNUR")
					|| tableType.equalsIgnoreCase("CDNURA")) {
				if (supplyType.equalsIgnoreCase("TAX"))
					return "B2CL";
				else if (supplyType.equalsIgnoreCase("EXPT"))
					return "EXPWOP";
				else if (supplyType.equalsIgnoreCase("EXPWT"))
					return "EXPWP";
				else
					return supplyType;
			} else if (tableType.equalsIgnoreCase("EXP")
					|| tableType.equalsIgnoreCase("EXPA")) {
				if (supplyType.equalsIgnoreCase("EXPT"))
					return "WPAY";
				else if (supplyType.equalsIgnoreCase("EXPWT"))
					return "WOPAY";
				else
					return supplyType;
			} else if (tableType.equalsIgnoreCase("B2CL")
					|| tableType.equalsIgnoreCase("B2CLA")) {
				if (supplyType.equalsIgnoreCase("TAX"))
					return "TAX";
				else
					return supplyType;
			}
			return supplyType;
		} else {
			return null;
		}
	}

	public String getDocType(String docType) {
		if (docType != null) {
			if (docType.equalsIgnoreCase("CR")
					|| docType.equalsIgnoreCase("RCR")) {
				return "C";
			} else if (docType.equalsIgnoreCase("DR")
					|| docType.equalsIgnoreCase("RDR")) {
				return "D";
			} else {
				return docType;
			}
		} else {
			return null;
		}
	}
}
