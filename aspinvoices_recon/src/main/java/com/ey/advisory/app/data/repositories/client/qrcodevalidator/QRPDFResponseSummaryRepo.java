package com.ey.advisory.app.data.repositories.client.qrcodevalidator;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.services.qrcodevalidator.QRSearchParams;
import com.ey.advisory.app.data.services.qrcodevalidator.QRValidatorPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRPDFResponseSummaryRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRPDFResponseSummaryRepo
		extends JpaRepository<QRPDFResponseSummaryEntity, Long>,
		JpaSpecificationExecutor<QRPDFResponseSummaryEntity> {
	
	@Query("select DISTINCT r.buyerGstinQR from QRPDFResponseSummaryEntity r")
	List<String> findAllBuyerGstins();

	@Query("select DISTINCT r.sellerPanQR from QRPDFResponseSummaryEntity r")
	List<String> findAllSellerPans();

	@Query("SELECT DISTINCT r.sellerGstinQR FROM  QRPDFResponseSummaryEntity r"
			+ " WHERE r.sellerPanQR IN (:sellerPanQR)")
	List<String> findAllVendorGstinByVendorPans(
			@Param("sellerPanQR") List<String> sellerPanQR);
	
	public List<QRPDFResponseSummaryEntity> findByFileId(Long fileId);
	
	public List<QRPDFResponseSummaryEntity> findByFileId(Long fileId,
			Pageable pageReq);
	
	@Query("SELECT doc FROM QRPDFResponseSummaryEntity doc "
			+ "WHERE doc.fileId = :fileId ")
	public Optional<QRPDFResponseSummaryEntity> retrieveById(
			@Param("fileId") Long fileId);

	
	public default List<QRPDFResponseSummaryEntity> findConsolidatedDataByParams(
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

	
	public default Page<QRPDFResponseSummaryEntity> findConsolidatedDataByParams(
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


	@Modifying
	@Query("UPDATE QRPDFResponseSummaryEntity log SET log.entityId = :entityId "
			+ "WHERE  log.fileId = :fileId ")
	void updateEntityId(@Param("entityId") long entityId,
			@Param("fileId") Long fileId);
	
	public List<QRPDFResponseSummaryEntity> findByFileIdIn(List<Long> fileIds);
}
