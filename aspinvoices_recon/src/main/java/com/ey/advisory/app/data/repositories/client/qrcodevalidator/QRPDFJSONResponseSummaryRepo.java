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

import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFJSONResponseSummaryEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRPDFResponseSummaryEntity;
import com.ey.advisory.app.data.services.qrcodevalidator.QRSearchParams;
import com.ey.advisory.app.data.services.qrcodevalidator.QRValidatorPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRPDFJSONResponseSummaryRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRPDFJSONResponseSummaryRepo
		extends JpaRepository<QRPDFJSONResponseSummaryEntity, Long>,
		JpaSpecificationExecutor<QRPDFJSONResponseSummaryEntity> {
	
	@Query("select DISTINCT r.buyerGstinJson from QRPDFJSONResponseSummaryEntity r")
	List<String> findAllBuyerGstins();

	@Query("select DISTINCT r.sellerPanJson from QRPDFJSONResponseSummaryEntity r")
	List<String> findAllSellerPans();

	@Query("SELECT DISTINCT r.sellerGstinJson FROM  QRPDFJSONResponseSummaryEntity r"
			+ " WHERE r.sellerPanJson IN (:sellerPanJson)")
	List<String> findAllVendorGstinByVendorPans(
			@Param("sellerPanJson") List<String> sellerPanQR);
	
	public List<QRPDFJSONResponseSummaryEntity> findByFileId(Long fileId);
	
	public List<QRPDFJSONResponseSummaryEntity> findByFileId(Long fileId,
			Pageable pageReq);

	@Query("SELECT doc FROM QRPDFJSONResponseSummaryEntity doc "
			+ "WHERE doc.fileId = :fileId ")
	public Optional<QRPDFJSONResponseSummaryEntity> retrieveById(
			@Param("fileId") Long fileId);
	
	public default List<QRPDFJSONResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicatesqrPdfJson(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		});
	}

	
	public default Page<QRPDFJSONResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicatesqrPdfJson(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}

	@Modifying
	@Query("UPDATE QRPDFJSONResponseSummaryEntity log SET log.entityId = :entityId "
			+ "WHERE  log.fileId = :fileId ")
	void updateEntityId(@Param("entityId") long entityId,
			@Param("fileId") Long fileId);
	
	public List<QRPDFJSONResponseSummaryEntity> findByFileIdIn(List<Long> fileIds);

}
