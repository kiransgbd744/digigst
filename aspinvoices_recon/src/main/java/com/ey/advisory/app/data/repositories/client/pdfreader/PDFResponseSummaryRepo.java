package com.ey.advisory.app.data.repositories.client.pdfreader;

import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.pdfreader.PDFResponseSummaryEntity;
import com.ey.advisory.app.data.services.qrcodevalidator.QRSearchParams;
import com.ey.advisory.app.data.services.qrcodevalidator.QRValidatorPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("PDFResponseSummaryRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface PDFResponseSummaryRepo
		extends JpaRepository<PDFResponseSummaryEntity, Long>,
		JpaSpecificationExecutor<PDFResponseSummaryEntity> {
	
	public List<PDFResponseSummaryEntity> findByFileId(Long fileId);
	
	public List<PDFResponseSummaryEntity> findByFileId(Long fileId,
			Pageable pageReq);
	/*
	 
	@Query("select DISTINCT r.buyerGstinQR from PDFResponseSummaryEntity r")
	List<String> findAllBuyerGstins();

	@Query("select DISTINCT r.sellerPanQR from PDFResponseSummaryEntity r")
	List<String> findAllSellerPans();

	@Query("SELECT DISTINCT r.sellerGstinQR FROM  PDFResponseSummaryEntity r"
			+ " WHERE r.sellerPanQR IN (:sellerPanQR)")
	List<String> findAllVendorGstinByVendorPans(
			@Param("sellerPanQR") List<String> sellerPanQR);
	 */


    /*	
	public default List<PDFResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicatesqrPdf(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		});
	}
	*/
	

	/*
	public default Page<PDFResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicatesqrPdf(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}
	*/


}
