package com.ey.advisory.app.data.services.qrcodevalidator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("QRValidatorPredicateBuilder")
public class QRValidatorPredicateBuilder {

	public List<Predicate> buildAsycnExecJobSearchPredicates(
			QRSearchParams qrSearchParams, Root<QRResponseSummaryEntity> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (qrSearchParams == null) {
			LOGGER.debug("QR Params are Blank");
			return predicates;
		}

		// entityId
		if (qrSearchParams.getEntityId() != null && !qrSearchParams.getEntityId().isEmpty()) {
			Predicate entityIdPredicate = criteriaBuilder.equal(root.get("entityId"), 
					qrSearchParams.getEntityId());
			Predicate entityIdNullPredicate = criteriaBuilder.isNull(root.get("entityId"));
			predicates.add(criteriaBuilder.or(entityIdPredicate, entityIdNullPredicate));
		}

		// buyerGstin
		if (qrSearchParams.getRecipientGstins() != null
				&& !qrSearchParams.getRecipientGstins().isEmpty()) {

			if (qrSearchParams.getRecipientGstins().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("buyerGstin"),
						qrSearchParams.getRecipientGstins().get(0)));
			} else {
				predicates.add(root.get("buyerGstin")
						.in(qrSearchParams.getRecipientGstins()));
			}
		}
		// SellerGstin
		if (qrSearchParams.getVendorGstin() != null
				&& !qrSearchParams.getVendorGstin().isEmpty()) {
			if (qrSearchParams.getVendorGstin().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("sellerGstin"),
						qrSearchParams.getVendorGstin().get(0)));
			} else {
				predicates.add(root.get("sellerGstin")
						.in(qrSearchParams.getVendorGstin()));
			}
		}

		// validated Date
		if (qrSearchParams.getValidatedFrom() != null) {

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(
					root.get("validatedDate"),
					qrSearchParams.getValidatedFrom()));
		}

		// validated To
		if (qrSearchParams.getValidatedTo() != null) {
			predicates.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("validatedDate"),
							qrSearchParams.getValidatedTo()));
		}

		// additionalParams
		if (qrSearchParams.getRecordStatus() != null
				&& !qrSearchParams.getRecordStatus().isEmpty()) {

			LOGGER.debug("Inside RecordStatus");
			predicates
					.add(criteriaBuilder.or(
							criteriaBuilder.equal(root.get("isFullMatch"),
									qrSearchParams.getRecordStatus().contains(
											"Full Match") ? true : null),
							criteriaBuilder.equal(root.get("isProcessedQR"),
									qrSearchParams.getRecordStatus().contains(
											"Processed with QR") ? true : null),
							criteriaBuilder.equal(root.get("isSigMisMat"),
									qrSearchParams.getRecordStatus()
											.contains("Signature Mismatch")
													? true : null)));
		}

		return predicates;
	}
	
	public List<Predicate> buildAsycnExecJobSearchPredicatesqrPdf(
			QRSearchParams qrSearchParams, Root<QRPDFResponseSummaryEntity> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (qrSearchParams == null) {
			LOGGER.debug("QR Params are Blank");
			return predicates;
		}

		// entityId
		if (qrSearchParams.getEntityId() != null && !qrSearchParams.getEntityId().isEmpty()) {
			Predicate entityIdPredicate = criteriaBuilder.equal(root.get("entityId"), 
					qrSearchParams.getEntityId());
			Predicate entityIdNullPredicate = criteriaBuilder.isNull(root.get("entityId"));
			predicates.add(criteriaBuilder.or(entityIdPredicate, entityIdNullPredicate));
		}

		// buyerGstin
		if (qrSearchParams.getRecipientGstins() != null
				&& !qrSearchParams.getRecipientGstins().isEmpty()) {

			if (qrSearchParams.getRecipientGstins().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("buyerGstinQR"),
						qrSearchParams.getRecipientGstins().get(0)));
			} else {
				predicates.add(root.get("buyerGstinQR")
						.in(qrSearchParams.getRecipientGstins()));
			}
		}
		// SellerGstin
		if (qrSearchParams.getVendorGstin() != null
				&& !qrSearchParams.getVendorGstin().isEmpty()) {
			if (qrSearchParams.getVendorGstin().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("sellerGstinQR"),
						qrSearchParams.getVendorGstin().get(0)));
			} else {
				predicates.add(root.get("sellerGstinQR")
						.in(qrSearchParams.getVendorGstin()));
			}
		}

		// validated Date
		if (qrSearchParams.getValidatedFrom() != null) {

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(
					root.get("validatedDate"),
					qrSearchParams.getValidatedFrom()));
		}

		// validated To
		if (qrSearchParams.getValidatedTo() != null) {
			predicates.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("validatedDate"),
							qrSearchParams.getValidatedTo()));
		}

		// additionalParams
		if (qrSearchParams.getRecordStatus() != null
				&& !qrSearchParams.getRecordStatus().isEmpty()) {

			LOGGER.debug("Inside RecordStatus");
			predicates
					.add(criteriaBuilder.or(
							criteriaBuilder.equal(root.get("isFullMatch"),
									qrSearchParams.getRecordStatus().contains(
											"Full Match") ? true : null),
							criteriaBuilder.equal(root.get("isProcessedQR"),
									qrSearchParams.getRecordStatus().contains(
											"Processed with QR") ? true : null),
							criteriaBuilder.equal(root.get("isSigMisMat"),
									qrSearchParams.getRecordStatus()
											.contains("Signature Mismatch")
													? true : null)));
		}

		return predicates;
	}
	
	public List<Predicate> buildAsycnExecJobSearchPredicatesqrPdfJson(
			QRSearchParams qrSearchParams, Root<QRPDFJSONResponseSummaryEntity> root,
			CriteriaBuilder criteriaBuilder) {

		List<Predicate> predicates = new LinkedList<>();

		if (qrSearchParams == null) {
			LOGGER.debug("QR Params are Blank");
			return predicates;
		}

		// entityId
		if (qrSearchParams.getEntityId() != null && !qrSearchParams.getEntityId().isEmpty()) {
			Predicate entityIdPredicate = criteriaBuilder.equal(root.get("entityId"), 
					qrSearchParams.getEntityId());
			Predicate entityIdNullPredicate = criteriaBuilder.isNull(root.get("entityId"));
			predicates.add(criteriaBuilder.or(entityIdPredicate, entityIdNullPredicate));
		}

		// buyerGstin
		if (qrSearchParams.getRecipientGstins() != null
				&& !qrSearchParams.getRecipientGstins().isEmpty()) {

			if (qrSearchParams.getRecipientGstins().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("buyerGstinJson"),
						qrSearchParams.getRecipientGstins().get(0)));
			} else {
				predicates.add(root.get("buyerGstinJson")
						.in(qrSearchParams.getRecipientGstins()));
			}
		}
		// SellerGstin
		if (qrSearchParams.getVendorGstin() != null
				&& !qrSearchParams.getVendorGstin().isEmpty()) {
			if (qrSearchParams.getVendorGstin().size() == 1) {
				predicates.add(criteriaBuilder.equal(root.get("sellerGstinJson"),
						qrSearchParams.getVendorGstin().get(0)));
			} else {
				predicates.add(root.get("sellerGstinJson")
						.in(qrSearchParams.getVendorGstin()));
			}
		}

		// validated Date
		if (qrSearchParams.getValidatedFrom() != null) {

			predicates.add(criteriaBuilder.greaterThanOrEqualTo(
					root.get("validatedDate"),
					qrSearchParams.getValidatedFrom()));
		}

		// validated To
		if (qrSearchParams.getValidatedTo() != null) {
			predicates.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("validatedDate"),
							qrSearchParams.getValidatedTo()));
		}

		// additionalParams
		if (qrSearchParams.getRecordStatus() != null
				&& !qrSearchParams.getRecordStatus().isEmpty()) {

			LOGGER.debug("Inside RecordStatus");
			predicates
					.add(criteriaBuilder.or(
							criteriaBuilder.equal(root.get("isFullMatch"),
									qrSearchParams.getRecordStatus().contains(
											"Full Match") ? true : null),
							criteriaBuilder.equal(root.get("isProcessedQR"),
									qrSearchParams.getRecordStatus().contains(
											"Processed with QR") ? true : null),
							criteriaBuilder.equal(root.get("isSigMisMat"),
									qrSearchParams.getRecordStatus()
											.contains("Signature Mismatch")
													? true : null)));
		}

		return predicates;
	}


}
