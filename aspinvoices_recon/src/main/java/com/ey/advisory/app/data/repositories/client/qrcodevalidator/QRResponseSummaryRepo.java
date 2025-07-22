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

import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseSummaryEntity;
import com.ey.advisory.app.data.services.qrcodevalidator.QRSearchParams;
import com.ey.advisory.app.data.services.qrcodevalidator.QRValidatorPredicateBuilder;
import com.ey.advisory.common.StaticContextHolder;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("QRResponseSummaryRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRResponseSummaryRepo
		extends JpaRepository<QRResponseSummaryEntity, Long>,
		JpaSpecificationExecutor<QRResponseSummaryEntity> {

	public List<QRResponseSummaryEntity> findByFileId(Long fileId);

	@Query("SELECT doc FROM QRResponseSummaryEntity doc "
			+ "WHERE doc.fileId = :fileId ")
	public Optional<QRResponseSummaryEntity> retrieveById(
			@Param("fileId") Long fileId);

	public List<QRResponseSummaryEntity> findByFileId(Long fileId,
			Pageable pageReq);

	@Modifying
	@Query("UPDATE QRUploadFileStatusEntity log SET log.fileStatus = :fileStatus "
			+ "WHERE  log.id = :id ")
	void updateFileStatus(@Param("fileStatus") String fileStatus,
			@Param("id") Long id);

	public default Page<QRResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}

	public default List<QRResponseSummaryEntity> findConsolidatedDataByParams(
			QRSearchParams qrParams) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			QRValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(QRValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		});
	}

	@Query("select DISTINCT r.buyerGstin from QRResponseSummaryEntity r")
	List<String> findAllBuyerGstins();

	@Query("select DISTINCT r.sellerPan from QRResponseSummaryEntity r")
	List<String> findAllSellerPans();

	@Query("SELECT DISTINCT r.sellerGstin FROM  QRResponseSummaryEntity r"
			+ " WHERE r.sellerPan IN (:sellerPan)")
	List<String> findAllVendorGstinByVendorPans(
			@Param("sellerPan") List<String> sellerPan);

	public List<QRResponseSummaryEntity> findByFileIdIn(List<Long> fileIds);
	
	@Modifying
	@Query("UPDATE QRResponseSummaryEntity log SET log.entityId = :entityId "
			+ "WHERE  log.fileId = :fileId ")
	void updateEntityId(@Param("entityId") long entityId,
			@Param("fileId") Long fileId);
}
