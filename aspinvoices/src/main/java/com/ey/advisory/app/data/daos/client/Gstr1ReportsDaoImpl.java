package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr1ErrorReportView;
import com.ey.advisory.app.data.views.client.Gstr1InformationReportView;
import com.ey.advisory.app.data.views.client.Gstr1ProcessedReportView;
import com.ey.advisory.app.data.views.client.Gstr1TotRecReportView;
import com.ey.advisory.app.docs.dto.ReportSearchReqDto;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 *
 * @author Mohana.Dasari
 *
 */
@Component("Gstr1ReportsDaoImpl")
public class Gstr1ReportsDaoImpl implements Gstr1ReportsDao {
	private static final String PROCESSED_REPORTS_VIEW = "REPORTS/ASP_PROCESSED";
	private static final String ERROR_REPORTS_VIEW = "REPORTS/ASP_ERROR";
	private static final String TOTREC_REPORTS_VIEW = "REPORTS/ASP_TOTALREC";
	private static final String INFORMATION_REPORTS_VIEW = "REPORTS/ASP_INFORMATION";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Gstr1ProcessedReportView> getProcessedReports(
			ReportSearchReqDto request) {
		StringBuilder buildQuery = new StringBuilder();
		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String docNo = request.getDocNo();
		LocalDate date = request.getDate();
		if (receiveFromDate != null && receiveToDate != null && date != null) {
			buildQuery.append("RECEIVED_DATE BETWEEN ");
			buildQuery.append(
					":receiveFromDate AND :receiveToDate AND :date = RECEIVED_DATE");
		}

		if (docFromDate != null && docToDate != null && date != null) {
			buildQuery.append("DOC_DATE BETWEEN ");
			buildQuery
					.append(":docFromDate AND :docToDate AND :date = DOC_DATE");
		}

		if (returnFrom != null && returnTo != null) {
			buildQuery.append("DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":retPeriodFrom AND :retPeriodTo");
		}

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		if (request.getDocNo() != null) {
			buildQuery.append(" AND DOC_NUM = :docNo");
		}

		String queryStr = createQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			q.setParameter("gstin", request.getGstins());
		}

		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("retPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("retPeriodTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docNo != null) {
			q.setParameter("docNo", docNo);
		}
		if (date != null) {
			q.setParameter("date", date);
		}

		

		List<Object[]> list = q.getResultList();
		List<Gstr1ProcessedReportView> retList = list.parallelStream()
				.map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	@Override
	public List<Gstr1ErrorReportView> getErrorReports(
			ReportSearchReqDto request) {
		StringBuilder buildQuery = new StringBuilder();
		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String docNo = request.getDocNo();
		LocalDate date = request.getDate();
		if (receiveFromDate != null && receiveToDate != null && date != null) {
			buildQuery.append("RECEIVED_DATE BETWEEN ");
			buildQuery.append(
					":receiveFromDate AND :receiveToDate AND :date = RECEIVED_DATE");
		}

		if (docFromDate != null && docToDate != null && date != null) {
			buildQuery.append("DOC_DATE BETWEEN ");
			buildQuery
					.append(":docFromDate AND :docToDate AND :date = DOC_DATE");
		}

		if (returnFrom != null && returnTo != null) {
			buildQuery.append("DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":retPeriodFrom AND :retPeriodTo");
		}

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		if (request.getDocNo() != null) {
			buildQuery.append(" AND DOC_NUM = :docNo");
		}

		String queryStr = createErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			q.setParameter("gstin", request.getGstins());
		}

		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("retPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("retPeriodTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docNo != null) {
			q.setParameter("docNo", docNo);
		}
		if (date != null) {
			q.setParameter("date", date);
		}


		List<Object[]> list = q.getResultList();
		List<Gstr1ErrorReportView> retList = list.parallelStream()
				.map(o -> convertError(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	@Override
	public List<Gstr1InformationReportView> getInfoReports(
			ReportSearchReqDto request) {
		StringBuilder buildQuery = new StringBuilder();
		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String docNo = request.getDocNo();
		LocalDate date = request.getDate();
		if (receiveFromDate != null && receiveToDate != null && date != null) {
			buildQuery.append("RECEIVED_DATE BETWEEN ");
			buildQuery.append(
					":receiveFromDate AND :receiveToDate AND :date = RECEIVED_DATE");
		}

		if (docFromDate != null && docToDate != null && date != null) {
			buildQuery.append("DOC_DATE BETWEEN ");
			buildQuery
					.append(":docFromDate AND :docToDate AND :date = DOC_DATE");
		}

		if (returnFrom != null && returnTo != null) {
			buildQuery.append("DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":retPeriodFrom AND :retPeriodTo");
		}

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		if (request.getDocNo() != null) {
			buildQuery.append(" AND DOC_NUM = :docNo");
		}

		String queryStr = createInfoRecQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			q.setParameter("gstin", request.getGstins());
		}

		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("retPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("retPeriodTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docNo != null) {
			q.setParameter("docNo", docNo);
		}
		if (date != null) {
			q.setParameter("date", date);
		}

		List<Object[]> list = q.getResultList();
		List<Gstr1InformationReportView> retList = list.parallelStream()
				.map(o -> convertInfoRec(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	@Override
	public List<Gstr1TotRecReportView> getTotRecReports(
			ReportSearchReqDto request) {
		StringBuilder buildQuery = new StringBuilder();
		LocalDate receiveFromDate = request.getReceivFromDate();
		LocalDate receiveToDate = request.getReceivToDate();
		LocalDate docFromDate = request.getDocFromDate();
		LocalDate docToDate = request.getDocToDate();
		String returnFrom = request.getReturnFrom();
		String returnTo = request.getReturnTo();
		String docNo = request.getDocNo();
		LocalDate date = request.getDate();
		if (receiveFromDate != null && receiveToDate != null && date != null) {
			buildQuery.append("RECEIVED_DATE BETWEEN ");
			buildQuery.append(
					":receiveFromDate AND :receiveToDate AND :date = RECEIVED_DATE");
		}

		if (docFromDate != null && docToDate != null && date != null) {
			buildQuery.append("DOC_DATE BETWEEN ");
			buildQuery
					.append(":docFromDate AND :docToDate AND :date = DOC_DATE");
		}

		if (returnFrom != null && returnTo != null) {
			buildQuery.append("DERIVED_RET_PERIOD BETWEEN ");
			buildQuery.append(":retPeriodFrom AND :retPeriodTo");
		}

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			buildQuery.append(" AND SUPPLIER_GSTIN IN :gstin");
		}
		if (request.getDocNo() != null) {
			buildQuery.append(" AND DOC_NUM = :docNo");
		}

		String queryStr = createTotRecQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (request.getGstins() != null && request.getGstins().size() > 0) {
			q.setParameter("gstin", request.getGstins());
		}

		if (docFromDate != null && docToDate != null) {
			q.setParameter("docFromDate", docFromDate);
			q.setParameter("docToDate", docToDate);
		}
		if (returnFrom != null && returnTo != null) {
			int derivedRetPeriodFrom = GenUtil
					.convertTaxPeriodToInt(request.getReturnFrom());
			int derivedRetPeriodTo = GenUtil
					.convertTaxPeriodToInt(request.getReturnTo());
			q.setParameter("retPeriodFrom", derivedRetPeriodFrom);
			q.setParameter("retPeriodTo", derivedRetPeriodTo);
		}
		if (receiveFromDate != null && receiveToDate != null) {
			q.setParameter("receiveFromDate", receiveFromDate);
			q.setParameter("receiveToDate", receiveToDate);
		}
		if (docNo != null) {
			q.setParameter("docNo", docNo);
		}
		if (date != null) {
			q.setParameter("date", date);
		}

		List<Object[]> list = q.getResultList();
		List<Gstr1TotRecReportView> retList = list.parallelStream()
				.map(o -> convertTotRec(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Gstr1ProcessedReportView convert(Object[] arr) {
		Gstr1ProcessedReportView obj = new Gstr1ProcessedReportView();
		obj.setSupplierGstin((String) arr[0]);
		obj.setReturnPeriod((String) arr[1]);
		obj.setDerivedReturnPeriod((Integer) arr[2]);
		obj.setReceivedDate(((java.sql.Date) arr[3]).toLocalDate());
		obj.setSupplyType((String) arr[4]);
		obj.setDocType((String) arr[5]);
		obj.setDocNum((String) arr[6]);
		obj.setDocDate(((java.sql.Date) arr[7]).toLocalDate());
		obj.setDocAmount(arr[8] != null ? (BigDecimal) arr[8] : null);
		obj.setOrgDocNum((String) arr[9]);
		obj.setOrgDocDate(arr[10] != null
				? ((java.sql.Date) arr[10]).toLocalDate() : null);
		obj.setOrgCustGstin((String) arr[11]);
		obj.setTaxDocType((String) arr[12]);
		obj.setTableSection((String) arr[13]);
		obj.setUinOrComposition((String) arr[14]);
		obj.setCustomerGstin((String) arr[15]);
		obj.setCustomerName((String) arr[16]);
		obj.setBillToState((String) arr[17]);
		obj.setShipToState((String) arr[18]);
		obj.setShipBillNum((String) arr[19]);
		obj.setShipBillDate(arr[20] != null
				? ((java.sql.Date) arr[20]).toLocalDate() : null);
		obj.setShipPortCode((String) arr[21]);
		obj.setReverseCharge((String) arr[22]);
		obj.setEcomCustomerGstin((String) arr[23]);
		obj.setItcFlag(((Byte) arr[24]).toString());
		obj.setExportDuty(arr[25] != null ? (BigDecimal) arr[23] : null);
		obj.setCustomerCode((String) arr[26]);
		obj.setAccountingVoucherNo((String) arr[27]);
		obj.setAccountingVoucherDate(arr[28] != null
				? ((java.sql.Date) arr[28]).toLocalDate() : null);
		obj.setGlaAccountCode((String) arr[29]);
		obj.setFob(arr[30] != null ? (BigDecimal) arr[30] : null);
		obj.setTcsFlag(((Byte) arr[31]).toString());
		obj.setCrDrPreGst(((Byte) arr[32]).toString());
		obj.setCrDrReason((String) arr[33]);
		obj.setSourceIdentifier((String) arr[34]);
		obj.setSourceFileName((String) arr[35]);
		obj.setDivision((String) arr[36]);
		obj.setSubDivision((String) arr[37]);
		obj.setProfitCenter1((String) arr[38]);
		obj.setProfitCenter2((String) arr[39]);
		obj.setPlantCode((String) arr[40]);
		obj.setUserDefinied1((String) arr[41]);
		obj.setUserDefinied2((String) arr[42]);
		obj.setUserDefinied3((String) arr[43]);
		obj.setPos((String) arr[44]);
		obj.setGstnStatus((String) arr[45]);
		obj.setItemHsnSac((String) arr[46]);
		obj.setItemDesc((String) arr[47]);
		obj.setItemType((String) arr[48]);
		obj.setItemUqc((String) arr[49]);
		obj.setItemQuantity(arr[50] != null ? (BigDecimal) arr[50] : null);
		obj.setProductCode((String) arr[51]);
		obj.setTaxableValue(arr[52] != null ? (BigDecimal) arr[52] : null);
		obj.setIgstRate(arr[53] != null ? (BigDecimal) arr[53] : null);
		obj.setIgstAmount(arr[54] != null ? (BigDecimal) arr[54] : null);
		obj.setCgstRate(arr[55] != null ? (BigDecimal) arr[55] : null);
		obj.setCgstAmount(arr[56] != null ? (BigDecimal) arr[56] : null);
		obj.setSgstRate(arr[57] != null ? (BigDecimal) arr[57] : null);
		obj.setSgstAmount(arr[58] != null ? (BigDecimal) arr[58] : null);
		obj.setCessRateSpecific(arr[59] != null ? (BigDecimal) arr[59] : null);
		obj.setCessAmountSpecific(
				arr[60] != null ? (BigDecimal) arr[60] : null);
		obj.setCessRateAdv(arr[61] != null ? (BigDecimal) arr[61] : null);
		obj.setCessAmountAdv(arr[62] != null ? (BigDecimal) arr[62] : null);
		obj.setDocHeaderId((GenUtil.getBigInteger(arr[63])).longValue());
		obj.setItemNo((Integer) arr[64]);
		obj.setErrorCode((String) arr[65]);
		obj.setErrorType((String) arr[66]);
		obj.setErrorDesc((String) arr[67]);
		obj.setInfoCode((String) arr[68]);
		obj.setInfoDesc((String) arr[69]);
		return obj;
	}

	private Gstr1ErrorReportView convertError(Object[] arr) {
		Gstr1ErrorReportView obj = new Gstr1ErrorReportView();
		obj.setSupplierGstin((String) arr[0]);
		obj.setReturnPeriod((String) arr[1]);
		obj.setDerivedReturnPeriod((Integer) arr[2]);
		obj.setReceivedDate(((java.sql.Date) arr[3]).toLocalDate());
		obj.setSupplyType((String) arr[4]);
		obj.setDocType((String) arr[5]);
		obj.setDocNum((String) arr[6]);
		obj.setDocDate(((java.sql.Date) arr[7]).toLocalDate());
		obj.setDocAmount(arr[8] != null ? (BigDecimal) arr[8] : null);
		obj.setOrgDocNum((String) arr[9]);
		obj.setOrgDocDate(arr[10] != null
				? ((java.sql.Date) arr[10]).toLocalDate() : null);
		obj.setOrgCustGstin((String) arr[11]);
		obj.setTaxDocType((String) arr[12]);
		obj.setTableSection((String) arr[13]);
		obj.setUinOrComposition((String) arr[14]);
		obj.setCustomerGstin((String) arr[15]);
		obj.setCustomerName((String) arr[16]);
		obj.setBillToState((String) arr[17]);
		obj.setShipToState((String) arr[18]);
		obj.setShipBillNum((String) arr[19]);
		obj.setShipBillDate(arr[20] != null
				? ((java.sql.Date) arr[20]).toLocalDate() : null);
		obj.setShipPortCode((String) arr[21]);
		obj.setReverseCharge((String) arr[22]);
		obj.setEcomCustomerGstin((String) arr[23]);
		obj.setItcFlag(((Byte) arr[24]).toString());
		obj.setExportDuty(arr[25] != null ? (BigDecimal) arr[23] : null);
		obj.setCustomerCode((String) arr[26]);
		obj.setAccountingVoucherNo((String) arr[27]);
		obj.setAccountingVoucherDate(arr[28] != null
				? ((java.sql.Date) arr[28]).toLocalDate() : null);
		obj.setGlaAccountCode((String) arr[29]);
		obj.setFob(arr[30] != null ? (BigDecimal) arr[30] : null);
		obj.setTcsFlag(((Byte) arr[31]).toString());
		obj.setCrDrPreGst(((Byte) arr[32]).toString());
		obj.setCrDrReason((String) arr[33]);
		obj.setSourceIdentifier((String) arr[34]);
		obj.setSourceFileName((String) arr[35]);
		obj.setDivision((String) arr[36]);
		obj.setSubDivision((String) arr[37]);
		obj.setProfitCenter1((String) arr[38]);
		obj.setProfitCenter2((String) arr[39]);
		obj.setPlantCode((String) arr[40]);
		obj.setUserDefinied1((String) arr[41]);
		obj.setUserDefinied2((String) arr[42]);
		obj.setUserDefinied3((String) arr[43]);
		obj.setPos((String) arr[44]);
		obj.setGstnStatus((String) arr[45]);
		obj.setItemHsnSac((String) arr[46]);
		obj.setItemDesc((String) arr[47]);
		obj.setItemType((String) arr[48]);
		obj.setItemUqc((String) arr[49]);
		obj.setItemQuantity(arr[50] != null ? (BigDecimal) arr[50] : null);
		obj.setProductCode((String) arr[51]);
		obj.setTaxableValue(arr[52] != null ? (BigDecimal) arr[52] : null);
		obj.setIgstRate(arr[53] != null ? (BigDecimal) arr[53] : null);
		obj.setIgstAmount(arr[54] != null ? (BigDecimal) arr[54] : null);
		obj.setCgstRate(arr[55] != null ? (BigDecimal) arr[55] : null);
		obj.setCgstAmount(arr[56] != null ? (BigDecimal) arr[56] : null);
		obj.setSgstRate(arr[57] != null ? (BigDecimal) arr[57] : null);
		obj.setSgstAmount(arr[58] != null ? (BigDecimal) arr[58] : null);
		obj.setCessRateSpecific(arr[59] != null ? (BigDecimal) arr[59] : null);
		obj.setCessAmountSpecific(
				arr[60] != null ? (BigDecimal) arr[60] : null);
		obj.setCessRateAdv(arr[61] != null ? (BigDecimal) arr[61] : null);
		obj.setCessAmountAdv(arr[62] != null ? (BigDecimal) arr[62] : null);
		obj.setDocHeaderId((GenUtil.getBigInteger(arr[63])).longValue());
		obj.setItemNo((Integer) arr[64]);
		obj.setErrorCode((String) arr[65]);
		obj.setErrorType((String) arr[66]);
		obj.setErrorDesc((String) arr[67]);
		obj.setInfoCode((String) arr[68]);
		obj.setInfoDesc((String) arr[69]);
		return obj;
	}

	private Gstr1TotRecReportView convertTotRec(Object[] arr) {
		Gstr1TotRecReportView obj = new Gstr1TotRecReportView();
		obj.setSupplierGstin((String) arr[0]);
		obj.setReturnPeriod((String) arr[1]);
		obj.setDerivedReturnPeriod((Integer) arr[2]);
		obj.setReceivedDate(((java.sql.Date) arr[3]).toLocalDate());
		obj.setSupplyType((String) arr[4]);
		obj.setDocType((String) arr[5]);
		obj.setDocNum((String) arr[6]);
		obj.setDocDate(((java.sql.Date) arr[7]).toLocalDate());
		obj.setDocAmount(arr[8] != null ? (BigDecimal) arr[8] : null);
		obj.setOrgDocNum((String) arr[9]);
		obj.setOrgDocDate(arr[10] != null
				? ((java.sql.Date) arr[10]).toLocalDate() : null);
		obj.setOrgCustGstin((String) arr[11]);
		obj.setTaxDocType((String) arr[12]);
		obj.setTableSection((String) arr[13]);
		obj.setUinOrComposition((String) arr[14]);
		obj.setCustomerGstin((String) arr[15]);
		obj.setCustomerName((String) arr[16]);
		obj.setBillToState((String) arr[17]);
		obj.setShipToState((String) arr[18]);
		obj.setShipBillNum((String) arr[19]);
		obj.setShipBillDate(arr[20] != null
				? ((java.sql.Date) arr[20]).toLocalDate() : null);
		obj.setShipPortCode((String) arr[21]);
		obj.setReverseCharge((String) arr[22]);
		obj.setEcomCustomerGstin((String) arr[23]);
		obj.setItcFlag(((Byte) arr[24]).toString());
		obj.setExportDuty(arr[25] != null ? (BigDecimal) arr[23] : null);
		obj.setCustomerCode((String) arr[26]);
		obj.setAccountingVoucherNo((String) arr[27]);
		obj.setAccountingVoucherDate(arr[28] != null
				? ((java.sql.Date) arr[28]).toLocalDate() : null);
		obj.setGlaAccountCode((String) arr[29]);
		obj.setFob(arr[30] != null ? (BigDecimal) arr[30] : null);
		obj.setTcsFlag(((Byte) arr[31]).toString());
		obj.setCrDrPreGst(((Byte) arr[32]).toString());
		obj.setCrDrReason((String) arr[33]);
		obj.setSourceIdentifier((String) arr[34]);
		obj.setSourceFileName((String) arr[35]);
		obj.setDivision((String) arr[36]);
		obj.setSubDivision((String) arr[37]);
		obj.setProfitCenter1((String) arr[38]);
		obj.setProfitCenter2((String) arr[39]);
		obj.setPlantCode((String) arr[40]);
		obj.setUserDefinied1((String) arr[41]);
		obj.setUserDefinied2((String) arr[42]);
		obj.setUserDefinied3((String) arr[43]);
		obj.setPos((String) arr[44]);
		obj.setGstnStatus((String) arr[45]);
		obj.setItemHsnSac((String) arr[46]);
		obj.setItemDesc((String) arr[47]);
		obj.setItemType((String) arr[48]);
		obj.setItemUqc((String) arr[49]);
		obj.setItemQuantity(arr[50] != null ? (BigDecimal) arr[50] : null);
		obj.setProductCode((String) arr[51]);
		obj.setTaxableValue(arr[52] != null ? (BigDecimal) arr[52] : null);
		obj.setIgstRate(arr[53] != null ? (BigDecimal) arr[53] : null);
		obj.setIgstAmount(arr[54] != null ? (BigDecimal) arr[54] : null);
		obj.setCgstRate(arr[55] != null ? (BigDecimal) arr[55] : null);
		obj.setCgstAmount(arr[56] != null ? (BigDecimal) arr[56] : null);
		obj.setSgstRate(arr[57] != null ? (BigDecimal) arr[57] : null);
		obj.setSgstAmount(arr[58] != null ? (BigDecimal) arr[58] : null);
		obj.setCessRateSpecific(arr[59] != null ? (BigDecimal) arr[59] : null);
		obj.setCessAmountSpecific(
				arr[60] != null ? (BigDecimal) arr[60] : null);
		obj.setCessRateAdv(arr[61] != null ? (BigDecimal) arr[61] : null);
		obj.setCessAmountAdv(arr[62] != null ? (BigDecimal) arr[62] : null);
		obj.setDocHeaderId((GenUtil.getBigInteger(arr[63])).longValue());
		obj.setItemNo((Integer) arr[64]);
		obj.setErrorCode((String) arr[65]);
		obj.setErrorType((String) arr[66]);
		obj.setErrorDesc((String) arr[67]);
		obj.setInfoCode((String) arr[68]);
		obj.setInfoDesc((String) arr[69]);
		return obj;
	}

	private Gstr1InformationReportView convertInfoRec(Object[] arr) {
		Gstr1InformationReportView obj = new Gstr1InformationReportView();
		obj.setSupplierGstin((String) arr[0]);
		obj.setReturnPeriod((String) arr[1]);
		obj.setDerivedReturnPeriod((Integer) arr[2]);
		obj.setReceivedDate(((java.sql.Date) arr[3]).toLocalDate());
		obj.setSupplyType((String) arr[4]);
		obj.setDocType((String) arr[5]);
		obj.setDocNum((String) arr[6]);
		obj.setDocDate(((java.sql.Date) arr[7]).toLocalDate());
		obj.setDocAmount(arr[8] != null ? (BigDecimal) arr[8] : null);
		obj.setOrgDocNum((String) arr[9]);
		obj.setOrgDocDate(arr[10] != null
				? ((java.sql.Date) arr[10]).toLocalDate() : null);
		obj.setOrgCustGstin((String) arr[11]);
		obj.setTaxDocType((String) arr[12]);
		obj.setTableSection((String) arr[13]);
		obj.setUinOrComposition((String) arr[14]);
		obj.setCustomerGstin((String) arr[15]);
		obj.setCustomerName((String) arr[16]);
		obj.setBillToState((String) arr[17]);
		obj.setShipToState((String) arr[18]);
		obj.setShipBillNum((String) arr[19]);
		obj.setShipBillDate(arr[20] != null
				? ((java.sql.Date) arr[20]).toLocalDate() : null);
		obj.setShipPortCode((String) arr[21]);
		obj.setReverseCharge((String) arr[22]);
		obj.setEcomCustomerGstin((String) arr[23]);
		obj.setItcFlag(((Byte) arr[24]).toString());
		obj.setExportDuty(arr[25] != null ? (BigDecimal) arr[23] : null);
		obj.setCustomerCode((String) arr[26]);
		obj.setAccountingVoucherNo((String) arr[27]);
		obj.setAccountingVoucherDate(arr[28] != null
				? ((java.sql.Date) arr[28]).toLocalDate() : null);
		obj.setGlaAccountCode((String) arr[29]);
		obj.setFob(arr[30] != null ? (BigDecimal) arr[30] : null);
		obj.setTcsFlag(((Byte) arr[31]).toString());
		obj.setCrDrPreGst(((Byte) arr[32]).toString());
		obj.setCrDrReason((String) arr[33]);
		obj.setSourceIdentifier((String) arr[34]);
		obj.setSourceFileName((String) arr[35]);
		obj.setDivision((String) arr[36]);
		obj.setSubDivision((String) arr[37]);
		obj.setProfitCenter1((String) arr[38]);
		obj.setProfitCenter2((String) arr[39]);
		obj.setPlantCode((String) arr[40]);
		obj.setUserDefinied1((String) arr[41]);
		obj.setUserDefinied2((String) arr[42]);
		obj.setUserDefinied3((String) arr[43]);
		obj.setPos((String) arr[44]);
		obj.setGstnStatus((String) arr[45]);
		obj.setItemHsnSac((String) arr[46]);
		obj.setItemDesc((String) arr[47]);
		obj.setItemType((String) arr[48]);
		obj.setItemUqc((String) arr[49]);
		obj.setItemQuantity(arr[50] != null ? (BigDecimal) arr[50] : null);
		obj.setProductCode((String) arr[51]);
		obj.setTaxableValue(arr[52] != null ? (BigDecimal) arr[52] : null);
		obj.setIgstRate(arr[53] != null ? (BigDecimal) arr[53] : null);
		obj.setIgstAmount(arr[54] != null ? (BigDecimal) arr[54] : null);
		obj.setCgstRate(arr[55] != null ? (BigDecimal) arr[55] : null);
		obj.setCgstAmount(arr[56] != null ? (BigDecimal) arr[56] : null);
		obj.setSgstRate(arr[57] != null ? (BigDecimal) arr[57] : null);
		obj.setSgstAmount(arr[58] != null ? (BigDecimal) arr[58] : null);
		obj.setCessRateSpecific(arr[59] != null ? (BigDecimal) arr[59] : null);
		obj.setCessAmountSpecific(
				arr[60] != null ? (BigDecimal) arr[60] : null);
		obj.setCessRateAdv(arr[61] != null ? (BigDecimal) arr[61] : null);
		obj.setCessAmountAdv(arr[62] != null ? (BigDecimal) arr[62] : null);
		obj.setDocHeaderId((GenUtil.getBigInteger(arr[63])).longValue());
		obj.setItemNo((Integer) arr[64]);
		obj.setErrorCode((String) arr[65]);
		obj.setErrorType((String) arr[66]);
		obj.setErrorDesc((String) arr[67]);
		obj.setInfoCode((String) arr[68]);
		obj.setInfoDesc((String) arr[69]);
		return obj;
	}

	private String createQueryString(String buildQuery) {
		String queryStr = "SELECT * FROM \"" + PROCESSED_REPORTS_VIEW
				+ "\" WHERE " + buildQuery;
		return queryStr;
	}

	private String createErrorQueryString(String buildQuery) {
		String queryStr = "SELECT * FROM \"" + ERROR_REPORTS_VIEW + "\" WHERE "
				+ buildQuery;
		return queryStr;
	}

	private String createTotRecQueryString(String buildQuery) {
		String queryStr = "SELECT * FROM \"" + TOTREC_REPORTS_VIEW + "\" WHERE "
				+ buildQuery;
		return queryStr;
	}

	private String createInfoRecQueryString(String buildQuery) {
		String queryStr = "SELECT * FROM \"" + INFORMATION_REPORTS_VIEW
				+ "\" WHERE " + buildQuery;
		return queryStr;
	}

}
