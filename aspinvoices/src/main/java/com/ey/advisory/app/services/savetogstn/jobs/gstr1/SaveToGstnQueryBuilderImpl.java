package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("saveToGstnQueryBuilderImpl")
public class SaveToGstnQueryBuilderImpl implements SaveToGstnQueryBuilder {

	@Autowired
	private UserCriteriaBuilder userCriteriaBuilder;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	public static void main(String[] args) {
	
		StringBuilder build = new StringBuilder();
		if (true) {
			build.append(" AND doc.sgstin = :o_Gstin");
			build.append(" AND doc.taxperiod = :o_RetPeriod");
			build.append(" AND doc2.sgstin = :c_Gstin");
			build.append(" AND doc2.taxperiod = :c_RetPeriod");
			if (true) {
				build.append(" AND doc.supplyType <> :o_SupplyType");
				build.append(" AND doc2.supplyType = :c_SupplyType");
			}

			if (true) {
				build.append(" AND doc2.id IN :c_docIds");
			}
		}
		String str = "SELECT doc.id, doc.gstnBifurcation,doc2.id FROM "
				+ "OutwardTransDocument doc,OutwardTransDocument doc2 "
				+ "WHERE doc.id <> doc2.id AND doc.docKey=doc2.docKey "
				+ "AND doc.isDeleted = TRUE AND doc2.isDeleted = FALSE "
				+ "AND doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
				+ "AND doc2.complianceApplicable = TRUE AND doc2.aspInvoiceStatus = 2 "
				+ "AND doc.isSent = TRUE AND doc2.isSent = FALSE " + build
				+ " ORDER BY doc.gstnBifurcation, doc.id";
		System.out.println(str);
	}

	@Override
	public String buildGstr1CancelledQuery(String gstin, String retPeriod,
			String docType, List<Long> docIds, ProcessingContext context) {

		LOGGER.debug("inside buildCancelledQuery method with args {}{}", gstin,
				retPeriod);

		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1CancelledUserCriteria(gstin, retPeriod, docType,
						docIds);
		String returnType = GenUtil.getReturnType(context);

		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.gstnBifurcation,doc2.id FROM "
					+ "Gstr1AOutwardTransDocument doc,Gstr1AOutwardTransDocument doc2 "
					+ "WHERE doc.id <> doc2.id AND doc.docKey=doc2.docKey "
					+ "AND doc.isDeleted = TRUE AND doc2.isDeleted = FALSE "
					+ "AND doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
					+ "AND doc2.complianceApplicable = TRUE AND doc2.aspInvoiceStatus = 2 "
					+ "AND doc.isSent = TRUE AND doc2.isSent = FALSE "
					+ criteria + " ORDER BY doc.gstnBifurcation, doc.id";
		}

		return "SELECT doc.id, doc.gstnBifurcation,doc2.id FROM "
				+ "OutwardTransDocument doc,OutwardTransDocument doc2 "
				+ "WHERE doc.id <> doc2.id AND doc.docKey=doc2.docKey "
				+ "AND doc.isDeleted = TRUE AND doc2.isDeleted = FALSE "
				+ "AND doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
				+ "AND doc2.complianceApplicable = TRUE AND doc2.aspInvoiceStatus = 2 "
				+ "AND doc.isSent = TRUE AND doc2.isSent = FALSE " + criteria
				+ " ORDER BY doc.gstnBifurcation, doc.id";

	}

	@Override
	public String buildGstr1B2bB2baQuery(String gstin, String retPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildB2bB2baQuery method with args {}{}",
					gstin, retPeriod);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, orgDocIds, docIds, context);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			String query = "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
					+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
					+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
					+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
					+ "doc.id,doc.supplyType,doc.docType,doc.docDate,"
					+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
					+ "doc.aspInvoiceStatus = 2 " + criteria
					+ " ORDER BY doc.sgstin, "
					+ "doc.taxperiod, doc.cgstin, doc.id";
			return query;

		} else {
			if (!(boolean) context.getAttribute("isMultiSupplyOpted")) {
				return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
						+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
						+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
						+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
						+ "doc.id,doc.supplyType,doc.docType,doc.docDate,"
						+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.cgstin, doc.id";
			} else {
				// AND RATE.ITM_TAX_DOC_TYPE :docType
				String query = "SELECT doc.sgstin, doc.taxperiod,"
						+ " doc.cgstin, doc.docNo, doc.docAmount, "
						+ "doc.pos, doc.reverseCharge, doc.egstin, doc.diffPercent,"
						+ " doc.section7OfIgstFlag, rate.taxRate, "
						+ "rate.taxValue AS taxableValue,"
						+ " rate.igstAmt, rate.cgstAmt, rate.sgstAmt, rate.cessAmt, doc.id, "
						+ "doc.supplyType AS supplyType,"
						+ " doc.docType, doc.docDate, doc.preceedingInvoiceNumber, doc.preceedingInvoiceDate "
						+ "FROM OutwardTransDocument doc JOIN DocRateSummary "
						+ "rate ON doc.id=rate.docHeaderId WHERE "
						+ "doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
						+ criteria
						+ " ORDER BY doc.sgstin, doc.taxperiod, doc.cgstin, doc.id";
				return query;

			}
		}
	}

	@Override
	public String buildGstr1B2clB2claQuery(String gstin, String retPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildB2clB2claQuery method with args {}{}",
					gstin, retPeriod);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, orgDocIds, docIds, context);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
					+ "doc.pos, doc.egstin, doc.diffPercent,doc.section7OfIgstFlag, "
					+ "rate.taxRate, rate.taxValue, rate.igstAmt, rate.cessAmt, "
					+ "doc.id, doc.docDate, doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate, doc.docType FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
					+ "doc.aspInvoiceStatus = 2 " + criteria
					+ " ORDER BY doc.sgstin, "
					+ "doc.taxperiod, doc.pos, doc.id";

		} else {
			if (!(boolean) context.getAttribute("isMultiSupplyOpted")) {
				return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
						+ "doc.pos, doc.egstin, doc.diffPercent,doc.section7OfIgstFlag, "
						+ "rate.taxRate, rate.taxValue, rate.igstAmt, rate.cessAmt, "
						+ "doc.id, doc.docDate, doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate, doc.docType FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.pos, doc.id";
			} else {
				return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
						+ "doc.pos, doc.egstin, doc.diffPercent,doc.section7OfIgstFlag, "
						+ "rate.taxRate, rate.taxValue AS taxableValue,"
						+ " rate.igstAmt, rate.cessAmt,doc.id, doc.docDate, doc.preceedingInvoiceNumber,"
						+ "doc.preceedingInvoiceDate, doc.docType FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId "
						+ " WHERE "
						+ "doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.pos, doc.id";
			}
		}
	}

	@Override
	public String buildGstr1ExpExpaQuery(String gstin, String retPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildExpExpaQuery method with args {}{}",
					gstin, retPeriod);
		}

		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, orgDocIds, docIds, context);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
					+ "doc.diffPercent, rate.taxRate, rate.taxValue, rate.igstAmt, "
					+ "rate.cessAmt, doc.id, doc.docDate, doc.preceedingInvoiceNumber,"
					+ "doc.preceedingInvoiceDate, doc.portCode, doc.shippingBillNo, "
					+ "doc.shippingBillDate, doc.supplyType FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
					+ "doc.aspInvoiceStatus = 2 " + criteria
					+ " ORDER BY doc.sgstin, "
					+ "doc.taxperiod, doc.supplyType, doc.id";
		} else {
			if (!(boolean) context.getAttribute("isMultiSupplyOpted")) {
				return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
						+ "doc.diffPercent, rate.taxRate, rate.taxValue, rate.igstAmt, "
						+ "rate.cessAmt, doc.id, doc.docDate, doc.preceedingInvoiceNumber,"
						+ "doc.preceedingInvoiceDate, doc.portCode, doc.shippingBillNo, "
						+ "doc.shippingBillDate, doc.supplyType FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.supplyType, doc.id";
			} else {
				return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
						+ "doc.diffPercent, rate.taxRate, "
						+ "rate.taxValue AS taxableValue, rate.igstAmt, "
						+ "rate.cessAmt, doc.id, doc.docDate, doc.preceedingInvoiceNumber,"
						+ "doc.preceedingInvoiceDate, doc.portCode, doc.shippingBillNo, "
						+ "doc.shippingBillDate, doc.supplyType AS supplyType FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId "
						+ " WHERE "
						+ "doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
						+ criteria + " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.supplyType, doc.id";
			}
		}

	}

	@Override
	public String buildGstr1CdnQuery(String gstin, String retPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {

		LOGGER.debug("inside buildGstr1CdnQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, orgDocIds, docIds, context);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
					+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
					+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
					+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
					+ "doc.id,doc.supplyType,doc.taxPayable,doc.docDate,"
					+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate,doc.crDrPreGst, "
					+ "doc.docType, doc.gstnBifurcation,doc.shippingBillNo,"
					+ "doc.shippingBillDate FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
					+ "doc.aspInvoiceStatus = 2 " + criteria
					+ " ORDER BY doc.sgstin, "
					+ "doc.taxperiod, doc.cgstin, doc.id";
		} else {
			if (!(boolean) context.getAttribute("isMultiSupplyOpted")) {
				return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
						+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
						+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
						+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
						+ "doc.id,doc.supplyType,doc.taxPayable,doc.docDate,"
						+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate,doc.crDrPreGst, "
						+ "doc.docType, doc.gstnBifurcation,doc.shippingBillNo,"
						+ "doc.shippingBillDate FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.cgstin, doc.id";
			} else {
				return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
						+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
						+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,"
						+ " rate.taxValue AS taxableValue,"
						+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
						+ "doc.id," + "doc.supplyType AS supplyType,"
						+ "doc.taxPayable,doc.docDate,"
						+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate,doc.crDrPreGst, "
						+ "doc.docType, doc.gstnBifurcation,doc.shippingBillNo,"
						+ "doc.shippingBillDate FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId "
						+ "  WHERE "
						+ "doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
						+ criteria + " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.cgstin, doc.id";

			}
		}
	}

	@Override
	public String buildGstr1EcomQuery(String gstin, String retPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildExpExpaQuery method with args {}{}",
					gstin, retPeriod);
		}

		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, orgDocIds, docIds, context);
		String returnType = GenUtil.getReturnType(context);

		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
					+ "doc.docAmount,doc.pos,doc.egstin,doc.id,doc.supplyType,doc.docType,doc.docDate,doc.tableType,"
					+ "rate.taxRate,rate.taxValue,"
					+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
					+ "doc.section7OfIgstFlag FROM "
					+ "Gstr1AOutwardTransDocument doc JOIN Gstr1ADocRateSummary rate "
					+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
					+ "doc.aspInvoiceStatus = 2 " + criteria
					+ " ORDER BY doc.sgstin, "
					+ "doc.taxperiod, doc.cgstin, doc.id";

		} else {
			if (!(boolean) context.getAttribute("isMultiSupplyOpted")) {
				return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
						+ "doc.docAmount,doc.pos,doc.egstin,doc.id,doc.supplyType,doc.docType,doc.docDate,doc.tableType,"
						+ "rate.taxRate,rate.taxValue,"
						+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
						+ "doc.section7OfIgstFlag FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
						+ "doc.aspInvoiceStatus = 2 " + criteria
						+ " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.cgstin, doc.id";
			} else {
				//TODO
				return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
						+ "doc.docAmount,doc.pos,doc.egstin,doc.id,"
						+ " doc.supplyType AS supplyType,doc.docType,doc.docDate,"
						+ " doc.tableType AS TABLE_SECTION,"
						+ "rate.taxRate, rate.taxValue AS taxableValue,"
						+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
						+ "doc.section7OfIgstFlag FROM "
						+ "OutwardTransDocument doc JOIN DocRateSummary rate "
						+ "ON doc.id=rate.docHeaderId "
						+ " WHERE "
						+ "doc.complianceApplicable = TRUE AND doc.aspInvoiceStatus = 2 "
						+ criteria + " ORDER BY doc.sgstin, "
						+ "doc.taxperiod, doc.cgstin, doc.id";
			}
		}
	}

	@Override
	public String buildGstr1B2csB2csaQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildB2csB2csaQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);

		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin, doc.returnPeriod, doc.pos, doc.eGstin, "
					+ "doc.diffPercent, doc.rate, doc.taxableValue, "
					+ "doc.igst, doc.cgst, doc.sgst, doc.cess, doc.id, doc.type, "
					+ "doc.orgMonth FROM Gstr1AB2csProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.orgMonth, doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.eGstin, doc.id";
		} else {
			return "SELECT doc.sgstin, doc.returnPeriod, doc.pos, doc.eGstin, "
					+ "doc.diffPercent, doc.rate, doc.taxableValue, "
					+ "doc.igst, doc.cgst, doc.sgst, doc.cess, doc.id, doc.type, "
					+ "doc.orgMonth FROM B2csProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.orgMonth, doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.eGstin, doc.id";
		}

	}

	@Override
	public String buildGstr1TxpTxpaQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildTxpTxpaQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);

		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.rate, doc.adjamt, doc.diffPercent, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess, doc.orgMonth FROM Gstr1ATxpProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.orgMonth, doc.sgstin, doc.returnPeriod, doc.id";
		} else {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.rate, doc.adjamt, doc.diffPercent, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess, doc.orgMonth FROM TxpProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.orgMonth, doc.sgstin, doc.returnPeriod, doc.id";
		}
	}

	@Override
	public String buildGstr1SupEcomQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildGstr1SupEcomQuery method with args {}{}",
				gstin, retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);

		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,doc.gstnBifurcation,doc.tableSection, doc.ecomGstin, "
					+ "doc.taxableValue, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess FROM Gstr1ASupEcomProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.id";
		} else {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,doc.gstnBifurcation,doc.tableSection, doc.ecomGstin, "
					+ "doc.taxableValue, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess FROM SupEcomProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.id";
		}
	}

	@Override
	public String buildGstr1EcomSumQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildExpExpaQuery method with args {}{}",
					gstin, retPeriod);
		}
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,doc.gstnBifurcation,doc.tableSection, doc.ecomGstin, "
					+ "doc.taxableValue, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess,doc.pos,doc.rate FROM Gstr1AEcomSupSummProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.id";
		} else {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,doc.gstnBifurcation,doc.tableSection, doc.ecomGstin, "
					+ "doc.taxableValue, doc.igst, doc.cgst, "
					+ "doc.sgst, doc.cess,doc.pos,doc.rate FROM EcomSupSummProcEntity doc WHERE "
					+ " doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.id";
		}
	}

	//HSN
	@Override
	public String buildGstr1HsnSumQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildHsnSumQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.returnPeriod, doc.sgstin, doc.itmhsnsac, "
					+ "doc.itmdesc, doc.itmuqc, doc.itmqty, doc.taxableval,"
					+ "doc.igst, doc.cgst, doc.sgst, doc.cess, doc.totalvalue, "
					+ "doc.taxRate, doc.recordType "
					+ " FROM Gstr1AHsnProcEntity doc WHERE doc.isDelete = FALSE "
					+ criteria + " ORDER BY "
					+ "doc.sgstin, doc.returnPeriod, doc.id";
		} else {
			return "SELECT doc.id, doc.returnPeriod, doc.sgstin, doc.itmhsnsac, "
					+ "doc.itmdesc, doc.itmuqc, doc.itmqty, doc.taxableval,"
					+ "doc.igst, doc.cgst, doc.sgst, doc.cess, doc.totalvalue, "
					+ "doc.taxRate,doc.recordType "
					+ " FROM HsnProcEntity doc WHERE doc.isDelete = FALSE "
					+ criteria + " ORDER BY "
					+ "doc.sgstin, doc.returnPeriod, doc.id";
		}

	}

	@Override
	public String buildGstr1NilQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildNilQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.returnPeriod, doc.sgstin, doc.table, "
					+ "doc.supplyType, doc.amt FROM Gstr1ANilProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.table, doc.supplyType, doc.id";
		} else {
			return "SELECT doc.id, doc.returnPeriod, doc.sgstin, doc.table, "
					+ "doc.supplyType, doc.amt FROM NilProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.table, doc.supplyType, doc.id";
		}
	}

	@Override
	public String buildGstr1AtAtaQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildAtAtaQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.sgstin, doc.returnPeriod, doc.pos, doc.diffPercent, "
					+ "doc.rate, doc.adAmt, doc.igst, doc.cgst, doc.sgst, doc.cess, "
					+ "doc.id, doc.orgMonth FROM Gstr1AAtProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.igst,doc.diffPercent, doc.id";
		} else {
			return "SELECT doc.sgstin, doc.returnPeriod, doc.pos, doc.diffPercent, "
					+ "doc.rate, doc.adAmt, doc.igst, doc.cgst, doc.sgst, doc.cess, "
					+ "doc.id, doc.orgMonth FROM AtProcEntity doc WHERE "
					+ "doc.isDelete = FALSE " + criteria
					+ " ORDER BY doc.sgstin, doc.returnPeriod, doc.pos, "
					+ "doc.igst,doc.diffPercent, doc.id";
		}

	}

	@Override
	public String buildGstr1DocIssuedQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context) {

		LOGGER.debug("inside buildDocIssuedQuery method with args {}{}", gstin,
				retPeriod);
		String criteria = userCriteriaBuilder.buildGstr1VeritalUserCriteria(
				gstin, retPeriod, docType, tableType);
		String returnType = GenUtil.getReturnType(context);
		if (returnType.equalsIgnoreCase(APIConstants.GSTR1A)) {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,"
					+ "doc.serialNo, doc.natureOfDocument, doc.from, "
					+ "doc.to, doc.totalNumber, doc.cancelled, "
					+ "doc.netNumber, doc.invoiceKey FROM "
					+ "Gstr1AInvoiceFileUploadEntity doc WHERE doc.isDelete = FALSE "
					+ criteria + " ORDER BY doc.sgstin, doc.returnPeriod, "
					+ "doc.serialNo";
		} else {
			return "SELECT doc.id, doc.sgstin, doc.returnPeriod,"
					+ "doc.serialNo, doc.natureOfDocument, doc.from, "
					+ "doc.to, doc.totalNumber, doc.cancelled, "
					+ "doc.netNumber, doc.invoiceKey FROM "
					+ "Gstr1InvoiceFileUploadEntity doc WHERE doc.isDelete = FALSE "
					+ criteria + " ORDER BY doc.sgstin, doc.returnPeriod, "
					+ "doc.serialNo";
		}
	}

	// --------------------GSTR1 Dependent Retry ----------------------//

	@Override
	public String buildGstr1DependentRetryB2bB2baQuery(Long batchId,
			Set<String> errorCodes) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"inside buildGstr1DependentRetryB2bB2baQuery method with args {}{}",
					batchId, errorCodes);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1DependentRetryHorizontalUserCriteria(batchId,
						errorCodes);

		return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
				+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
				+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
				+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
				+ "doc.id,doc.supplyType,doc.docType,doc.docDate,"
				+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate FROM "
				+ "OutwardTransDocument doc JOIN DocRateSummary rate "
				+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
				+ "doc.aspInvoiceStatus = 2 " + criteria
				+ " ORDER BY doc.sgstin, "
				+ "doc.taxperiod, doc.cgstin, doc.id";

	}

	@Override
	public String buildGstr1DependentRetryB2clB2claQuery(Long batchId,
			Set<String> errorCodes) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"inside buildGstr1DependentRetryB2clB2claQuery method with args {}{}",
					batchId, errorCodes);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1DependentRetryHorizontalUserCriteria(batchId,
						errorCodes);

		return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
				+ "doc.pos, doc.egstin, doc.diffPercent,doc.section7OfIgstFlag, "
				+ "rate.taxRate, rate.taxValue, rate.igstAmt, rate.cessAmt, "
				+ "doc.id, doc.docDate, doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate, doc.docType FROM "
				+ "OutwardTransDocument doc JOIN DocRateSummary rate "
				+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
				+ "doc.aspInvoiceStatus = 2 " + criteria
				+ " ORDER BY doc.sgstin, " + "doc.taxperiod, doc.pos, doc.id";

	}

	@Override
	public String buildGstr1DependentRetryExpExpaQuery(Long batchId,
			Set<String> errorCodes) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"inside buildGstr1DependentRetryExpExpaQuery method with args {}{}",
					batchId, errorCodes);
		}

		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1DependentRetryHorizontalUserCriteria(batchId,
						errorCodes);

		return "SELECT doc.sgstin, doc.taxperiod, doc.docNo, doc.docAmount, "
				+ "doc.diffPercent, rate.taxRate, rate.taxValue, rate.igstAmt, "
				+ "rate.cessAmt, doc.id, doc.docDate, doc.preceedingInvoiceNumber,"
				+ "doc.preceedingInvoiceDate, doc.portCode, doc.shippingBillNo, "
				+ "doc.shippingBillDate, doc.supplyType FROM "
				+ "OutwardTransDocument doc JOIN DocRateSummary rate "
				+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
				+ "doc.aspInvoiceStatus = 2 " + criteria
				+ " ORDER BY doc.sgstin, "
				+ "doc.taxperiod, doc.supplyType, doc.id";

	}

	@Override
	public String buildGstr1DependentRetryCdnQuery(Long batchId,
			Set<String> errorCodes) {

		LOGGER.debug(
				"inside buildGstr1DependentRetryCdnQuery method with args {}{}",
				batchId, errorCodes);
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr1DependentRetryHorizontalUserCriteria(batchId,
						errorCodes);

		return "SELECT doc.sgstin,doc.taxperiod,doc.cgstin,doc.docNo,"
				+ "doc.docAmount,doc.pos,doc.reverseCharge,doc.egstin,"
				+ "doc.diffPercent,doc.section7OfIgstFlag,rate.taxRate,rate.taxValue,"
				+ "rate.igstAmt,rate.cgstAmt,rate.sgstAmt,rate.cessAmt,"
				+ "doc.id,doc.supplyType,doc.taxPayable,doc.docDate,"
				+ "doc.preceedingInvoiceNumber,doc.preceedingInvoiceDate,doc.crDrPreGst, "
				+ "doc.docType, doc.gstnBifurcation,doc.shippingBillNo,"
				+ "doc.shippingBillDate FROM "
				+ "OutwardTransDocument doc JOIN DocRateSummary rate "
				+ "ON doc.id=rate.docHeaderId AND doc.complianceApplicable = TRUE AND "
				+ "doc.aspInvoiceStatus = 2 " + criteria
				+ " ORDER BY doc.sgstin, "
				+ "doc.taxperiod, doc.cgstin, doc.id";

	}

	// --------------------------- ANX1
	// ---------------------------------------//

	@Override
	public String buildAnx1CancelledQuery(String gstin, String retPeriod,
			String docType) {

		LOGGER.debug("inside buildAnx1CancelledQuery method with args {}{}",
				gstin, retPeriod);
		// Assuming docType is CAN
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1CancelledUserCriteria(gstin, retPeriod, docType);
		return "SELECT doc.id, doc.gstnBifurcationNew FROM "
				+ "OutwardTransDocument doc WHERE doc.docKey IN (SELECT "
				+ "doc2.docKey FROM OutwardTransDocument doc2 WHERE "
				+ "doc2.isDeleted = FALSE " + criteria + ") AND "
				+ "doc.supplyType NOT IN ('CAN') AND doc.isSent = "
				+ "TRUE AND doc.isDeleted = TRUE ORDER BY "
				+ "doc.gstnBifurcationNew, doc.id";

	}

	// Section - 3A and TaxDocType - B2C
	@Override
	public String buildAnx1B2cQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildB2cQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1VeritalUserCriteria(gstin, retPeriod, docType,
						tableType);

		return "SELECT ID, SUPPLIER_GSTIN, RETURN_PERIOD, DIFF_PERCENT, "
				+ "SECTION7_OF_IGST_FLAG, RFNDELG, POS, TAX_RATE, "
				+ "TAXABLE_VALUE, IGST, CGST, SGST, CESS "
				+ "FROM ANXSAVE_B2C WHERE IS_SENT_TO_GSTN = FALSE " + criteria
				+ " ORDER BY ID";

	}

	// Section - 3B and TaxDocType - B2B
	@Override
	public String buildAnx1B2bQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildB2bQuery method with args {}{}", gstin,
				retPeriod);

		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, "
				+ "ITM.CUST_GSTIN, HDR.DOC_DATE, HDR.DOC_NUM, HDR.DOC_AMT, "
				+ "HDR.DOC_TYPE, HDR.DIFF_PERCENT,HDR.SECTION7_OF_IGST_FLAG, "
				+ "HDR.AUTOPOPULATE_TO_REFUND AS RFNDELG, HDR.POS, "
				+ "ITM.ITM_HSNSAC, ITM.TAX_RATE, (SUM(ITM.TAXABLE_VALUE) OVER "
				+ "(PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) AS TAXABLE_VALUE, CASE WHEN "
				+ "HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_IGST"
				+ ") OVER (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE,ITM.ITM_HSNSAC)) ELSE (SUM ("
				+ "ITM.IGST_AMT) OVER (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS IGST,"
				+ " CASE WHEN HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM ("
				+ "ITM.MEMO_VALUE_CGST) OVER (PARTITION BY HDR.ID,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) ELSE (SUM (ITM.CGST_AMT) OVER (PARTITION "
				+ "BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) END AS CGST, CASE WHEN HDR.DOC_TYPE IN("
				+ "'INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_SGST) OVER ("
				+ "PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) ELSE (SUM (ITM.SGST_AMT) OVER"
				+ " (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS SGST, CASE WHEN "
				+ "HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_CESS"
				+ ") OVER (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE,ITM.ITM_HSNSAC)) ELSE (SUM("
				+ "ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM) OVER ("
				+ "PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS CESS FROM "
				+ "ANX_OUTWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID= ITM.DOC_HEADER_ID "
				+ "WHERE IS_PROCESSED= TRUE " + criteria + " ORDER BY HDR.ID";
	}

	// Section - 3C and TaxDocType - EXPT --> with tax
	// Section - 3D and TaxDocType - EXPWT --> without tax
	@Override
	public String buildAnx1ExpwpAndExpwopQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildExpwpAndExpwopQuery method with args {}{}",
				gstin, retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, "
				+ "HDR.DOC_TYPE, HDR.DOC_DATE, HDR.DOC_NUM, HDR.DOC_AMT, "
				+ "HDR.AUTOPOPULATE_TO_REFUND AS RFNDELG, HDR.SHIP_BILL_NUM, "
				+ "HDR.SHIP_BILL_DATE, HDR.SHIP_PORT_CODE, ITM.ITM_HSNSAC, "
				+ "ITM.TAX_RATE, (SUM(ITM.TAXABLE_VALUE) OVER (PARTITION BY "
				+ "HDR.ID, HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,ITM.TAX_RATE, "
				+ "ITM.ITM_HSNSAC)) AS TAXABLE_VALUE, CASE WHEN HDR.DOC_TYPE "
				+ "IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_IGST) OVER ("
				+ "PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) ELSE (SUM (ITM.IGST_AMT) OVER"
				+ " (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS IGST, CASE WHEN "
				+ "HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM(ITM.MEMO_VALUE_CESS)"
				+ " OVER (PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE,ITM.ITM_HSNSAC)) ELSE ("
				+ "SUM(ITM.CESS_AMT_SPECIFIC + ITM.CESS_AMT_ADVALOREM) OVER ("
				+ "PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS CESS FROM "
				+ "ANX_OUTWARD_DOC_HEADER HDR INNER JOIN "
				+ "ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID= ITM.DOC_HEADER_ID "
				+ "WHERE IS_PROCESSED= TRUE " + criteria + " ORDER BY HDR.ID";
	}

	// Section - 3E and TaxDocType - SEZT --> with tax
	// Section - 3F and TaxDocType - SEZWT --> without tax
	@Override
	public String buildAnx1SezwpAndSezwopQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildSezwpAndSezwopQuery method with args {}{}",
				gstin, retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, "
				+ "HDR.CUST_GSTIN, HDR.DOC_DATE, HDR.DOC_NUM, HDR.DOC_AMT, "
				+ "HDR.DOC_TYPE, HDR.DIFF_PERCENT, HDR.AUTOPOPULATE_TO_REFUND "
				+ "AS RFNDELG, HDR.CLAIM_REFUND_FLAG, HDR.POS, ITM.ITM_HSNSAC,"
				+ " ITM.TAX_RATE, (SUM(ITM.TAXABLE_VALUE) OVER (PARTITION BY "
				+ "HDR.ID, HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) AS TAXABLE_VALUE, CASE WHEN HDR.DOC_TYPE "
				+ "IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_IGST) OVER ("
				+ "PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE, ITM.ITM_HSNSAC)) ELSE (SUM (ITM.IGST_AMT) "
				+ "OVER ( PARTITION BY HDR.ID,HDR.SUPPLIER_GSTIN, "
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE,ITM.ITM_HSNSAC)) END AS IGST,"
				+ " CASE WHEN HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM("
				+ "ITM.MEMO_VALUE_CESS) OVER ( PARTITION BY HDR.ID,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) ELSE(SUM(ITM.CESS_AMT_SPECIFIC + "
				+ "ITM.CESS_AMT_ADVALOREM) OVER ( PARTITION BY HDR.ID,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) END AS CESS FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = "
				+ "ITM.DOC_HEADER_ID WHERE IS_PROCESSED= TRUE " + criteria
				+ " ORDER BY HDR.ID";
	}

	// Section - 3G and TaxDocType - DXP
	@Override
	public String buildAnx1DeemedExportsQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildDeemedExportsQuery method with args {}{}",
				gstin, retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.SUPPLIER_GSTIN, HDR.RETURN_PERIOD, "
				+ "HDR.CUST_GSTIN, HDR.DOC_DATE, HDR.DOC_NUM, HDR.DOC_AMT, "
				+ "HDR.DOC_TYPE, HDR.DIFF_PERCENT, HDR.SECTION7_OF_IGST_FLAG, "
				+ "HDR.AUTOPOPULATE_TO_REFUND AS RFNDELG, HDR.CLAIM_REFUND_FLAG"
				+ ", HDR.POS, ITM.ITM_HSNSAC, ITM.TAX_RATE, (SUM("
				+ "ITM.TAXABLE_VALUE) OVER (PARTITION BY HDR.ID, "
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE,"
				+ "ITM.ITM_HSNSAC)) AS TAXABLE_VALUE, CASE WHEN HDR.DOC_TYPE "
				+ "IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_IGST) OVER ("
				+ "PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE, ITM.ITM_HSNSAC)) ELSE (SUM (ITM.IGST_AMT) "
				+ "OVER (PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE, ITM.ITM_HSNSAC)) END AS "
				+ "IGST, CASE WHEN HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM ("
				+ "ITM.MEMO_VALUE_CGST) OVER (PARTITION BY HDR.ID, "
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,ITM.TAX_RATE, "
				+ "ITM.ITM_HSNSAC)) ELSE (SUM (ITM.CGST_AMT) OVER ("
				+ "PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
				+ "ITM.TAX_RATE, ITM.ITM_HSNSAC)) END AS CGST, CASE WHEN "
				+ "HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM (ITM.MEMO_VALUE_SGST"
				+ ") OVER (PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN,"
				+ "HDR.RETURN_PERIOD,ITM.TAX_RATE, ITM.ITM_HSNSAC)) ELSE (SUM "
				+ "(ITM.SGST_AMT) OVER (PARTITION BY HDR.ID, HDR.SUPPLIER_GSTIN"
				+ ",HDR.RETURN_PERIOD,ITM.TAX_RATE, ITM.ITM_HSNSAC)) END AS "
				+ "SGST, CASE WHEN HDR.DOC_TYPE IN('INV', 'RNV') THEN (SUM ("
				+ "ITM.MEMO_VALUE_CESS) OVER ( PARTITION BY HDR.ID,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE, "
				+ "ITM.ITM_HSNSAC)) ELSE (SUM(ITM.CESS_AMT_SPECIFIC + "
				+ "ITM.CESS_AMT_ADVALOREM) OVER ( PARTITION BY HDR.ID,"
				+ "HDR.SUPPLIER_GSTIN,HDR.RETURN_PERIOD, ITM.TAX_RATE, "
				+ "ITM.ITM_HSNSAC)) END AS CESS FROM ANX_OUTWARD_DOC_HEADER "
				+ "HDR INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID = "
				+ "ITM.DOC_HEADER_ID WHERE IS_PROCESSED= TRUE " + criteria
				+ " ORDER BY HDR.ID";
	}

	// Section - 3H and TaxDocType - RCM
	@Override
	public String buildAnx1RevDataQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildRevDataQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1VeritalUserCriteria(gstin, retPeriod, docType,
						tableType);

		return "SELECT ID, CUST_GSTIN, RET_PERIOD, CTIN, DIFF_PERCENT, "
				+ "SECTION7_OF_IGST_FLAG, RFNDELG, POS, ITM_HSNSAC, TAX_RATE, "
				+ "TAXABLE_VALUE, IGST, CGST, SGST, CESS FROM ANXSAVE_RC "
				+ "WHERE IS_SENT_TO_GSTN = FALSE " + criteria + " ORDER BY ID";
		// AND ITM_HSNSAC NOT LIKE '99%' This is added only for testing purpose
		// and this should be handled at business rules itself.
	}

	// Section - 3I and TaxDocType - IMPS
	@Override
	public String buildAnx1ImpsQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildImpsQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1VeritalUserCriteria(gstin, retPeriod, docType,
						tableType);

		return "SELECT ID, CUST_GSTIN, RET_PERIOD, RFNDELG, POS, ITM_HSNSAC, "
				+ "TAX_RATE, TAXABLE_VALUE, IGST, CGST, SGST, CESS FROM "
				+ "ANXSAVE_IMPS WHERE IS_SENT_TO_GSTN = FALSE " + criteria
				+ " ORDER BY ID";
		// AND ITM_HSNSAC LIKE '99%' This is added only for testing purpose and
		// this should be handled at business rules itself.
	}

	@Override
	public String buildAnx1EcomQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildAnx1EcomQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1VeritalUserCriteria(gstin, retPeriod, docType,
						tableType);

		return "SELECT ID, RETURN_PERIOD, SUPPLIER_GSTIN, ECOM_GSTIN, "
				+ "SUPPLIES_MADE, SUPPLIES_RETURNED, "
				+ "NET_VALUE, IGST, CGST, SGST, CESS FROM "
				+ "ANXSAVE_ECOM WHERE IS_SENT_TO_GSTN = FALSE " + criteria
				+ " ORDER BY ID";
	}

	// Section - 3J and TaxDocType - IMPG
	// Section - 3K and TaxDocType - SEZG
	@Override
	public String buildAnx1ImpgAndImpgSezQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildImpgAndImpgSezQuery method with args {}{}",
				gstin, retPeriod);

		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.CUST_GSTIN, HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN AS CTIN, HDR.DOC_DATE,"
				+ "HDR.SHIP_PORT_CODE, HDR.DOC_AMT, HDR.DOC_TYPE,"
				+ "HDR.BILL_OF_ENTRY AS BILL_OF_ENTRY_NUM,"
				+ "HDR.AUTOPOPULATE_TO_REFUND AS RFNDELG, HDR.POS,"
				+ "ITM.ITM_HSNSAC, ITM.TAX_RATE, SUM("
				+ "ITM.TAXABLE_VALUE) AS TAXABLE_VALUE,SUM (ITM.IGST_AMT)  AS IGST,"
				+ "SUM(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM)  AS CESS "
				+ "FROM ANX_INWARD_DOC_HEADER HDR "
				+ "INNER JOIN ANX_INWARD_DOC_ITEM ITM "
				+ "ON HDR.ID= ITM.DOC_HEADER_ID " + "WHERE IS_PROCESSED= TRUE "
				+ criteria
				+ " GROUP BY HDR.ID, HDR.CUST_GSTIN, HDR.RETURN_PERIOD,"
				+ "HDR.SUPPLIER_GSTIN , HDR.DOC_DATE,"
				+ "HDR.SHIP_PORT_CODE, HDR.DOC_AMT, HDR.DOC_TYPE,"
				+ "HDR.BILL_OF_ENTRY ," + "HDR.AUTOPOPULATE_TO_REFUND, HDR.POS,"
				+ "ITM.ITM_HSNSAC, ITM.TAX_RATE ORDER BY HDR.ID";

	}

	@Override
	public String buildAnx1MisQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		LOGGER.debug("inside buildMisQuery method with args {}{}", gstin,
				retPeriod);

		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1HorizontalUserCriteria(gstin, retPeriod, docType,
						tableType, docIds);

		return "SELECT HDR.ID, HDR.CUST_GSTIN, HDR.RETURN_PERIOD, HDR.SUPPLIER_GSTIN AS "
				+ "CTIN, HDR.DOC_DATE, HDR.DOC_NUM, HDR.DOC_AMT, HDR.DOC_TYPE, HDR.DIFF_PERCENT,"
				+ "HDR.SECTION7_OF_IGST_FLAG, HDR.CLAIM_REFUND_FLAG, HDR.POS, "
				+ "ITM_HSNSAC, ITM.TAX_RATE,"
				+ "(SUM(ITM.TAXABLE_VALUE) OVER (PARTITION BY HDR.ID,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,TAX_RATE,ITM_HSNSAC)) AS TAXABLE_VALUE,"
				+ "(SUM (ITM.IGST_AMT) OVER (PARTITION BY HDR.ID,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,TAX_RATE,ITM_HSNSAC)) AS IGST,"
				+ "(SUM (ITM.CGST_AMT) OVER (PARTITION BY HDR.ID,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,TAX_RATE,ITM_HSNSAC)) AS CGST,"
				+ "(SUM (ITM.SGST_AMT) OVER (PARTITION BY HDR.ID,"
				+ "HDR.CUST_GSTIN,HDR.RETURN_PERIOD,TAX_RATE,ITM_HSNSAC)) AS SGST,"
				+ "(SUM(ITM.CESS_AMT_SPECIFIC+ITM.CESS_AMT_ADVALOREM) OVER ("
				+ "PARTITION BY HDR.ID,HDR.CUST_GSTIN,HDR.RETURN_PERIOD,TAX_RATE,"
				+ "ITM_HSNSAC)) AS CESS " + "FROM ANX_INWARD_DOC_HEADER HDR "
				+ "INNER JOIN ANX_INWARD_DOC_ITEM ITM "
				+ "ON HDR.ID= ITM.DOC_HEADER_ID "
				+ "WHERE HDR.IS_PROCESSED= TRUE " + criteria
				+ " ORDER BY HDR.ID";
	}

	///////////// ANX2 ////////////////////

	public String buildAnx2CancelledQuery(String gstin, String retPeriod,
			String docType) {
		LOGGER.debug("inside buildAnx1CancelledQuery method with args {}{}",
				gstin, retPeriod);
		// Assuming docType is CAN
		StringBuilder criteria = userCriteriaBuilder
				.buildAnx1CancelledUserCriteria(gstin, retPeriod, docType);
		return "SELECT doc.id, doc.gstnBifurcationNew FROM "
				+ "InwardTransDocument doc WHERE doc.docKey IN (SELECT "
				+ "doc2.docKey FROM InwardTransDocument doc2 WHERE "
				+ "doc2.isDeleted = FALSE " + criteria + ") AND "
				+ "doc.gstnBifurcationNew NOT IN ('CAN') AND doc.isSent = "
				+ "TRUE ORDER BY doc.gstnBifurcationNew, doc.id";
	}

	/*
	 * public String buildAnx2B2bB2baQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds) {
	 * 
	 * StringBuilder criteria = userCriteriaBuilder
	 * .buildAnx2UserCriteria(gstin, retPeriod, docType, tableType, docIds);
	 * 
	 * return
	 * "SELECT LNK.RECON_LINK_ID,LNK.A2_RECIPIENT_GSTIN,VW.RETURN_PERIOD, " +
	 * "VW.CGSTIN AS CTIN,LNK.A2_DOC_DATE,LNK.A2_DOC_NUM, " +
	 * "LNK.A2_DOC_TYPE,LNK.USER_RESPONSE ACTION,VW.CHKSUM, " +
	 * "VW.ITCENTITLEMENT FROM LINK_A2_PR LNK " +
	 * "INNER JOIN RECON_ANX2_SAVE_VIEW VW ON LNK.A2_INVOICE_KEY " +
	 * "= VW.INVOICE_KEY AND LNK.A2_TABLE = 'GETANX2_B2B_HEADER' " +
	 * " AND LNK.IS_ACTIVE = TRUE AND LNK.IS_DELETED = FALSE " + criteria; }
	 * 
	 * public String buildAnx2SezwpSezwpaQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds) { StringBuilder
	 * criteria = userCriteriaBuilder .buildAnx2UserCriteria(gstin, retPeriod,
	 * docType, tableType, docIds);
	 * 
	 * return
	 * "SELECT LNK.RECON_LINK_ID,LNK.A2_RECIPIENT_GSTIN,VW.RETURN_PERIOD," +
	 * "VW.CGSTIN AS CTIN,LNK.A2_DOC_DATE,LNK.A2_DOC_NUM," +
	 * "LNK.A2_DOC_TYPE,LNK.USER_RESPONSE ACTION,VW.CHKSUM," +
	 * "VW.ITCENTITLEMENT FROM LINK_A2_PR LNK INNER JOIN " +
	 * "RECON_ANX2_SAVE_VIEW VW ON LNK.A2_INVOICE_KEY = " +
	 * "VW.INVOICE_KEY AND LNK.A2_TABLE =  'GETANX2_SEZWP_HEADER' " +
	 * "AND LNK.IS_ACTIVE = TRUE AND LNK.IS_DELETED = FALSE " + criteria; }
	 * 
	 * public String buildAnx2SezwopSezwopaQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds) { StringBuilder
	 * criteria = userCriteriaBuilder .buildAnx2UserCriteria(gstin, retPeriod,
	 * docType, tableType, docIds);
	 * 
	 * return
	 * "SELECT LNK.RECON_LINK_ID,LNK.A2_RECIPIENT_GSTIN,VW.RETURN_PERIOD," +
	 * "VW.CGSTIN AS CTIN,LNK.A2_DOC_DATE,LNK.A2_DOC_NUM," +
	 * "LNK.A2_DOC_TYPE,LNK.USER_RESPONSE ACTION,VW.CHKSUM," +
	 * "VW.ITCENTITLEMENT FROM  LINK_A2_PR LNK " +
	 * "INNER JOIN RECON_ANX2_SAVE_VIEW  VW ON " +
	 * "LNK.A2_INVOICE_KEY = VW.INVOICE_KEY AND " +
	 * "LNK.A2_TABLE = 'GETANX2_SEZWOP_HEADER' AND " +
	 * "LNK.IS_ACTIVE = TRUE AND LNK.IS_DELETED = FALSE" + criteria; }
	 * 
	 * public String buildAnx2DeemedExportsQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds) { StringBuilder
	 * criteria = userCriteriaBuilder .buildAnx2UserCriteria(gstin, retPeriod,
	 * docType, tableType, docIds);
	 * 
	 * return
	 * "SELECT LNK.RECON_LINK_ID,LNK.A2_RECIPIENT_GSTIN,VW.RETURN_PERIOD," +
	 * "VW.CGSTIN AS CTIN,LNK.A2_DOC_DATE, LNK.A2_DOC_NUM," +
	 * "LNK.A2_DOC_TYPE,LNK.USER_RESPONSE ACTION,VW.CHKSUM," +
	 * "VW.ITCENTITLEMENT FROM LINK_A2_PR LNK INNER JOIN " +
	 * "RECON_ANX2_SAVE_VIEW VW ON LNK.A2_INVOICE_KEY = " +
	 * "VW.INVOICE_KEY AND LNK.A2_TABLE = 'GETANX2_DE_HEADER' " +
	 * " AND LNK.IS_ACTIVE = TRUE AND LNK.IS_DELETED = FALSE " + criteria; }
	 */

	public String buildAnx2SaveQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {

		StringBuilder criteria = userCriteriaBuilder.buildAnx2UserCriteria(
				gstin, retPeriod, docType, tableType, docIds);

		return "SELECT RECON_LINK_ID,A2_RECIPIENT_GSTIN,RETURN_PERIOD,CTIN,"
				+ "A2_DOC_DATE, A2_DOC_NUM,A2_DOC_TYPE,USER_RESPONSE,CHKSUM,"
				+ "ITCENTITLEMENT FROM ANX2_GSTN_SAVE WHERE IS_DELETE = FALSE "
				+ criteria;

	}

	// -----------------------GSTR6--------------------------------//
	// GSTR6 B2B

	// public static void main(String[] args) {
	//
	// StringBuilder buildQuery = new StringBuilder();
	//
	// StringBuilder prBuildQuery = new StringBuilder();
	// if (true) {
	// buildQuery.append(" AND CUST_GSTIN = :gstin ");
	// prBuildQuery.append(" AND PSD.RGSTINPR = :gstin ");
	// }
	//
	// if (true) {
	// // Below line was connect because for C, D HDR Ret Period is not
	// // required.
	// // buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
	// prBuildQuery.append(" AND PSD.RSPTAXPERIOD3B= :retPeriod ");
	// }
	//
	//
	// String query = "WITH LOCK_DATA as ( SELECT PSD.INVOICEKEYPR,
	// PSD.RGSTINPR, PSD.RSPTAXPERIOD3B,"
	// + " TO_INTEGER(RIGHT(TAXPERIODPR,4) || LEFT(TAXPERIODPR,2)) AS
	// PR_RET_PERIOD "
	// + "FROM TBL_RECON_RESP_PSD PSD WHERE PSD.ENDDTM IS NULL "
	// + prBuildQuery
	// + " ) SELECT HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B,HDR, "
	// + "SUPPLIER_GSTIN as ctin, '' AS CFS, '' AS FLAG, '' as CHECKSUM, "
	// + "HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE, 'DD-MM-YYYY') as DOC_DATE,
	// "
	// + "SUM(HDR.DOC_AMT), HDR.POS, NULL AS ITM_NO, SUM(ITM.TAXABLE_VALUE), "
	// + "ITM.TAX_RATE, SUM(ITM.IGST_AMT), SUM(ITM.CGST_AMT), SUM(ITM.SGST_AMT),
	// "
	// + "SUM( ifnull(ITM.CESS_AMT_SPECIFIC, 0) + ifnull(ITM.CESS_AMT_ADVALOREM,
	// 0) ) as CESS_AMT, "
	// + "HDR.SUPPLY_TYPE FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
	// + "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
	// + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
	// + "INNER JOIN LOCK_DATA PSD ON PSD.INVOICEKEYPR = "
	// + "(CASE WHEN MONTH(HDR.DOC_DATE)<4 THEN
	// TO_VARCHAR(YEAR(HDR.DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)),2)
	// ELSE
	// TO_VARCHAR(YEAR(HDR.DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)+1),2)
	// END ||'|'||HDR.SUPPLIER_GSTIN||'|'|| CASE WHEN "
	// + "HDR.DOC_TYPE='INV' THEN 'R' WHEN HDR.DOC_TYPE='RNV' THEN 'R' "
	// + "WHEN HDR.DOC_TYPE='CR' THEN 'C' WHEN HDR.DOC_TYPE='DR' THEN "
	// + "'D' WHEN HDR.DOC_TYPE='RCR' THEN 'C' WHEN HDR.DOC_TYPE='RDR' "
	// + "THEN 'D' ELSE 'R' END ||'|'||HDR.DOC_NUM||'|'||HDR.CUST_GSTIN) "
	// + "AND HDR.DERIVED_RET_PERIOD =PSD.PR_RET_PERIOD WHERE IS_DELETE = FALSE
	// "
	// + "AND HDR.SUPPLY_TYPE <> 'CAN' AND HDR.IS_SENT_TO_GSTN = FALSE AND "
	// + "HDR.IS_PROCESSED = TRUE " + buildQuery
	// + " group by HDR.ID, "
	// + "CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN, "
	// + "HDR.DOC_NUM, HDR.DOC_DATE, HDR.DOC_AMT, "
	// + "HDR.POS, ITM.TAX_RATE, HDR.SUPPLY_TYPE"
	// + " ORDER BY HDR.ID";
	//
	// System.out.println(query);
	//
	// }
	@Override
	public String buildGstr6B2bSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted) {

		StringBuilder buildQuery = new StringBuilder();

		StringBuilder prBuildQuery = new StringBuilder();

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND HDR.RETURN_TYPE = :returnType ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.TAX_DOC_TYPE IN :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND CUST_GSTIN = :gstin ");
			prBuildQuery.append(" AND PSD.RGSTINPR = :gstin ");
		}

		if (retPeriod != null && !retPeriod.isEmpty()) {
			// Below line was connect because for C, D HDR Ret Period is not
			// required.
			// buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			prBuildQuery.append(" AND PSD.RSPTAXPERIOD3B= :retPeriod ");
		}

		if (optionOpted.equalsIgnoreCase("C")
				|| optionOpted.equalsIgnoreCase("D")) {
			return "WITH LOCK_DATA as ( SELECT PSD.INVOICEKEYPR, PSD.RGSTINPR, PSD.RSPTAXPERIOD3B,"
					+ " TO_INTEGER(RIGHT(TAXPERIODPR,4) || LEFT(TAXPERIODPR,2)) AS PR_RET_PERIOD "
					+ "FROM TBL_RECON_RESP_PSD PSD WHERE PSD.ENDDTM IS NULL "
					+ prBuildQuery
					+ " ) SELECT HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, "
					+ "SUPPLIER_GSTIN as ctin, '' AS CFS, '' AS FLAG, '' as CHECKSUM, "
					+ "HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE, 'DD-MM-YYYY') as DOC_DATE, "
					+ "SUM(HDR.DOC_AMT), HDR.POS, NULL AS ITM_NO, SUM(ITM.TAXABLE_VALUE), "
					+ "ITM.TAX_RATE, SUM(ITM.IGST_AMT), SUM(ITM.CGST_AMT), SUM(ITM.SGST_AMT), "
					+ "SUM( ifnull(ITM.CESS_AMT_SPECIFIC, 0) + ifnull(ITM.CESS_AMT_ADVALOREM, 0) ) as CESS_AMT, "
					+ "HDR.SUPPLY_TYPE FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
					+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "INNER JOIN LOCK_DATA PSD ON PSD.INVOICEKEYPR = "
					+ "(CASE WHEN MONTH(HDR.DOC_DATE)<4 THEN TO_VARCHAR(YEAR(HDR.DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)),2) ELSE TO_VARCHAR(YEAR(HDR.DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)+1),2) END ||'|'||HDR.SUPPLIER_GSTIN||'|'|| CASE WHEN "
					+ "HDR.DOC_TYPE='INV' THEN 'R' WHEN HDR.DOC_TYPE='RNV' THEN 'R' "
					+ "WHEN HDR.DOC_TYPE='CR' THEN 'C' WHEN HDR.DOC_TYPE='DR' THEN "
					+ "'D' WHEN HDR.DOC_TYPE='RCR' THEN 'C' WHEN HDR.DOC_TYPE='RDR' "
					+ "THEN 'D' ELSE 'R' END ||'|'||HDR.DOC_NUM||'|'||HDR.CUST_GSTIN) "
					+ "AND HDR.DERIVED_RET_PERIOD =PSD.PR_RET_PERIOD WHERE IS_DELETE = FALSE "
					+ "AND HDR.SUPPLY_TYPE <> 'CAN' AND HDR.IS_SENT_TO_GSTN = FALSE AND "
					+ "HDR.IS_PROCESSED = TRUE " + buildQuery
					+ " group by HDR.ID, "
					+ "CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN, "
					+ "HDR.DOC_NUM, HDR.DOC_DATE, HDR.DOC_AMT, "
					+ "HDR.POS, ITM.TAX_RATE, HDR.SUPPLY_TYPE"
					+ " ORDER BY HDR.ID";

		} else {
			if (retPeriod != null && !retPeriod.isEmpty()) {
				buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			}

			return "SELECT HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN as ctin,"
					+ "'' AS CFS, '' AS FLAG, '' as CHECKSUM,"
					+ "HDR.DOC_NUM AS INUM,TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') "
					+ "as DOC_DATE,SUM(HDR.DOC_AMT),HDR.POS,NULL AS ITM_NO,"
					+ "SUM(ITM.TAXABLE_VALUE),ITM.TAX_RATE,"
					+ "SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),"
					+ "SUM(ITM.SGST_AMT),SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) "
					+ "+ ifnull(ITM.CESS_AMT_ADVALOREM,0)) as CESS_AMT,HDR.SUPPLY_TYPE FROM "
					+ " ANX_INWARD_DOC_HEADER  HDR INNER JOIN "
					+ " ANX_INWARD_DOC_ITEM ITM  ON "
					+ " HDR.ID = ITM.DOC_HEADER_ID AND "
					+ " HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ " WHERE IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' "
					+ " AND HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE "
					+ buildQuery
					+ " group by HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN, "
					+ "HDR.DOC_NUM,HDR.DOC_DATE, "
					+ "HDR.DOC_AMT,HDR.POS,ITM.TAX_RATE,HDR.SUPPLY_TYPE ORDER BY HDR.ID";
		}
	}

	// GSTR6 ISD
	@Override
	public String buildGstr6IsdSaveQuery(String gstin, String retPeriod,
			String isdDocType) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND TAX_PERIOD = :retPeriod ");
		}
		if (isdDocType != null && !isdDocType.isEmpty()) {
			buildQuery.append(" AND DOC_TYPE = :isdDocType  ");
		}
		return "select ID,ISD_GSTIN,TAX_PERIOD," + "CASE "
				+ " WHEN  ELIGIBLE_INDICATOR IN ('E')THEN 'ELIGIBLE' "
				+ " WHEN  ELIGIBLE_INDICATOR IN ('IE')THEN 'INELIGIBLE' "
				+ " END AS ELIG_IND," + " CASE "
				+ " WHEN CUST_GSTIN is not null then 'R' "
				+ " WHEN CUST_GSTIN IS NULL THEN 'UR' " + " END as Type, "
				+ " CUST_GSTIN AS cpty," + " STATE_CODE," + " '' as Chksum,"
				+ " case "
				+ " WHEN DOC_TYPE = 'INV' AND CUST_GSTIN is not null THEN 'ISD' "
				+ " WHEN DOC_TYPE = 'INV' AND CUST_GSTIN is  null THEN 'ISDUR' "
				+ " WHEN DOC_TYPE = 'CR' AND CUST_GSTIN is not null THEN 'ISDCN' "
				+ " WHEN DOC_TYPE = 'CR' AND CUST_GSTIN is  null THEN 'ISDCNUR' "
				+ " END as ISD_DOCTY,"
				+ " DOC_NUM,TO_CHAR(DOC_DATE,'DD-MM-YYYY') as DOC_DATE,ORG_DOC_NUM,"
				+ " TO_CHAR(ORG_DOC_DATE,'DD-MM-YYYY') as ORG_DOC_DATE,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)) AS IGST_AMT_AS_IGST,"
				+ "0 AS IGST_AMT_AS_SGST, 0 AS IGST_AMT_AS_CGST,"
				+ "SUM(IFNULL(SGST_AMT_AS_SGST,0)+IFNULL(IGST_AMT_AS_SGST,0))"
				+ " AS SGST_AMT_AS_SGST,0 AS SGST_AMT_AS_IGST, 0 AS CGST_AMT_AS_IGST,"
				+ "SUM( IFNULL(IGST_AMT_AS_CGST,0)+IFNULL(CGST_AMT_AS_CGST,0))"
				+ " AS CGST_AMT_AS_CGST,SUM(CESS_AMT) AS CESS_AMT"
				+ " FROM GSTR6_ISD_DISTRIBUTION " + "WHERE  IS_DELETE = FALSE "
				/* + " AND DOC_TYPE IN ('INV','CR') " */
				+ " AND SUPPLY_TYPE IS NULL  AND IS_SENT_TO_GSTN = FALSE "
				+ buildQuery
				+ " GROUP BY ID,ISD_GSTIN,TAX_PERIOD,CUST_GSTIN,STATE_CODE,"
				+ "DOC_NUM,DOC_DATE,ORG_DOC_NUM,ORG_DOC_DATE,ELIGIBLE_INDICATOR,"
				+ "DOC_TYPE ORDER BY cpty,Type,STATE_CODE,ID";
	}

	// GSTR6 ISDA
	@Override
	public String buildGstr6IsdaSaveQuery(String gstin, String retPeriod,
			String isdDocType) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND TAX_PERIOD = :retPeriod ");
		}
		if (isdDocType != null && !isdDocType.isEmpty()) {
			buildQuery.append(" AND DOC_TYPE = :isdDocType  ");
		}
		return "select ID,ISD_GSTIN,TAX_PERIOD," + " CASE "
				+ " WHEN  ELIGIBLE_INDICATOR IN ('IS','E')THEN 'ELIGIBLE' "
				+ " WHEN  ELIGIBLE_INDICATOR IN ('NO','IE')THEN 'INELIGIBLE' "
				+ " END AS ELIG_IND, CASE "
				+ " WHEN CUST_GSTIN is not null then 'R' "
				+ " WHEN CUST_GSTIN IS NULL THEN 'UR' END as Type,"
				+ "CUST_GSTIN AS cpty,STATE_CODE,'' as Chksum," + " case "
				+ " WHEN DOC_TYPE = 'RNV' AND CUST_GSTIN is not null THEN 'ISDA' "
				+ " WHEN DOC_TYPE = 'RNV' AND CUST_GSTIN is  null THEN 'ISDURA' "
				+ " WHEN DOC_TYPE = 'RCR' AND CUST_GSTIN is not null THEN 'ISDCNA' "
				+ " WHEN DOC_TYPE = 'RCR' AND CUST_GSTIN is  null THEN 'ISDCNURA' "
				+ " END as ISD_DOCTY,"
				+ "DOC_NUM,TO_CHAR(DOC_DATE,'DD-MM-YYYY') as DOC_DATE,ORG_CR_NUM,"
				+ " TO_CHAR(ORG_CR_DATE,'DD-MM-YYYY') as ORG_CR_DATE,"
				+ "SUM(IFNULL(IGST_AMT_AS_IGST,0)+IFNULL(CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(SGST_AMT_AS_IGST,0)) as IGST_AMT_AS_IGST,"
				+ " 0 as IGST_AMT_AS_SGST,0 as IGST_AMT_AS_CGST,"
				+ "SUM(IFNULL(SGST_AMT_AS_SGST,0)+IFNULL(IGST_AMT_AS_SGST,0)) "
				+ "as SGST_AMT_AS_SGST,0 as SGST_AMT_AS_IGST,0 as CGST_AMT_AS_IGST,"
				+ "SUM( IFNULL(IGST_AMT_AS_CGST,0)+IFNULL(CGST_AMT_AS_CGST,0))"
				+ " as CGST_AMT_AS_CGST,SUM(CESS_AMT) AS CESS_AMT "
				+ " FROM GSTR6_ISD_DISTRIBUTION " + " WHERE IS_DELETE = FALSE "
				/* + "AND DOC_TYPE IN ('RNV','RCR')" */
				+ " AND SUPPLY_TYPE IS NULL  AND IS_SENT_TO_GSTN = FALSE "
				+ buildQuery
				+ "GROUP BY ID,ISD_GSTIN,TAX_PERIOD,CUST_GSTIN,STATE_CODE,"
				+ "DOC_NUM,DOC_DATE,ORG_CR_NUM,ORG_CR_DATE,ELIGIBLE_INDICATOR,DOC_TYPE "
				+ " ORDER BY cpty,STATE_CODE,ID ";
	}

	// GSTR6 B2BA
	@Override
	public String buildGstr6B2baSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted) {

		StringBuilder buildQuery = new StringBuilder();

		StringBuilder prBuildQuery = new StringBuilder();

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND HDR.RETURN_TYPE = :returnType ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.TAX_DOC_TYPE IN :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND CUST_GSTIN = :gstin ");
			prBuildQuery.append(" AND PSD.RGSTINPR = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			// Below line was connect because for C, D HDR Ret Period is not
			// required.
			// buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			prBuildQuery.append(" AND PSD.RSPTAXPERIOD3B= :retPeriod ");
		}

		if (optionOpted.equalsIgnoreCase("C")
				|| optionOpted.equalsIgnoreCase("D")) {
			return "WITH LOCK_DATA as ( SELECT PSD.INVOICEKEYPR, "
					+ "PSD.RGSTINPR, PSD.RSPTAXPERIOD3B, "
					+ "TO_INTEGER(RIGHT(TAXPERIODPR,4) || LEFT(TAXPERIODPR,2)) AS PR_RET_PERIOD "
					+ "FROM TBL_RECON_RESP_PSD PSD WHERE PSD.ENDDTM IS NULL "
					+ prBuildQuery + " ) "
					+ "SELECT HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN as ctin, '' AS CFS, '' AS FLAG, "
					+ "HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') as DOC_DATE, "
					+ "HDR.ORIGINAL_DOC_NUM, TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as O_DOC_DATE,"
					+ " SUM(HDR.DOC_AMT), HDR.POS, '' AS CHKSUM, NULL AS ITM_NO, SUM(ITM.TAXABLE_VALUE),"
					+ " ITM.TAX_RATE, SUM(ITM.IGST_AMT), SUM(ITM.CGST_AMT), SUM(ITM.SGST_AMT), "
					+ "SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0)) as CESS_AMT,"
					+ " HDR.SUPPLY_TYPE, CASE WHEN HDR.ORIG_SUPPLIER_GSTIN is null THEN "
					+ "SUPPLIER_GSTIN ELSE HDR.ORIG_SUPPLIER_GSTIN END as ORIG_SUPPLIER_GSTIN "
					+ "FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
					+ "HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "INNER JOIN LOCK_DATA PSD ON "
					+ "PSD.INVOICEKEYPR = (CASE WHEN MONTH(HDR.DOC_DATE)<4 THEN TO_VARCHAR(YEAR(HDR.DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)),2) ELSE TO_VARCHAR(YEAR(HDR.DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)+1),2) END ||'|'||HDR.SUPPLIER_GSTIN||'|'|| CASE WHEN "
					+ "HDR.DOC_TYPE='INV' THEN 'R' "
					+ "WHEN HDR.DOC_TYPE='RNV' THEN 'R' "
					+ "WHEN HDR.DOC_TYPE='CR' THEN 'C' WHEN "
					+ "HDR.DOC_TYPE='DR' THEN 'D' WHEN HDR.DOC_TYPE='RCR' "
					+ "THEN 'C' WHEN HDR.DOC_TYPE='RDR' THEN 'D' "
					+ "ELSE 'R' END ||'|'||HDR.DOC_NUM||'|'||HDR.CUST_GSTIN) "
					+ "AND HDR.DERIVED_RET_PERIOD =PSD.PR_RET_PERIOD WHERE "
					+ "IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' AND "
					+ "HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE "
					+ buildQuery
					+ " GROUP BY HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN,"
					+ " HDR.DOC_NUM, HDR.DOC_DATE, HDR.ORIGINAL_DOC_NUM, HDR.ORIGINAL_DOC_DATE, "
					+ "HDR.POS, ITM.TAX_RATE, HDR.SUPPLY_TYPE, HDR.ORIG_SUPPLIER_GSTIN ORDER BY HDR.ID";
		} else {
			if (retPeriod != null && !retPeriod.isEmpty()) {
				buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			}
			return "SELECT HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,"
					+ "SUPPLIER_GSTIN as ctin,'' AS CFS, '' AS FLAG,"
					+ "HDR.DOC_NUM AS INUM,TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') as "
					+ "DOC_DATE, HDR.ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as O_DOC_DATE,"
					+ "SUM(HDR.DOC_AMT),HDR.POS,'' AS CHKSUM,NULL AS ITM_NO,SUM(ITM.TAXABLE_VALUE),"
					+ "ITM.TAX_RATE,SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ "SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0))"
					+ " as CESS_AMT,HDR.SUPPLY_TYPE,CASE WHEN HDR.ORIG_SUPPLIER_GSTIN is null "
					+ " then SUPPLIER_GSTIN else HDR.ORIG_SUPPLIER_GSTIN end as ORIG_SUPPLIER_GSTIN "
					+ " FROM ANX_INWARD_DOC_HEADER HDR "
					+ " INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
					+ " HDR.ID = ITM.DOC_HEADER_ID AND "
					+ " HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ " WHERE IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN'  "
					+ " AND HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE"
					+ buildQuery
					+ " GROUP BY HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN, "
					+ "HDR.DOC_NUM , HDR.DOC_DATE, HDR.ORIGINAL_DOC_NUM, "
					+ " HDR.ORIGINAL_DOC_DATE,HDR.POS,ITM.TAX_RATE,HDR.SUPPLY_TYPE,"
					+ "HDR.ORIG_SUPPLIER_GSTIN ORDER BY HDR.ID";
		}
	}

	// GSTR6 CDN
	@Override
	public String buildGstr6CdnSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted) {

		StringBuilder buildQuery = new StringBuilder();
		StringBuilder prBuildQuery = new StringBuilder();

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND HDR.RETURN_TYPE = :returnType ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.TAX_DOC_TYPE IN :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND CUST_GSTIN = :gstin ");
			prBuildQuery.append(" AND PSD.RGSTINPR = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			// Below line was connect because for C, D HDR Ret Period is not
			// required.
			// buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			prBuildQuery.append(" AND PSD.RSPTAXPERIOD3B= :retPeriod ");
		}

		if (optionOpted.equalsIgnoreCase("C")
				|| optionOpted.equalsIgnoreCase("D")) {
			return "WITH LOCK_DATA as ( SELECT PSD.INVOICEKEYPR, PSD.RGSTINPR, "
					+ "PSD.RSPTAXPERIOD3B, TO_INTEGER(RIGHT(TAXPERIODPR,4) || LEFT(TAXPERIODPR,2)) AS PR_RET_PERIOD "
					+ "FROM TBL_RECON_RESP_PSD PSD WHERE PSD.ENDDTM IS NULL "
					+ prBuildQuery + " ) "
					+ "SELECT HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN as ctin, "
					+ "HDR.DOC_TYPE, HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') as DOC_DATE,"
					+ " HDR.ORIGINAL_DOC_NUM, TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as ORIGINAL_DOC_DATE, "
					+ "SUM(HDR.DOC_AMT), NULL AS ITM_NO, SUM(ITM.TAXABLE_VALUE), ITM.TAX_RATE, SUM(ITM.IGST_AMT),"
					+ " SUM(ITM.CGST_AMT), SUM(ITM.SGST_AMT), SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0) +"
					+ " IFNULL(ITM.CESS_AMT_ADVALOREM,0)) as CESS_AMT, HDR.POS, "
					+ "HDR.SUPPLY_TYPE FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
					+ "ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD INNER JOIN LOCK_DATA PSD ON "
					+ "PSD.INVOICEKEYPR = (CASE WHEN MONTH(HDR.DOC_DATE)<4 THEN TO_VARCHAR(YEAR(HDR.DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)),2) ELSE TO_VARCHAR(YEAR(HDR.DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)+1),2) END ||'|'||HDR.SUPPLIER_GSTIN||'|'|| CASE WHEN "
					+ "HDR.DOC_TYPE='INV' THEN 'R' WHEN HDR.DOC_TYPE='RNV' THEN 'R' WHEN HDR.DOC_TYPE='CR' THEN "
					+ "'C' WHEN HDR.DOC_TYPE='DR' THEN 'D' WHEN HDR.DOC_TYPE='RCR' "
					+ "THEN 'C' WHEN HDR.DOC_TYPE='RDR' THEN 'D' ELSE 'R' END ||'|'||HDR.DOC_NUM||'|'||HDR.CUST_GSTIN) AND HDR.DERIVED_RET_PERIOD =PSD.PR_RET_PERIOD "
					+ "WHERE IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' "
					+ "AND HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE "
					+ buildQuery
					+ " GROUP BY HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN, "
					+ "HDR.DOC_TYPE, HDR.DOC_NUM, HDR.DOC_DATE, HDR.ORIGINAL_DOC_NUM,"
					+ " HDR.ORIGINAL_DOC_DATE, HDR.POS, ITM.TAX_RATE, HDR.SUPPLY_TYPE ORDER BY HDR.ID";
		} else {
			if (retPeriod != null && !retPeriod.isEmpty()) {
				buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			}
			return "SELECT HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN as ctin,"
					+ "HDR.DOC_TYPE,HDR.DOC_NUM AS INUM,TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') "
					+ "as DOC_DATE,HDR.ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as "
					+ " ORIGINAL_DOC_DATE,SUM(HDR.DOC_AMT),"
					+ "NULL AS ITM_NO,SUM(ITM.TAXABLE_VALUE),ITM.TAX_RATE,SUM(ITM.IGST_AMT),"
					+ "SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ "SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0))"
					+ " as CESS_AMT,HDR.POS,HDR.SUPPLY_TYPE FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
					+ " ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID AND "
					+ " HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ " WHERE IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN'  "
					+ " AND HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE"
					+ buildQuery
					+ " GROUP BY HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD, "
					+ " SUPPLIER_GSTIN,HDR.DOC_NUM , HDR.DOC_DATE, "
					+ " HDR.ORIGINAL_DOC_NUM,HDR.ORIGINAL_DOC_DATE,"
					+ " HDR.POS,ITM.TAX_RATE,HDR.DOC_TYPE,HDR.SUPPLY_TYPE ORDER BY HDR.ID";
		}
	}

	// GSTR6 CDNA
	@Override
	public String buildGstr6CdnaSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted) {
		StringBuilder buildQuery = new StringBuilder();
		StringBuilder prBuildQuery = new StringBuilder();

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND HDR.RETURN_TYPE = :returnType ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND HDR.TAX_DOC_TYPE IN :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND CUST_GSTIN = :gstin ");
			prBuildQuery.append(" AND PSD.RGSTINPR = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			// Below line was connect because for C, D HDR Ret Period is not
			// required.
			// buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			prBuildQuery.append(" AND PSD.RSPTAXPERIOD3B= :retPeriod ");
		}
		if (optionOpted.equalsIgnoreCase("C")
				|| optionOpted.equalsIgnoreCase("D")) {
			return "WITH LOCK_DATA as ( SELECT PSD.INVOICEKEYPR, PSD.RGSTINPR, PSD.RSPTAXPERIOD3B,"
					+ " TO_INTEGER(RIGHT(TAXPERIODPR,4) || LEFT(TAXPERIODPR,2)) AS PR_RET_PERIOD "
					+ "FROM TBL_RECON_RESP_PSD PSD WHERE PSD.ENDDTM IS NULL "
					+ prBuildQuery + " ) "
					+ "SELECT HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN as ctin, HDR.DOC_TYPE, "
					+ "HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') as DOC_DATE, HDR.ORIGINAL_DOC_NUM, "
					+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as ORG_DOC_DATE, HDR.BILL_OF_ENTRY,"
					+ " TO_CHAR(HDR.BILL_OF_ENTRY_DATE,'DD-MM-YYYY') AS IDATE, SUM(HDR.DOC_AMT), NULL AS ITM_NO,"
					+ " SUM(ITM.TAXABLE_VALUE), ITM.TAX_RATE, SUM(ITM.IGST_AMT), SUM(ITM.CGST_AMT), SUM(ITM.SGST_AMT), "
					+ "SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0) + IFNULL(ITM.CESS_AMT_ADVALOREM,0)) as CESS_AMT, "
					+ "HDR.POS, HDR.SUPPLY_TYPE, "
					+ "CASE WHEN HDR.ORIG_SUPPLIER_GSTIN is null THEN SUPPLIER_GSTIN ELSE HDR.ORIG_SUPPLIER_GSTIN END as ORIG_SUPPLIER_GSTIN "
					+ "FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN ANX_INWARD_DOC_ITEM ITM "
					+ "ON HDR.ID = ITM.DOC_HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ "INNER JOIN LOCK_DATA PSD ON PSD.INVOICEKEYPR = (CASE WHEN MONTH(HDR.DOC_DATE)<4 THEN TO_VARCHAR(YEAR(HDR.DOC_DATE)-1)||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)),2) ELSE TO_VARCHAR(YEAR(HDR.DOC_DATE))||'-'||RIGHT(TO_VARCHAR(YEAR(HDR.DOC_DATE)+1),2) END ||'|'||HDR.SUPPLIER_GSTIN||'|'|| CASE "
					+ "WHEN HDR.DOC_TYPE='INV' THEN 'R' WHEN HDR.DOC_TYPE='RNV' THEN "
					+ "'R' WHEN HDR.DOC_TYPE='CR' THEN 'C' WHEN HDR.DOC_TYPE='DR' THEN 'D' WHEN "
					+ "HDR.DOC_TYPE='RCR' THEN 'C' WHEN HDR.DOC_TYPE='RDR' THEN 'D' ELSE 'R' "
					+ "END ||'|'||HDR.DOC_NUM||'|'||HDR.CUST_GSTIN) AND HDR.DERIVED_RET_PERIOD =PSD.PR_RET_PERIOD WHERE "
					+ "IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' AND HDR.IS_SENT_TO_GSTN = FALSE AND "
					+ "HDR.IS_PROCESSED = TRUE " + buildQuery
					+ " GROUP BY HDR.ID, CUST_GSTIN, PSD.RSPTAXPERIOD3B, SUPPLIER_GSTIN, HDR.DOC_TYPE, HDR.DOC_NUM, HDR.DOC_DATE, HDR.ORIGINAL_DOC_NUM, "
					+ "HDR.ORIGINAL_DOC_DATE, HDR.BILL_OF_ENTRY, HDR.BILL_OF_ENTRY_DATE, ITM.TAX_RATE,"
					+ " HDR.POS, HDR.SUPPLY_TYPE, HDR.ORIG_SUPPLIER_GSTIN ORDER BY HDR.ID";
		} else {
			if (retPeriod != null && !retPeriod.isEmpty()) {
				buildQuery.append(" AND HDR.RETURN_PERIOD = :retPeriod ");
			}
			return "SELECT HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN as ctin,"
					+ "HDR.DOC_TYPE,HDR.DOC_NUM AS INUM, TO_CHAR(HDR.DOC_DATE,'DD-MM-YYYY') "
					+ "as DOC_DATE,HDR.ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(HDR.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as ORG_DOC_DATE,HDR.BILL_OF_ENTRY,"
					+ "TO_CHAR(HDR.BILL_OF_ENTRY_DATE,'DD-MM-YYYY') AS IDATE,SUM(HDR.DOC_AMT),NULL AS ITM_NO,"
					+ "SUM(ITM.TAXABLE_VALUE),ITM.TAX_RATE,SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ "SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0))"
					+ " as CESS_AMT,HDR.POS,HDR.SUPPLY_TYPE,case when HDR.ORIG_SUPPLIER_GSTIN is null "
					+ " then SUPPLIER_GSTIN else HDR.ORIG_SUPPLIER_GSTIN end as ORIG_SUPPLIER_GSTIN "
					+ " FROM ANX_INWARD_DOC_HEADER HDR INNER JOIN "
					+ " ANX_INWARD_DOC_ITEM ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "  AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
					+ " WHERE IS_DELETE = FALSE AND HDR.SUPPLY_TYPE <> 'CAN' "
					+ " AND HDR.IS_SENT_TO_GSTN = FALSE AND HDR.IS_PROCESSED = TRUE"
					+ buildQuery
					+ " GROUP BY HDR.ID,CUST_GSTIN,HDR.RETURN_PERIOD,SUPPLIER_GSTIN, "
					+ " HDR.DOC_TYPE,HDR.DOC_NUM,HDR.DOC_DATE, "
					+ " HDR.ORIGINAL_DOC_NUM,HDR.ORIGINAL_DOC_DATE,HDR.BILL_OF_ENTRY, "
					+ " HDR.BILL_OF_ENTRY_DATE, ITM.TAX_RATE,HDR.POS,HDR.SUPPLY_TYPE,"
					+ " HDR.ORIG_SUPPLIER_GSTIN ORDER BY HDR.ID";
		}
	}
	// ------------------GSTR7-----------------------//

	@Override
	public String buildGstr7TDSQuery(String gstin, String retPeriod,
			String docType) {

		LOGGER.debug("inside buildGstr7TDSQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder buildQuery = new StringBuilder();

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND doc.SECTION_NAME = :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND doc.TDS_DEDUCTOR_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND doc.RETURN_PERIOD = :retPeriod ");
		}

		return "SELECT doc.RETURN_PERIOD, doc.TDS_DEDUCTOR_GSTIN, doc.TDS_DEDUCTEE_GSTIN,"
				+ "doc.SECTION_NAME,doc.NEW_GROSS_AMT, doc.ORG_TDS_DEDUCTEE_GSTIN,"
				+ "doc.ORG_GROSS_AMT,doc.ORG_MONTH, doc.IGST, doc.CGST, doc.SGST,doc.ID"
				+ " FROM GSTR7_SAVE_TDS doc "
				+ " WHERE  doc.IS_DELETE = FALSE AND doc.IS_SENT_TO_GSTN = FALSE AND "
				+ " (doc.ACTION_TYPE <> 'CAN' OR doc.ACTION_TYPE IS NULL)"
				+ buildQuery;

	}

	@Override
	public String buildGstr7CanQuery(String gstin, String retPeriod,
			String docType) {

		LOGGER.debug("inside buildGstr7CanQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder buildQuery = new StringBuilder();

		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND doc.SECTION_NAME = :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND doc.TDS_DEDUCTOR_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND doc.RETURN_PERIOD = :retPeriod ");
		}

		return "SELECT doc.RETURN_PERIOD, doc.TDS_DEDUCTOR_GSTIN, doc.TDS_DEDUCTEE_GSTIN,"
				+ "doc.SECTION_NAME,doc.NEW_GROSS_AMT, doc.ORG_TDS_DEDUCTEE_GSTIN,"
				+ "doc.ORG_GROSS_AMT,doc.ORG_MONTH, doc.IGST, doc.CGST, doc.SGST,doc.ID"
				+ " FROM GSTR7_SAVE_TDS doc "
				+ " WHERE  doc.IS_DELETE = FALSE AND doc.IS_SENT_TO_GSTN = FALSE AND "
				+ " doc.ACTION_TYPE = 'CAN' " + buildQuery;

	}

	// ------------------GSTR8-----------------------//

	@Override
	public String buildGstr8SummQuery(String gstin, String retPeriod,
			String section) {

		LOGGER.debug("inside buildGstr8TDSQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder buildQuery = new StringBuilder();

		if (section != null && !section.isEmpty()) {
			buildQuery.append(" AND doc.SECTION = :section ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND doc.GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND doc.RETURN_PERIOD = :retPeriod ");
		}

		return "SELECT doc.RETURN_PERIOD, doc.GSTIN, doc.SECTION,doc.ORG_RET_PERIOD, doc.ORG_NET_SUPPLIES,"
				+ "doc.SGSTIN_OR_ENROL_ID,doc.ORG_SGSTIN_OR_ENROL_ID,doc.SUPPLIES_TO_REGISTERED,"
				+ "doc.RETURNS_FROM_REGISTERED,doc.SUPPLIES_TO_UNREGISTERED,"
				+ "doc.RETURNS_FROM_UNREGISTERED,doc.NET_SUPPLIES,doc.IGST_AMT, "
				+ "doc.CGST_AMT,doc.SGST_AMT, doc.ID"
				+ " FROM GSTR8_SAVE_SUMMARY doc "
				+ " WHERE  doc.IS_DELETE = FALSE AND doc.IS_SENT_TO_GSTN = FALSE AND "
				+ " (doc.ACTION_TYPE <> 'CAN' OR doc.ACTION_TYPE IS NULL)"
				+ buildQuery;

	}

	@Override
	public String buildGstr8CanSummQuery(String gstin, String retPeriod,
			String section) {

		LOGGER.debug("inside buildGstr8CanQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder buildQuery = new StringBuilder();

		if (section != null && !section.isEmpty()) {
			buildQuery.append(" AND doc.SECTION = :section ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND doc.TDS_DEDUCTOR_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND doc.RETURN_PERIOD = :retPeriod ");
		}

		return "SELECT doc.RETURN_PERIOD, doc.TDS_DEDUCTOR_GSTIN, doc.TDS_DEDUCTEE_GSTIN,"
				+ "doc.SECTION_NAME,doc.NEW_GROSS_AMT, doc.ORG_TDS_DEDUCTEE_GSTIN,"
				+ "doc.ORG_GROSS_AMT,doc.ORG_MONTH, doc.IGST, doc.CGST, doc.SGST,doc.ID"
				+ " FROM GSTR7_SAVE_TDS doc "
				+ " WHERE  doc.IS_DELETE = FALSE AND doc.IS_SENT_TO_GSTN = FALSE AND "
				+ " doc.ACTION_TYPE = 'CAN' " + buildQuery;

	}

	@Override
	public String buildGstr6CanQuery(String gstin, String retPeriod,
			String docType, String returnType) {

		LOGGER.debug("inside buildGstr6CanQuery method with args {}{}", gstin,
				retPeriod);
		StringBuilder buildQuery = new StringBuilder();

		if (returnType != null && !returnType.isEmpty()) {
			buildQuery.append(" AND T1.RETURN_TYPE = :returnType ");
		}
		if (docType != null && !docType.isEmpty()) {
			buildQuery.append(" AND T1.TAX_DOC_TYPE IN :docType ");
		}
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND T1.CUST_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND T1.GSTR6_SAVED_RET_PERIOD = :retPeriod ");
		}

		String query = null;

		if (docType.equalsIgnoreCase(APIConstants.B2B.toUpperCase())) {
			query = "SELECT T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,"
					+ "T2.SUPPLIER_GSTIN as ctin,'' AS CFS, "
					+ "'' AS FLAG, '' as CHECKSUM,T2.DOC_NUM AS INUM,"
					+ "TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY') as DOC_DATE,"
					+ " SUM(T2.DOC_AMT),T2.POS,NULL AS ITM_NO,"
					+ "SUM(ITM.TAXABLE_VALUE),ITM.TAX_RATE,"
					+ "SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),"
					+ "SUM(ITM.SGST_AMT),SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) +"
					+ "ifnull(ITM.CESS_AMT_ADVALOREM,0)) as CESS_AMT,T2.SUPPLY_TYPE "
					+ " FROM ANX_INWARD_DOC_HEADER T1 "
					+ " inner join ANX_INWARD_DOC_HEADER T2 on "
					+ " T1.ID <> T2.ID AND " + " T1.DOC_KEY = T2.DOC_KEY "
					+ " AND T1.IS_DELETE = TRUE AND T2.IS_DELETE = FALSE "
					+ " AND T1.IS_PROCESSED = TRUE AND T2.IS_PROCESSED = TRUE "
					+ " AND T1.IS_SAVED_TO_GSTN = TRUE  AND T2.IS_SENT_TO_GSTN = FALSE "
					+ " AND  T1.SUPPLY_TYPE <> 'CAN' AND T2.SUPPLY_TYPE = 'CAN' "
					+ buildQuery + " INNER JOIN "
					+ " ANX_INWARD_DOC_ITEM ITM ON "
					+ " T2.ID = ITM.DOC_HEADER_ID "
					+ " group by T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN,"
					+ " T2.DOC_NUM,T2.DOC_DATE,T2.DOC_AMT,T2.POS,ITM.TAX_RATE,"
					+ "T1.SUPPLY_TYPE,T2.SUPPLY_TYPE ";
		} else if (docType.equalsIgnoreCase(APIConstants.B2BA.toUpperCase())) {
			query = "SELECT T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,"
					+ "T2.SUPPLIER_GSTIN as ctin,'' AS CFS, '' AS FLAG,"
					+ "T2.DOC_NUM AS INUM,TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY') as "
					+ " DOC_DATE, T2.ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(T2.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as O_DOC_DATE,"
					+ "SUM(T2.DOC_AMT),T2.POS,'' AS CHKSUM,NULL AS ITM_NO,SUM(ITM.TAXABLE_VALUE),"
					+ "ITM.TAX_RATE,SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ "SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0))"
					+ " as CESS_AMT,T2.SUPPLY_TYPE,case when T2.ORIG_SUPPLIER_GSTIN is null "
					+ " then T2.SUPPLIER_GSTIN else T2.ORIG_SUPPLIER_GSTIN end as ORIG_SUPPLIER_GSTIN "
					+ " FROM ANX_INWARD_DOC_HEADER T1 "
					+ "  inner join  ANX_INWARD_DOC_HEADER T2  on "
					+ " T1.ID <> T2.ID AND " + " T1.DOC_KEY = T2.DOC_KEY "
					+ " AND T1.IS_DELETE = TRUE AND T2.IS_DELETE = FALSE "
					+ " AND T1.IS_PROCESSED = TRUE AND T2.IS_PROCESSED = TRUE "
					+ " AND T1.IS_SAVED_TO_GSTN = TRUE  AND T2.IS_SENT_TO_GSTN = FALSE "
					+ " AND  T1.SUPPLY_TYPE <> 'CAN' AND T2.SUPPLY_TYPE = 'CAN' "
					+ buildQuery + "  INNER JOIN ANX_INWARD_DOC_ITEM ITM ON "
					+ "  T2.ID = ITM.DOC_HEADER_ID "
					+ " group by T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN,"
					+ "T2.DOC_NUM,T2.DOC_DATE,T2.ORIGINAL_DOC_NUM,T2.ORIGINAL_DOC_DATE,"
					+ "T2.DOC_AMT,T2.POS,ITM.TAX_RATE,T1.SUPPLY_TYPE,T2.SUPPLY_TYPE,T2.ORIG_SUPPLIER_GSTIN";
		} else if (docType.equalsIgnoreCase(APIConstants.CDN.toUpperCase())) {
			query = "SELECT T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN as ctin,"
					+ "T2.DOC_TYPE,T2.DOC_NUM AS INUM,TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY') as DOC_DATE,"
					+ "T2.ORIGINAL_DOC_NUM,TO_CHAR(T2.ORIGINAL_DOC_DATE,'DD-MM-YYYY') "
					+ " as ORIGINAL_DOC_DATE,SUM(T2.DOC_AMT),NULL AS ITM_NO,SUM(ITM.TAXABLE_VALUE),"
					+ "ITM.TAX_RATE,SUM(ITM.IGST_AMT),SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ " SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0)) "
					+ " as CESS_AMT,T2.POS,T2.SUPPLY_TYPE "
					+ "  FROM ANX_INWARD_DOC_HEADER  T1 "
					+ " inner join ANX_INWARD_DOC_HEADER T2 on "
					+ " T1.ID <> T2.ID AND  T1.DOC_KEY = T2.DOC_KEY "
					+ " AND T1.IS_DELETE = TRUE AND T2.IS_DELETE = FALSE "
					+ " AND T1.IS_PROCESSED = TRUE AND T2.IS_PROCESSED = TRUE "
					+ " AND T1.IS_SAVED_TO_GSTN = TRUE  AND T2.IS_SENT_TO_GSTN = FALSE "
					+ " AND  T1.SUPPLY_TYPE <> 'CAN' AND T2.SUPPLY_TYPE = 'CAN' "
					+ buildQuery
					+ "INNER JOIN ANX_INWARD_DOC_ITEM ITM ON  T2.ID = ITM.DOC_HEADER_ID "
					+ " group by T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN,T2.DOC_TYPE,"
					+ "T2.DOC_NUM,T2.DOC_DATE,T2.ORIGINAL_DOC_NUM,T2.ORIGINAL_DOC_DATE,"
					+ "T2.DOC_AMT,T2.POS,ITM.TAX_RATE,T1.SUPPLY_TYPE,T2.SUPPLY_TYPE ";
		} else if (docType.equalsIgnoreCase(APIConstants.CDNA.toUpperCase())) {
			query = "SELECT T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN as ctin,"
					+ "T2.DOC_TYPE,T2.DOC_NUM AS INUM, TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY')"
					+ " as DOC_DATE,T2.ORIGINAL_DOC_NUM,"
					+ "TO_CHAR(T2.ORIGINAL_DOC_DATE,'DD-MM-YYYY') as ORG_DOC_DATE,T2.BILL_OF_ENTRY,"
					+ "TO_CHAR(T2.BILL_OF_ENTRY_DATE,'DD-MM-YYYY') AS IDATE,SUM(T2.DOC_AMT),"
					+ "NULL AS ITM_NO,SUM(ITM.TAXABLE_VALUE),ITM.TAX_RATE,SUM(ITM.IGST_AMT),"
					+ "SUM(ITM.CGST_AMT),SUM(ITM.SGST_AMT),"
					+ "SUM(ifnull(ITM.CESS_AMT_SPECIFIC,0) + ifnull(ITM.CESS_AMT_ADVALOREM,0)) "
					+ " as CESS_AMT,T2.POS,T2.SUPPLY_TYPE,case when T2.ORIG_SUPPLIER_GSTIN is null "
					+ " then T2.SUPPLIER_GSTIN else T2.ORIG_SUPPLIER_GSTIN end as ORIG_SUPPLIER_GSTIN "
					+ " FROM ANX_INWARD_DOC_HEADER T1 "
					+ " inner join ANX_INWARD_DOC_HEADER  T2 on "
					+ " T1.ID <> T2.ID AND T1.DOC_KEY = T2.DOC_KEY "
					+ "  AND T1.IS_DELETE = TRUE AND T2.IS_DELETE = FALSE "
					+ " AND T1.IS_PROCESSED = TRUE AND T2.IS_PROCESSED = TRUE "
					+ " AND T1.IS_SAVED_TO_GSTN = TRUE  AND T2.IS_SENT_TO_GSTN = FALSE "
					+ " AND  T1.SUPPLY_TYPE <> 'CAN' AND T2.SUPPLY_TYPE = 'CAN' "
					+ buildQuery
					+ " INNER JOIN ANX_INWARD_DOC_ITEM ITM ON T2.ID = ITM.DOC_HEADER_ID "
					+ " group by T2.ID,T2.CUST_GSTIN,T2.GSTR6_SAVED_RET_PERIOD,T2.SUPPLIER_GSTIN,T2.DOC_TYPE,"
					+ "T2.DOC_NUM,T2.DOC_DATE,T2.ORIGINAL_DOC_NUM,T2.ORIGINAL_DOC_DATE,T2.BILL_OF_ENTRY,"
					+ "T2.BILL_OF_ENTRY_DATE,T2.DOC_AMT,T2.POS,ITM.TAX_RATE,T1.SUPPLY_TYPE,T2.SUPPLY_TYPE,"
					+ "T2.ORIG_SUPPLIER_GSTIN";
		}
		return query;
	}

	@Override
	public String buildGstr6CanIsdQuery(String gstin, String retPeriod,
			String isdDocType) {

		StringBuilder buildQuery1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery1.append(" AND T2.ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery1.append(" AND T2.TAX_PERIOD = :retPeriod ");
		}
		if (isdDocType != null && !isdDocType.isEmpty()) {
			buildQuery1.append(" AND T2.DOC_TYPE = :isdDocType  ");
		}
		return "select T2.ID,T2.ISD_GSTIN,T2.TAX_PERIOD, CASE "
				+ " WHEN  T2.ELIGIBLE_INDICATOR IN ('E')THEN 'ELIGIBLE' "
				+ "  WHEN  T2.ELIGIBLE_INDICATOR IN ('IE')THEN 'INELIGIBLE' "
				+ " END AS ELIG_IND,  CASE "
				+ " WHEN T2.CUST_GSTIN is not null then 'R' "
				+ "  WHEN T2.CUST_GSTIN IS NULL THEN 'UR'   END as Type, "
				+ " T2.CUST_GSTIN AS cpty,  T2.STATE_CODE,  '' as Chksum, "
				+ "  case "
				+ " WHEN T2.DOC_TYPE = 'INV' AND T2.CUST_GSTIN is not null THEN 'ISD' "
				+ " WHEN T2.DOC_TYPE = 'INV' AND T2.CUST_GSTIN is  null THEN 'ISDUR' "
				+ " WHEN T2.DOC_TYPE = 'CR' AND T2.CUST_GSTIN is not null THEN 'ISDCN' "
				+ " WHEN T2.DOC_TYPE = 'CR' AND T2.CUST_GSTIN is  null THEN 'ISDCNUR' "
				+ " END as ISD_DOCTY,T2.DOC_NUM,TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY') as DOC_DATE,"
				+ "T2.ORG_DOC_NUM,TO_CHAR(T2.ORG_DOC_DATE,'DD-MM-YYYY') as ORG_DOC_DATE,"
				+ "SUM(IFNULL(T2.IGST_AMT_AS_IGST,0)+IFNULL(T2.CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(T2.SGST_AMT_AS_IGST,0)) AS IGST_AMT_AS_IGST,"
				+ " 0 AS IGST_AMT_AS_SGST, 0 AS IGST_AMT_AS_CGST,"
				+ "SUM(IFNULL(T2.SGST_AMT_AS_SGST,0)+IFNULL(T2.IGST_AMT_AS_SGST,0)) AS SGST_AMT_AS_SGST,"
				+ " 0 AS SGST_AMT_AS_IGST, 0 AS CGST_AMT_AS_IGST,"
				+ " SUM( IFNULL(T2.IGST_AMT_AS_CGST,0)+IFNULL(T2.CGST_AMT_AS_CGST,0)) AS CGST_AMT_AS_CGST,"
				+ " SUM(T2.CESS_AMT) AS CESS_AMT FROM GSTR6_ISD_DISTRIBUTION T1 "
				+ " INNER JOIN GSTR6_ISD_DISTRIBUTION T2  on  T1.ID <> T2.ID AND "
				+ " T1.DOC_KEY = T2.DOC_KEY AND T1.IS_DELETE = TRUE AND T2.IS_DELETE = FALSE "
				+ " AND T1.IS_SAVED_TO_GSTN = TRUE  AND T2.IS_SENT_TO_GSTN = FALSE AND "
				+ " T1.SUPPLY_TYPE IS NULL AND T2.SUPPLY_TYPE ='CAN' WHERE  T2.IS_DELETE = FALSE "
				/* + " AND  T2.DOC_TYPE IN ('INV','CR') " */
				+ " AND T2.IS_SENT_TO_GSTN = FALSE " + buildQuery1
				+ " GROUP BY T2.ID,T2.ISD_GSTIN,T2.TAX_PERIOD,T2.CUST_GSTIN,T2.STATE_CODE,"
				+ "T2.DOC_NUM,T2.DOC_DATE,T2.ORG_DOC_NUM,T2.ORG_DOC_DATE,T2.ELIGIBLE_INDICATOR,"
				+ " T2.DOC_TYPE ORDER BY cpty,Type,T2.STATE_CODE,T2.ID";
	}

	@Override
	public String buildGstr6CanIsdaQuery(String gstin, String retPeriod,
			String isdDocType) {

		StringBuilder buildQuery1 = new StringBuilder();
		if (gstin != null && !gstin.isEmpty()) {
			buildQuery1.append(" AND T2.ISD_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery1.append(" AND T2.TAX_PERIOD = :retPeriod ");
		}
		if (isdDocType != null && !isdDocType.isEmpty()) {
			buildQuery1.append(" AND T2.DOC_TYPE = :isdDocType  ");
		}
		return "select T2.ID,T2.ISD_GSTIN,T2.TAX_PERIOD,  CASE "
				+ " WHEN  T2.ELIGIBLE_INDICATOR IN ('IS','E')THEN 'ELIGIBLE' "
				+ " WHEN  T2.ELIGIBLE_INDICATOR IN ('NO','IE')THEN 'INELIGIBLE' "
				+ " END AS ELIG_IND, CASE "
				+ " WHEN T2.CUST_GSTIN is not null then 'R' "
				+ " WHEN T2.CUST_GSTIN IS NULL THEN 'UR' END as Type,"
				+ "T2.CUST_GSTIN AS cpty,T2.STATE_CODE,'' as Chksum,  case "
				+ " WHEN T2.DOC_TYPE = 'RNV' AND T2.CUST_GSTIN is not null THEN 'ISDA' "
				+ " WHEN T2.DOC_TYPE = 'RNV' AND T2.CUST_GSTIN is  null THEN 'ISDURA' "
				+ "  WHEN T2.DOC_TYPE = 'RCR' AND T2.CUST_GSTIN is not null THEN 'ISDCNA' "
				+ " WHEN T2.DOC_TYPE = 'RCR' AND T2.CUST_GSTIN is  null THEN 'ISDCNURA' "
				+ " END as ISD_DOCTY,"
				+ "T2.DOC_NUM,TO_CHAR(T2.DOC_DATE,'DD-MM-YYYY') as DOC_DATE,T2.ORG_CR_NUM,"
				+ "TO_CHAR(T2.ORG_CR_DATE,'DD-MM-YYYY') as ORG_CR_DATE,"
				+ "SUM(IFNULL(T2.IGST_AMT_AS_IGST,0)+IFNULL(T2.CGST_AMT_AS_IGST,0)+"
				+ "IFNULL(T2.SGST_AMT_AS_IGST,0)) as IGST_AMT_AS_IGST,"
				+ "0 as IGST_AMT_AS_SGST,0 as IGST_AMT_AS_CGST,"
				+ "SUM(IFNULL(T2.SGST_AMT_AS_SGST,0)+IFNULL(T2.IGST_AMT_AS_SGST,0)) "
				+ " as SGST_AMT_AS_SGST,0 as SGST_AMT_AS_IGST,0 as CGST_AMT_AS_IGST,"
				+ " SUM( IFNULL(T2.IGST_AMT_AS_CGST,0)+IFNULL(T2.CGST_AMT_AS_CGST,0)) "
				+ " as CGST_AMT_AS_CGST,SUM(T2.CESS_AMT) AS CESS_AMT "
				+ " FROM GSTR6_ISD_DISTRIBUTION T1 INNER JOIN GSTR6_ISD_DISTRIBUTION T2 "
				+ "  on T1.ID <> T2.ID AND T1.DOC_KEY = T2.DOC_KEY AND T1.IS_DELETE = TRUE "
				+ " AND T2.IS_DELETE = FALSE AND T1.IS_SENT_TO_GSTN = TRUE "
				+ " AND T2.IS_SAVED_TO_GSTN = FALSE AND  T1.SUPPLY_TYPE IS NULL"
				+ " WHERE T2.IS_DELETE = FALSE "
				/* + " AND T2.DOC_TYPE IN ('RNV','RCR') " */
				+ " AND T2.SUPPLY_TYPE IS NULL AND T2.SUPPLY_TYPE ='CAN' AND T2.IS_SENT_TO_GSTN = FALSE "
				+ buildQuery1
				+ " GROUP BY T2.ID,T2.ISD_GSTIN,T2.TAX_PERIOD,T2.CUST_GSTIN,T2.STATE_CODE,"
				+ "T2.DOC_NUM,T2.DOC_DATE,T2.ORG_CR_NUM,T2.ORG_CR_DATE,T2.ELIGIBLE_INDICATOR,T2.DOC_TYPE"
				+ " ORDER BY cpty,T2.STATE_CODE,T2.ID";
	}

	// ----------------------------ITC04
	// SAVE--------------------------------------//
	// M2JW Table 4
	@Override
	public String buildItc04M2jwSaveQuery(String gstin, String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND HDR.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,"
				+ "JW_GSTIN AS CTIN ,JW_STATE_CODE,"
				+ "DELIVERY_CHALLAN_NO,TO_CHAR(DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS DELIVERY_CHALLAN_DATE,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "CASE WHEN GOODS_TYPE IN('IG') THEN '8b' WHEN GOODS_TYPE IN('CG') THEN '7b' END as GOODS_TYPE,"
				+ "ITM_UQC,IFNULL(SUM(ITM.ITM_QTY),0) AS ITM_QTY,"
				+ "IGST_RATE,CGST_RATE,SGST_RATE,CESS_RATE_SPECIFIC,"
				+ "CESS_RATE_ADVALOREM,PRODUCT_DESC,HDR.SAVE_FLAG "
				+ " FROM  ITC04_HEADER HDR " + "	INNER JOIN ITC04_ITEM ITM "
				+ " ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ " HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ " WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE "
				+ " AND TABLE_NUMBER='4' AND "
				+ " (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ " AND IS_SENT_TO_GSTN = FALSE " + buildQuery
				+ " GROUP BY HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,"
				+ "JW_GSTIN,JW_STATE_CODE,DELIVERY_CHALLAN_NO,"
				+ "DELIVERY_CHALLAN_DATE,GOODS_TYPE,ITM_UQC,ITM_QTY,"
				+ "IGST_RATE,CGST_RATE,SGST_RATE,"
				+ "CESS_RATE_SPECIFIC,CESS_RATE_ADVALOREM,PRODUCT_DESC,HDR.SAVE_FLAG "
				+ " ORDER BY HDR.ID ";
	}

	// TABLE5A
	@Override
	public String buildItc04Table5aSaveQuery(String gstin, String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND HDR.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN AS CTIN,"
				+ "JW_STATE_CODE,DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,"
				+ "JW_DELIVERY_CHALLAN_NO,TO_CHAR(JW_DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') "
				+ " AS JW_DELIVERY_CHALLAN_DATE,"
				+ "NATURE_OF_JW,ITM_UQC,IFNULL(SUM(ITM.ITM_QTY),0) AS ITM_QTY,PRODUCT_DESC,"
				+ "LOSSES_UQC,IFNULL(SUM(ITM.LOSSES_QTY),0) AS LOSSES_QTY,HDR.SAVE_FLAG "
				+ " FROM ITC04_HEADER HDR " + " INNER JOIN  ITC04_ITEM ITM "
				+ " ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ " HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ " WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE AND "
				+ " TABLE_NUMBER IN ('5A') "
				+ " AND (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ " AND IS_SENT_TO_GSTN = FALSE " + buildQuery
				+ " GROUP BY HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN,JW_STATE_CODE,"
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,JW_DELIVERY_CHALLAN_NO,"
				+ "JW_DELIVERY_CHALLAN_DATE,NATURE_OF_JW,ITM_UQC,PRODUCT_DESC,LOSSES_UQC,HDR.SAVE_FLAG "
				+ " ORDER BY HDR.ID ";
	}

	// TABLE5B
	@Override
	public String buildItc04Table5bSaveQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND HDR.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN AS CTIN,"
				+ "JW_STATE_CODE,DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,"
				+ "JW_DELIVERY_CHALLAN_NO,TO_CHAR(JW_DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') "
				+ " AS JW_DELIVERY_CHALLAN_DATE,"
				+ "NATURE_OF_JW,ITM_UQC,IFNULL(SUM(ITM.ITM_QTY),0) AS ITM_QTY,PRODUCT_DESC,"
				+ "LOSSES_UQC,IFNULL(SUM(ITM.LOSSES_QTY),0) AS LOSSES_QTY,HDR.SAVE_FLAG "
				+ " FROM  ITC04_HEADER HDR " + " INNER JOIN ITC04_ITEM ITM "
				+ " ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ "  HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ " WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE AND "
				+ " TABLE_NUMBER IN ('5B') AND "
				+ " (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ " AND IS_SENT_TO_GSTN = FALSE " + buildQuery
				+ " GROUP BY HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN,JW_STATE_CODE,"
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,JW_DELIVERY_CHALLAN_NO,"
				+ "JW_DELIVERY_CHALLAN_DATE,NATURE_OF_JW,ITM_UQC,PRODUCT_DESC,LOSSES_UQC,HDR.SAVE_FLAG "
				+ " ORDER BY HDR.ID ";
	}

	// TABLE5C
	@Override
	public String buildItc04Table5cSaveQuery(String gstin, String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND HDR.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN AS CTIN,"
				+ "JW_STATE_CODE,DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,INV_NUM,"
				+ "TO_CHAR(INV_DATE,'DD-MM-YYYY') AS INV_DATE,"
				+ "NATURE_OF_JW,ITM_UQC,IFNULL(SUM(ITM.ITM_QTY),0) AS ITM_QTY,PRODUCT_DESC,"
				+ "LOSSES_UQC,IFNULL(SUM(ITM.LOSSES_QTY),0) AS LOSSES_QTY,HDR.SAVE_FLAG "
				+ " FROM ITC04_HEADER HDR " + " INNER JOIN  ITC04_ITEM ITM "
				+ " ON HDR.ID=ITM.DOC_HEADER_ID AND "
				+ " HDR.RETURN_PERIOD=ITM.RETURN_PERIOD "
				+ " WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE "
				+ " AND TABLE_NUMBER IN ('5C') AND "
				+ "  (ACTION_TYPE <> 'CAN' OR ACTION_TYPE IS NULL) "
				+ " AND IS_SENT_TO_GSTN = FALSE " + buildQuery
				+ " GROUP BY HDR.ID,SUPPLIER_GSTIN,HDR.QRETURN_PERIOD,JW_GSTIN,JW_STATE_CODE,"
				+ "DELIVERY_CHALLAN_NO,DELIVERY_CHALLAN_DATE,INV_NUM,INV_DATE,"
				+ "NATURE_OF_JW,ITM_UQC,PRODUCT_DESC,LOSSES_UQC,HDR.SAVE_FLAG "
				+ " ORDER BY HDR.ID ";
	}

	// ----------------------ITC04 CAN-----------------------------//
	// Table-4 Can(m2jw)
	@Override
	public String buildItc04M2jwCanQuery(String gstin, String retPeriod) {

		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND C.SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND C.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN AS CTIN,"
				+ "C.JW_STATE_CODE,C.DELIVERY_CHALLAN_NO,"
				+ "TO_CHAR(C.DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS DELIVERY_CHALLAN_DATE,"
				+ "IFNULL(SUM(I.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "CASE WHEN I.GOODS_TYPE IN('IG') THEN '8b' WHEN I.GOODS_TYPE IN('CG') THEN '7b' END as GOODS_TYPE,"
				+ "I.ITM_UQC,IFNULL(SUM(I.ITM_QTY),0) AS ITM_QTY,"
				+ "I.IGST_RATE,I.CGST_RATE,I.SGST_RATE,I.CESS_RATE_SPECIFIC,"
				+ "I.CESS_RATE_ADVALOREM,I.PRODUCT_DESC,C.DOC_KEY,C.IS_SENT_TO_GSTN,N.ID as NID "
				+ " FROM ITC04_HEADER N INNER JOIN ITC04_HEADER C "
				+ " ON N.ID<>C.ID AND N.DOC_KEY=C.DOC_KEY AND "
				+ " N.IS_DELETE=TRUE AND C.IS_DELETE=FALSE AND N.IS_PROCESSED=TRUE "
				+ " AND  C.IS_PROCESSED=TRUE AND N.IS_SAVED_TO_GSTN=TRUE "
				+ " AND C.IS_SENT_TO_GSTN = FALSE AND C.TABLE_NUMBER='4' AND C.ACTION_TYPE='CAN' AND "
				+ " (N.ACTION_TYPE <>'CAN' OR N.ACTION_TYPE IS NULL) "
				+ buildQuery + " INNER JOIN ITC04_ITEM I "
				+ " ON C.ID=I.DOC_HEADER_ID AND C.QRETURN_PERIOD=I.QRETURN_PERIOD "
				+ " GROUP BY C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN,C.JW_STATE_CODE,"
				+ "C.DELIVERY_CHALLAN_NO,C.DELIVERY_CHALLAN_DATE,I.GOODS_TYPE,I.ITM_UQC,"
				+ "I.IGST_RATE,I.CGST_RATE,I.SGST_RATE,I.CESS_RATE_SPECIFIC,"
				+ "I.CESS_RATE_ADVALOREM,I.PRODUCT_DESC,C.DOC_KEY,C.IS_SENT_TO_GSTN,N.ID "
				+ " ORDER BY C.ID ";
	}

	// Table5A CAN
	@Override
	public String buildItc04Table5aCanQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND C.SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND C.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT  C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN AS CTIN,"
				+ "C.JW_STATE_CODE,C.DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(C.DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,C.JW_DELIVERY_CHALLAN_NO,"
				+ "TO_CHAR(C.JW_DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') "
				+ " AS JW_DELIVERY_CHALLAN_DATE,I.NATURE_OF_JW,I.ITM_UQC,IFNULL(SUM(I.ITM_QTY),0) AS ITM_QTY,"
				+ "I.PRODUCT_DESC,I.LOSSES_UQC,IFNULL(SUM(I.LOSSES_QTY),0) AS LOSSES_QTY "
				+ " FROM ITC04_HEADER N INNER JOIN  ITC04_HEADER C ON N.ID<>C.ID AND N.DOC_KEY=C.DOC_KEY AND "
				+ " N.IS_DELETE=TRUE AND C.IS_DELETE=FALSE AND N.IS_PROCESSED=TRUE AND "
				+ " C.IS_PROCESSED=TRUE AND N.IS_SAVED_TO_GSTN=TRUE "
				+ " AND C.IS_SENT_TO_GSTN = FALSE AND C.TABLE_NUMBER='5A' AND C.ACTION_TYPE='CAN' AND "
				+ " (N.ACTION_TYPE <>'CAN' OR N.ACTION_TYPE IS NULL) "
				+ buildQuery
				+ " INNER JOIN ITC04_ITEM I ON C.ID=I.DOC_HEADER_ID AND C.QRETURN_PERIOD=I.QRETURN_PERIOD "
				+ " GROUP BY C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN,C.JW_STATE_CODE,"
				+ "C.DELIVERY_CHALLAN_NO,C.DELIVERY_CHALLAN_DATE,C.JW_DELIVERY_CHALLAN_NO,"
				+ "C.JW_DELIVERY_CHALLAN_DATE,I.NATURE_OF_JW,I.ITM_UQC,I.PRODUCT_DESC,I.LOSSES_UQC "
				+ " ORDER BY C.ID ";
	}

	// Table5B CAN
	@Override
	public String buildItc04Table5bCanQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND C.SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND C.QRETURN_PERIOD = :retPeriod ");
		}
		return "SELECT  C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN AS CTIN,"
				+ "C.JW_STATE_CODE,C.DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(C.DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,C.JW_DELIVERY_CHALLAN_NO,"
				+ "TO_CHAR(C.JW_DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS JW_DELIVERY_CHALLAN_DATE,"
				+ "I.NATURE_OF_JW,I.ITM_UQC,IFNULL(SUM(I.ITM_QTY),0) AS ITM_QTY,"
				+ "I.PRODUCT_DESC,I.LOSSES_UQC,IFNULL(SUM(I.LOSSES_QTY),0) AS LOSSES_QTY"
				+ " FROM ITC04_HEADER N INNER JOIN ITC04_HEADER C "
				+ " ON N.ID<>C.ID AND N.DOC_KEY=C.DOC_KEY AND N.IS_DELETE=TRUE AND "
				+ "  C.IS_DELETE=FALSE AND N.IS_PROCESSED=TRUE AND C.IS_PROCESSED=TRUE "
				+ " AND N.IS_SAVED_TO_GSTN=TRUE AND C.IS_SENT_TO_GSTN = FALSE"
				+ " AND C.TABLE_NUMBER='5B' AND C.ACTION_TYPE='CAN' AND "
				+ " (N.ACTION_TYPE <>'CAN' OR N.ACTION_TYPE IS NULL) "
				+ buildQuery
				+ " INNER JOIN ITC04_ITEM I ON C.ID=I.DOC_HEADER_ID AND C.QRETURN_PERIOD=I.QRETURN_PERIOD "
				+ " GROUP BY C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN,C.JW_STATE_CODE,"
				+ "C.DELIVERY_CHALLAN_NO,C.DELIVERY_CHALLAN_DATE,C.JW_DELIVERY_CHALLAN_NO,"
				+ "C.JW_DELIVERY_CHALLAN_DATE,I.NATURE_OF_JW,I.ITM_UQC,I.PRODUCT_DESC,I.LOSSES_UQC "
				+ " ORDER BY C.ID ";
	}

	// Table5C CAN
	@Override
	public String buildItc04Table5cCanQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND C.SUPPLIER_GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND C.QRETURN_PERIOD = :retPeriod ");
		}
		return "Select  C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN AS CTIN,"
				+ "C.JW_STATE_CODE,C.DELIVERY_CHALLAN_NO AS ORG_CHNUM,"
				+ "TO_CHAR(C.DELIVERY_CHALLAN_DATE,'DD-MM-YYYY') AS ORG_CHDT,"
				+ "C.INV_NUM,TO_CHAR(C.INV_DATE,'DD-MM-YYYY') AS INV_DATE,I.NATURE_OF_JW,I.ITM_UQC,"
				+ "IFNULL(SUM(I.ITM_QTY),0) AS ITM_QTY,I.PRODUCT_DESC,I.LOSSES_UQC,"
				+ "IFNULL(SUM(I.LOSSES_QTY),0) AS LOSSES_QTY FROM ITC04_HEADER N INNER JOIN ITC04_HEADER C "
				+ " ON N.ID<>C.ID AND N.DOC_KEY=C.DOC_KEY AND N.IS_DELETE=TRUE AND C.IS_DELETE=FALSE AND "
				+ " N.IS_PROCESSED=TRUE AND C.IS_PROCESSED=TRUE "
				+ " AND N.IS_SAVED_TO_GSTN=TRUE AND C.IS_SENT_TO_GSTN = FALSE "
				+ " AND C.TABLE_NUMBER='5C' AND C.ACTION_TYPE='CAN' "
				+ " AND (N.ACTION_TYPE <>'CAN' OR N.ACTION_TYPE IS NULL) "
				+ buildQuery + " INNER JOIN ITC04_ITEM I "
				+ " ON C.ID=I.DOC_HEADER_ID AND C.QRETURN_PERIOD=I.QRETURN_PERIOD "
				+ " GROUP BY C.ID,C.SUPPLIER_GSTIN,C.QRETURN_PERIOD,C.JW_GSTIN,C.JW_STATE_CODE,"
				+ "C.DELIVERY_CHALLAN_NO,C.DELIVERY_CHALLAN_DATE,C.INV_NUM,C.INV_DATE,"
				+ "I.NATURE_OF_JW,I.ITM_UQC,I.PRODUCT_DESC,I.LOSSES_UQC "
				+ " ORDER BY C.ID ";
	}
	// ---------------------------GSTR2X SAVE-------------------------------//

	@Override
	public String buildGstr2xTdsSaveQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND FIL.GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND FIL.RET_PERIOD = :retPeriod ");
		}
		return "SELECT FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,"
				+ " FIL.DOC_NO, FIL.DOC_DATE "
				+ "FROM GSTR2X_PROCESSED_TCS_TDS FIL "
				+ " INNER JOIN GETGSTR2X_TDS_TDSA GET ON FIL.GSTIN=GET.GSTIN "
				+ " AND FIL.RET_PERIOD=GET.RET_PERIOD AND FIL.DOC_KEY=GET.DOC_KEY "
				+ " WHERE FIL.IS_DELETE=FALSE AND DATAORIGINTYPECODE IN ('E','U') "
				+ "AND FIL.RECORD_TYPE='TDS' "
				+ " AND USER_ACTION IN('A','R')  AND FIL.IS_SENT_TO_GSTN= FALSE "
				+ buildQuery
				+ " GROUP BY FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.DEDUCTOR_UPL_MONTH,FIL.ORG_DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,"
				+ " FIL.DOC_NO, FIL.DOC_DATE";
	}

	@Override
	public String buildGstr2xTdsaSaveQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND FIL.GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND FIL.RET_PERIOD = :retPeriod ");
		}
		return "SELECT FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.ORG_DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,"
				+ "FIL.DEDUCTOR_UPL_MONTH,FIL.DOC_NO, FIL.DOC_DATE"
				+ " FROM GSTR2X_PROCESSED_TCS_TDS FIL "
				+ " INNER JOIN GETGSTR2X_TDS_TDSA GET ON FIL.GSTIN=GET.GSTIN "
				+ " AND FIL.RET_PERIOD=GET.RET_PERIOD AND FIL.DOC_KEY=GET.DOC_KEY "
				+ " WHERE FIL.IS_DELETE=FALSE AND DATAORIGINTYPECODE IN ('E','U') AND FIL.RECORD_TYPE='TDSA' "
				+ " AND USER_ACTION IN('A','R')  AND FIL.IS_SENT_TO_GSTN= FALSE "
				+ buildQuery
				+ " GROUP BY FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.ORG_DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,FIL.DEDUCTOR_UPL_MONTH,"
				+ "FIL.DOC_NO, FIL.DOC_DATE";
	}

	@Override
	public String buildGstr2xTcsSaveQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND FIL.GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND FIL.RET_PERIOD = :retPeriod ");
		}
		return "SELECT FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION, FIL.DIGIGST_REMARKS, "
				+ "FIL.DIGIGST_COMMENT, FIL.POS FROM GSTR2X_PROCESSED_TCS_TDS FIL "
				+ " INNER JOIN GETGSTR2X_TCS_TCSA GET ON FIL.GSTIN=GET.GSTIN "
				+ " AND FIL.RET_PERIOD=GET.RET_PERIOD AND FIL.DOC_KEY=GET.DOC_KEY "
				+ " WHERE FIL.IS_DELETE=FALSE AND DATAORIGINTYPECODE IN ('E','U') "
				+ "AND FIL.RECORD_TYPE='TCS' "
				+ " AND USER_ACTION IN('A','R')  AND FIL.IS_SENT_TO_GSTN= FALSE "
				+ buildQuery
				+ " GROUP BY FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,FIL.DIGIGST_REMARKS, FIL.DIGIGST_COMMENT, FIL.POS";

	}

	@Override
	public String buildGstr2xTcsaSaveQuery(String gstin, String retPeriod) {
		StringBuilder buildQuery = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			buildQuery.append(" AND FIL.GSTIN = :gstin ");
		}
		if (retPeriod != null && !retPeriod.isEmpty()) {
			buildQuery.append(" AND FIL.RET_PERIOD = :retPeriod ");
		}
		return "SELECT FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.ORG_DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION, FIL.DEDUCTOR_UPL_MONTH,FIL.DIGIGST_REMARKS, "
				+ "FIL.DIGIGST_COMMENT,FIL.POS FROM GSTR2X_PROCESSED_TCS_TDS FIL "
				+ " INNER JOIN GETGSTR2X_TCS_TCSA GET ON FIL.GSTIN=GET.GSTIN "
				+ " AND FIL.RET_PERIOD=GET.RET_PERIOD AND FIL.DOC_KEY=GET.DOC_KEY "
				+ " WHERE FIL.IS_DELETE=FALSE AND DATAORIGINTYPECODE IN ('E','U') AND FIL.RECORD_TYPE='TCSA' "
				+ " AND USER_ACTION IN('A','R')  AND FIL.IS_SENT_TO_GSTN= FALSE "
				+ buildQuery
				+ " GROUP BY FIL.ID,FIL.RECORD_TYPE,FIL.GSTIN,FIL.CTIN,FIL.RET_PERIOD,"
				+ "FIL.ORG_DEDUCTOR_UPL_MONTH,CHKSUM,USER_ACTION,FIL.DEDUCTOR_UPL_MONTH,FIL.DIGIGST_REMARKS, "
				+ "FIL.DIGIGST_COMMENT,FIL.POS ";
	}

	@Override
	public String buildGstr7TransCancelledQuery(String gstin,
			String returnPeriod, String supplyType, ProcessingContext context) {

		LOGGER.debug("inside buildCancelledQuery method with args {}{}", gstin,
				returnPeriod);

		StringBuilder criteria = userCriteriaBuilder
				.buildGstr7TransCancelledUserCriteria(gstin, returnPeriod,
						supplyType);

		return "SELECT doc.id, doc.section,doc2.id FROM "
				+ "Gstr7TransDocHeaderEntity doc,Gstr7TransDocHeaderEntity doc2 "
				+ "WHERE doc.id <> doc2.id AND doc.docKey=doc2.docKey "
				+ "AND doc.isDelete = TRUE AND doc2.isDelete = FALSE "
				+ "AND doc.isSentToGstn = TRUE AND doc2.isSentToGstn = FALSE  "
				+ criteria + " ORDER BY doc.section, doc.id";

	}

	@Override
	public String buildGstr7TDSQuery(String gstin, String retPeriod,
			String docType, Set<Long> orgDocIds, ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildB2bB2baQuery method with args {}{}",
					gstin, retPeriod);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr7TransUserCriteria(gstin, retPeriod, docType,
						orgDocIds, context);

		String query = "SELECT doc.deductorGstin, doc.returnPeriod, doc.deducteeGstin,"
				+ " doc.docNum, doc.docDate, doc.invoiceValue, doc.taxableValue, "
				+ " doc.igstAmt, doc.cgstAmt, doc.sgstAmt, doc.id, "
				+ "doc.supplyType, doc.section "
				+ "FROM Gstr7TransDocHeaderEntity doc WHERE " + criteria
				+ " ORDER BY doc.deductorGstin, doc.returnPeriod, doc.deducteeGstin, doc.id";
		return query;

	}

	@Override
	public String buildGstr7TDSAQuery(String gstin, String retPeriod,
			String docType, Set<Long> orgDocIds, ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside buildGstr7TDSAQuery method with args {}{}",
					gstin, retPeriod);
		}
		StringBuilder criteria = userCriteriaBuilder
				.buildGstr7TransUserCriteria(gstin, retPeriod, docType,
						orgDocIds, context);

		String query = "SELECT doc.deductorGstin, doc.returnPeriod, doc.originalReturnPeriod, doc.deducteeGstin,"
				+ "doc.originalDeducteeGstin,"
				+ " doc.docNum, doc.originalDocNum, doc.docDate, doc.originalDocDate, "
				+ "doc.invoiceValue, doc.originalInvoiceValue, doc.taxableValue, "
				+ "doc.originalTaxableValue, "
				+ " doc.igstAmt, doc.cgstAmt, doc.sgstAmt, doc.id, "
				+ "doc.supplyType, doc.section "
				+ "FROM Gstr7TransDocHeaderEntity doc WHERE " + criteria
				+ " ORDER BY doc.deductorGstin, doc.returnPeriod, doc.deducteeGstin, doc.id";
		return query;

	}

}
