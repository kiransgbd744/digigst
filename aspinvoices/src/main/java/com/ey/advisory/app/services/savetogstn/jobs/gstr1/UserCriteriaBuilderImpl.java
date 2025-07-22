/*package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Slf4j
@Service("userCriteriaBuilderImpl")
public class UserCriteriaBuilderImpl implements UserCriteriaBuilder {

	@Override
	public StringBuilder buildGstr1CancelledUserCriteria(String sgstin,
			String retPeriod, String supplyType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"building the user criteria in buildGstr1CancelledUserCriteria");
		}
		StringBuilder build = new StringBuilder();
		if (sgstin != null && retPeriod != null) {
			build.append(" AND doc.sgstin = :o_Gstin");
			build.append(" AND doc.taxperiod = :o_RetPeriod");
			build.append(" AND doc2.sgstin = :c_Gstin");
			build.append(" AND doc2.taxperiod = :c_RetPeriod");
			if (supplyType != null && supplyType.trim().length() > 0) {
				build.append(" AND doc.supplyType != :o_SupplyType");
				build.append(" AND doc2.supplyType = :c_SupplyType");
			}
		}

		return build;
	}
	

	@Override
	public StringBuilder buildGstr1HorizontalUserCriteria(String sgstin,
			String retPeriod, String docType, String tableType,
			Set<Long> orgDocIds) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("building the user criteria");
		}
		StringBuilder build = new StringBuilder();

		if (sgstin != null && retPeriod != null) {
			build.append(" AND doc.sgstin = :gstins");
			build.append(" AND doc.taxperiod = :retPeriod");
			if (docType != null && !docType.isEmpty()) {
				build.append(" AND doc.gstnBifurcation IN :docType");
			}
			if (tableType != null && !tableType.isEmpty()) {
				build.append(" AND doc.tableType IN :tableType");
			}
			// CAN invoices (for deleting the invoices)
			if (orgDocIds != null && !orgDocIds.isEmpty()) {
				build.append(" AND doc.id IN :docIds");
				build.append(" AND doc.isSent = TRUE");
				build.append(" AND doc.isDeleted = TRUE");
			} else {
				build.append(" AND doc.isSent = FALSE");
				build.append(" AND doc.isDeleted = FALSE");
			}
		}

		return build;

	}

	@Override
	public String buildGstr1VeritalUserCriteria(String sgstin, String retPeriod,
			String docType, String tableType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("building the user criteria");
		}
		String criteria = "";

		StringBuilder build = new StringBuilder();

		if (!sgstin.isEmpty() && !retPeriod.isEmpty()) {
			build.append(" AND doc.sgstin = :gstins");
			build.append(" AND doc.returnPeriod = :retPeriod");
			build.append(" AND doc.isSentToGstn = FALSE");

		}
		if(!docType.equalsIgnoreCase(APIConstants.HSNSUM) &&
				!docType.equalsIgnoreCase(APIConstants.NIL) &&
				!docType.equalsIgnoreCase(APIConstants.DOCISS)) {
			build.append(" AND doc.gstnBifurcation = :docType");
		}
		criteria = build.toString();
		return criteria;

	}

	@Override
	public StringBuilder buildGstr1DependentRetryHorizontalUserCriteria(
			Long batchId, Set<String> errorCodes) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"building the user criteria buildGstr1DependentRetryHorizontalUserCriteria");
		}
		StringBuilder build = new StringBuilder();

		if (batchId != null && errorCodes != null && !errorCodes.isEmpty()) {
			build.append(" AND doc.gstnBatchId = :batchId");
			build.append(" AND doc.gstnErrorCode IN :errorCodes");

		}

		return build;

	}

	@Override
	public StringBuilder buildAnx1CancelledUserCriteria(String sgstin,
			String retPeriod, String supplyType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"building the user criteria in buildCancelledUserCriteria");
		}
		StringBuilder build = new StringBuilder();

		if (sgstin != null && retPeriod != null) {
			build.append(" AND doc2.sgstin = :gstin");
			build.append(" AND doc2.taxperiod = :retPeriod");
			if (supplyType != null && supplyType.trim().length() > 0) {
				build.append(" AND doc2.supplyType = :supplyType");
			}
		}

		return build;
	}

	@Override
	public StringBuilder buildAnx1HorizontalUserCriteria(String sgstin,
			String retPeriod, String docType, String tableType,
			List<Long> docIds) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("building the user criteria");
		}
		StringBuilder build = new StringBuilder();

		if (sgstin != null && retPeriod != null) {
			if (BifurcationConstants.TAX_DOC_TYPE_IMPG.equalsIgnoreCase(docType)
					|| BifurcationConstants.TAX_DOC_TYPE_SEZG
							.equalsIgnoreCase(docType)
					|| APIConstants.MIS.equalsIgnoreCase(docType)) {
				// Inward
				build.append(" AND HDR.CUST_GSTIN = :gstins");
			} else {
				// Outward
				build.append(" AND HDR.SUPPLIER_GSTIN = :gstins");
			}

			build.append(" AND HDR.RETURN_PERIOD = :retPeriod");
			
			if (docType != null && !docType.isEmpty()) {
				build.append(" AND HDR.AN_TAX_DOC_TYPE IN :docType");
			}
			if (tableType != null && !tableType.isEmpty()) {
				build.append(" AND HDR.AN_TABLE_SECTION IN :tableType");
			}
			// CAN invoices (for deleting the invoices)
			if (docIds != null && !docIds.isEmpty()) {
				build.append(" AND HDR.ID IN :docIds");
				build.append(" AND HDR.IS_SENT_TO_GSTN = TRUE");
				build.append(" AND HDR.IS_DELETE = TRUE");
			} else {
				build.append(" AND HDR.IS_SENT_TO_GSTN = FALSE");
				build.append(" AND HDR.IS_DELETE = FALSE");
			}
		}

		return build;

	}

	@Override
	public StringBuilder buildAnx1VeritalUserCriteria(String sgstin,
			String retPeriod, String docType, String tableType) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("building the user criteria");
		}
		StringBuilder build = new StringBuilder();

		// Outward data
		if (BifurcationConstants.TAX_DOC_TYPE_B2C.equalsIgnoreCase(docType)
				|| APIConstants.ECOM.equalsIgnoreCase(docType)) {
			if (!sgstin.isEmpty() && !retPeriod.isEmpty()) {
				build.append(" AND SUPPLIER_GSTIN = :gstins");
				build.append(" AND RETURN_PERIOD = :retPeriod");
			}
		} else {
			// Inward data
			if (!sgstin.isEmpty() && !retPeriod.isEmpty()) {
				build.append(" AND CUST_GSTIN = :gstins");
				build.append(" AND RET_PERIOD = :retPeriod");
			}
		}
		build.append(" AND IS_DELETE = FALSE");

		return build;

	}

	public StringBuilder buildAnx2UserCriteria(String sgstin, String retPeriod,
			String docType, String tableType, List<Long> docIds) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Building user criteria fo Anx2");
		}
		StringBuilder build = new StringBuilder();

		if (sgstin != null && retPeriod != null) {
			build.append(" AND A2_RECIPIENT_GSTIN = :gstins");
			build.append(" AND RETURN_PERIOD = :retPeriod");

			if (docType != null && !docType.isEmpty()) {
				build.append(" AND TABLE_TYPE IN :docType");
			}
			
			 * if (docType != null && !docType.isEmpty()) {
			 * build.append(" AND A2_DOC_TYPE IN :docType"); } if (tableType !=
			 * null && !tableType.isEmpty()) {
			 * build.append(" AND A2_TABLE IN :tableType"); }
			 
			// CAN invoices (for deleting the invoices)
			if (docIds != null && !docIds.isEmpty()) {
				build.append(" AND ID IN :docIds");
				build.append(" AND IS_SENT_TO_GSTN = TRUE");
			} else {
				build.append(" AND IS_SENT_TO_GSTN = FALSE");
			}
		}

		return build;

	}

}
*/