package com.ey.advisory.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.domain.client.ERPRequestLogEntity;

/**
 * @author Sai.Pakanati
 *
 */
@Repository("LoggerAdviceRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface LoggerAdviceRepository
		extends JpaRepository<ERPRequestLogEntity, Long>,
		JpaSpecificationExecutor<ERPRequestLogEntity> {

	@Modifying
	@Query("UPDATE ERPRequestLogEntity log SET log.batchId = :batchId "
			+ "WHERE  log.id IN (:ids) ")
	void updateBatchIds(@Param("batchId") String batchId,
			@Param("ids") List<Long> ids);

	@Modifying
	@Query("UPDATE ERPRequestLogEntity log SET log.batchId = null,log.isAutoDrafted = false "
			+ "WHERE  log.batchId = :batchId ")
	void resetAutoDraftBatch(@Param("batchId") String batchId);

	@Modifying
	@Query("UPDATE ERPRequestLogEntity log SET log.isAutoDrafted = :isAutoDrafted "
			+ "WHERE log.batchId = :batchId ")
	void updateIsAutoDraftedFlag(@Param("batchId") String batchId,
			@Param("isAutoDrafted") boolean isAutoDrafted);

	public List<ERPRequestLogEntity> findByApiTypeInAndIsDuplicateFalseAndNicStatusTrueAndBatchIdIsNullAndCompanyCodeAndSourceId(
			List<String> apiType, String companyCode, String sourceId,
			Pageable page);

	public Optional<ERPRequestLogEntity> findTop1ByApiTypeInAndDocNumAndDocTypeAndNicStatusFalseOrderByIdDesc(
			List<String> apiType, String docNum, String docType);

	public Optional<ERPRequestLogEntity> findTop1ByApiTypeInAndIrnNumAndNicStatusFalseOrderByIdDesc(
			List<String> apiType, String irnNum);

	public Optional<ERPRequestLogEntity> findTop1ByApiTypeInAndEwbNoAndNicStatusFalseOrderByIdDesc(
			List<String> apiType, String ewbNo);

	public List<ERPRequestLogEntity> findByApiTypeInAndIsDuplicateFalseAndNicRawRespTimestampIsNotNullAndBatchIdIsNullAndCompanyCodeAndSourceId(
			List<String> apiType, String companyCode, String sourceId,
			Pageable page);

	public List<ERPRequestLogEntity> findByBatchId(String batchId);

	public List<ERPRequestLogEntity> findByIrnNumInAndApiTypeAndIsDuplicateFalseAndNicStatusTrueAndBatchIdIsNotNullAndCompanyCodeAndSourceIdAndIsAutoDraftedTrue(
			List<String> irnNum, String apiType, String companyCode,
			String sourceId);
}
