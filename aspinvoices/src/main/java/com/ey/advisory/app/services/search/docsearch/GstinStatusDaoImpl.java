/**
 * 
 */
package com.ey.advisory.app.services.search.docsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.GstinStatusDocumentDto;
import com.ey.advisory.app.docs.dto.einvoice.GstinStatusDocSearchReqDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.search.PageRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("GstinStatusDaoImpl")
@Slf4j
public class GstinStatusDaoImpl implements GstinStatusDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<GstinStatusDocumentDto> getGstinStatusListing(
			GstinStatusDocSearchReqDto criteria, PageRequest pageReq) {

		GstinStatusDocSearchReqDto request = (GstinStatusDocSearchReqDto) criteria;

		String returnPeriod = request.getReturnPeriod();
		String tableType = request.getTableType();
		String supplyType = getSupplyType(tableType, request.getSupplyType());
		String docType = getDocType(request.getDocType());
		String requestedDocType = request.getDocType();
		List<String> docNoList = request.getDocNums();
		docNoList.replaceAll(String::toUpperCase);
		String docDate = request.getDocDate();
		String recipientGstin = request.getRecipientGstin();

		int pageN = pageReq.getPageNo();
		int pageSize = pageReq.getPageSize();
		int pageNo = pageN * pageSize;

		String GSTIN = null;

		List<String> gstinList = null;
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
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

		List<Object[]> list = new ArrayList<Object[]>();

		if (tableType.equalsIgnoreCase("B2B")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				list = getB2BData(gstinList, returnPeriod, docNoList, docDate,
						recipientGstin, pageNo, pageSize, GSTIN, supplyType,
						tableType);
			}
		} else if (tableType.equalsIgnoreCase("B2BA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("R")
							|| supplyType.equalsIgnoreCase("SEWOP")
							|| supplyType.equalsIgnoreCase("SEWP")
							|| supplyType.equalsIgnoreCase("DE"))) {
				list = getB2BData(gstinList, returnPeriod, docNoList, docDate,
						recipientGstin, pageNo, pageSize, GSTIN, supplyType,
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
				list = getCDNRData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
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
				list = getCDNRData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("EXP")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("WPAY")
							|| supplyType.equalsIgnoreCase("WOPAY"))
					&& (recipientGstin == null)) {
				list = getEXPData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("EXPA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("WPAY")
							|| supplyType.equalsIgnoreCase("WOPAY"))
					&& (recipientGstin == null)) {
				list = getEXPData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("CDNUR")) {
			if ((docType == null || requestedDocType.equalsIgnoreCase("CR")
					|| requestedDocType.equalsIgnoreCase("DR"))
					&& (supplyType == null
							|| supplyType.equalsIgnoreCase("B2CL")
							|| supplyType.equalsIgnoreCase("EXPWOP")
							|| supplyType.equalsIgnoreCase("EXPWP"))
					&& (recipientGstin == null)) {
				list = getCDNURData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
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
				list = getCDNURData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("B2CL")) {
			if ((docType == null || docType.equalsIgnoreCase("INV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("TAX")
							|| supplyType.equalsIgnoreCase("EXPWOP"))
					&& (recipientGstin == null)) {
				list = getB2CLData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		} else if (tableType.equalsIgnoreCase("B2CLA")) {
			if ((docType == null || docType.equalsIgnoreCase("RNV"))
					&& (supplyType == null || supplyType.equalsIgnoreCase("TAX")
							|| supplyType.equalsIgnoreCase("EXPWOP"))
					&& (recipientGstin == null)) {
				list = getB2CLData(gstinList, returnPeriod, docNoList, docDate,
						docType, recipientGstin, pageNo, pageSize, GSTIN,
						supplyType, tableType);
			}
		}
		return list.parallelStream().map(o -> convertGstinStatus(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private GstinStatusDocumentDto convertGstinStatus(Object[] arr) {
		GstinStatusDocumentDto obj = new GstinStatusDocumentDto();

		obj.setSupplierGstin(arr[0] != null ? String.valueOf(arr[0]) : null);
		obj.setReturnPeriod(arr[1] != null ? String.valueOf(arr[1]) : null);
		obj.setTableType(arr[2] != null ? String.valueOf(arr[2]) : null);
		obj.setDocType((arr[3] != null && arr[2] != null) ? convertToDocType(
				String.valueOf(arr[2]), String.valueOf(arr[3])) : null);
		obj.setSupplyType(
				(arr[4] != null && arr[2] != null) ? convertToSupplyType(
						String.valueOf(arr[2]), String.valueOf(arr[4])) : null);
		obj.setDocNo(arr[5] != null ? String.valueOf(arr[5]) : null);
		obj.setDocDate(arr[6] != null ? String.valueOf(arr[6]) : null);
		obj.setRecipientGstin(arr[7] != null ? String.valueOf(arr[7]) : null);
		obj.setPos(arr[8] != null ? String.valueOf(arr[8]) : null);
		obj.setTaxableValue(arr[9] != null ? String.valueOf(arr[9]) : null);
		obj.setIgstAmount(arr[10] != null ? String.valueOf(arr[10]) : null);
		obj.setCgstAmount(arr[11] != null ? String.valueOf(arr[11]) : null);
		obj.setSgstAmount(arr[12] != null ? String.valueOf(arr[12]) : null);
		obj.setCessAmount(arr[13] != null ? String.valueOf(arr[13]) : null);
		obj.setInvoiceValue(arr[14] != null ? String.valueOf(arr[14]) : null);
		return obj;
	}

	private String createB2BQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'B2B','INV',INV_TYPE,INV_NUM,INV_DATE,CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_B2B_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createB2BAQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'B2BA','RNV',INV_TYPE,INV_NUM,INV_DATE,CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_B2BA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createCDNRQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'CDNR',NOTE_TYPE,INV_TYPE,NOTE_NUM,NOTE_DATE,CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_CDNR_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createCDNRAQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'CDNRA',NOTE_TYPE,INV_TYPE,NOTE_NUM,NOTE_DATE,CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,CGST_AMT,SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_CDNRA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createEXPQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'EXP','INV',EXPORT_TYPE,INV_NUM,INV_DATE,NULL AS CTIN, "
				+ "NULL AS POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_EXP_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createEXPAQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'EXPA','RNV',EXPORT_TYPE,INV_NUM,INV_DATE,NULL AS CTIN, "
				+ "NULL AS POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_EXPA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createCDNURQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'CDNUR',NOTE_TYPE,TYPE,NOTE_NUM,NOTE_DATE,NULL AS CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_CDNUR_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createCDNURAQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'CDNURA',NOTE_TYPE,TYPE,NOTE_NUM,NOTE_DATE,NULL AS CTIN, "
				+ "POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_CDNURA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createB2CLQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'B2CL','INV',NULL AS INV_TYPE,INV_NUM,INV_DATE,NULL AS CTIN, "
				+ "NULL AS POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_B2CL_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	private String createB2CLAQueryString(String buildQuery, int pageNo,
			int pageSize) {

		return "SELECT GSTIN,RETURN_PERIOD,'B2CLA','RNV',NULL AS INV_TYPE,INV_NUM,INV_DATE,NULL AS CTIN, "
				+ "NULL AS POS,TAXABLE_VALUE,IGST_AMT,NULL AS CGST_AMT,NULL AS SGST_AMT,CESS_AMT,"
				+ "INV_VALUE FROM GETGSTR1_B2CLA_HEADER WHERE IS_DELETE = FALSE "
				+ buildQuery + " limit " + pageSize + " offset " + pageNo;
	}

	public List<Object[]> getB2BData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String recipientGstin, int pageNo, int pageSize, String GSTIN,
			String supplyType, String tableType) {

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
			queryStr = createB2BQueryString(buildQuery.toString(), pageNo,
					pageSize);
		} else if (tableType.equalsIgnoreCase("B2BA")) {
			queryStr = createB2BAQueryString(buildQuery.toString(), pageNo,
					pageSize);
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

		return q.getResultList();
	}

	public List<Object[]> getCDNRData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String docType, String recipientGstin, int pageNo, int pageSize,
			String GSTIN, String supplyType, String tableType) {

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
			queryStr = createCDNRQueryString(buildQuery.toString(), pageNo,
					pageSize);
		} else if (tableType.equalsIgnoreCase("CDNRA")) {
			queryStr = createCDNRAQueryString(buildQuery.toString(), pageNo,
					pageSize);
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

		return q.getResultList();
	}

	public List<Object[]> getEXPData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String docType, String recipientGstin, int pageNo, int pageSize,
			String GSTIN, String supplyType, String tableType) {

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
			queryStr = createEXPQueryString(buildQuery.toString(), pageNo,
					pageSize);
		} else if (tableType.equalsIgnoreCase("EXPA")) {
			queryStr = createEXPAQueryString(buildQuery.toString(), pageNo,
					pageSize);
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

		return q.getResultList();
	}

	public List<Object[]> getCDNURData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String docType, String recipientGstin, int pageNo, int pageSize,
			String GSTIN, String supplyType, String tableType) {

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
			queryStr = createCDNURQueryString(buildQuery.toString(), pageNo,
					pageSize);
		} else if (tableType.equalsIgnoreCase("CDNURA")) {
			queryStr = createCDNURAQueryString(buildQuery.toString(), pageNo,
					pageSize);
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

		return q.getResultList();
	}

	public List<Object[]> getB2CLData(List<String> gstinList,
			String returnPeriod, List<String> docNoList, String docDate,
			String docType, String recipientGstin, int pageNo, int pageSize,
			String GSTIN, String supplyType, String tableType) {

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
			queryStr = createB2CLQueryString(buildQuery.toString(), pageNo,
					pageSize);
		} else if (tableType.equalsIgnoreCase("B2CLA")) {
			queryStr = createB2CLAQueryString(buildQuery.toString(), pageNo,
					pageSize);
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

		return q.getResultList();
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

	public String convertToSupplyType(String tableType, String supplyType) {
		if (supplyType != null && tableType != null) {
			if (tableType.equalsIgnoreCase("B2B")
					|| tableType.equalsIgnoreCase("B2BA")
					|| tableType.equalsIgnoreCase("CDNR")
					|| tableType.equalsIgnoreCase("CDNRA")) {
				if (supplyType.equalsIgnoreCase("R"))
					return "TAX";
				else if (supplyType.equalsIgnoreCase("SEWOP"))
					return "SEZWOP";
				else if (supplyType.equalsIgnoreCase("SEWP"))
					return "SEZWP";
				else if (supplyType.equalsIgnoreCase("DE"))
					return "DXP";
				else
					return supplyType;
			} else if (tableType.equalsIgnoreCase("CDNUR")
					|| tableType.equalsIgnoreCase("CDNURA")) {
				if (supplyType.equalsIgnoreCase("B2CL"))
					return "TAX";
				else if (supplyType.equalsIgnoreCase("EXPWOP"))
					return "EXPT";
				else if (supplyType.equalsIgnoreCase("EXPWP"))
					return "EXPWT";
				else
					return supplyType;
			} else if (tableType.equalsIgnoreCase("EXP")
					|| tableType.equalsIgnoreCase("EXPA")) {
				if (supplyType.equalsIgnoreCase("WPAY"))
					return "EXPT";
				else if (supplyType.equalsIgnoreCase("WOPAY"))
					return "EXPWT";
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

	public String convertToDocType(String tableType, String docType) {
		if (docType != null && tableType != null) {
			if (tableType.equalsIgnoreCase("CDNR")
					|| tableType.equalsIgnoreCase("CDNUR")) {
				if (docType.equalsIgnoreCase("C"))
					return "CR";
				else if (docType.equalsIgnoreCase("D"))
					return "DR";
				else
					return docType;
			} else if (tableType.equalsIgnoreCase("CDNRA")
					|| tableType.equalsIgnoreCase("CDNURA")) {
				if (docType.equalsIgnoreCase("C"))
					return "RCR";
				else if (docType.equalsIgnoreCase("D"))
					return "RDR";
				else
					return docType;
			} else
				return docType;
		} else {
			return null;
		}
	}
}
