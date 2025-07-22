package com.ey.advisory.common.client.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.client.domain.ErpReqAggSummaryEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("ErpReqAggSummaryRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpReqAggSummaryRepo
		extends JpaRepository<ErpReqAggSummaryEntity, Long>,
		JpaSpecificationExecutor<ErpReqAggSummaryEntity> {
	@Query("SELECT doc FROM ErpReqAggSummaryEntity doc "
			+ "WHERE doc.summaryDate >= :summaryDate AND doc.isActive = true ")
	public List<ErpReqAggSummaryEntity> findActiveRecordsOnSummaryDate(
			@Param("summaryDate") LocalDate summaryDate);
	
	@Modifying
	@Query("UPDATE ErpReqAggSummaryEntity log SET log.isPushedtoCloud = :isPushedtoCloud "
			+ "WHERE log.summaryDate = :summaryDate")
	int updatePushedCloudStatus(
			@Param("isPushedtoCloud") boolean isPushedtoCloud,
			@Param("summaryDate") LocalDate summaryDate);

	@Modifying
	@Query("UPDATE ErpReqAggSummaryEntity log SET log.isActive = :isActive "
			+ "WHERE log.summaryDate = :summaryDate and log.region = :region ")
	int updateisActiveStatus(@Param("isActive") boolean isActive,
			@Param("summaryDate") LocalDate summaryDate,@Param("region") String region);
	
	@Query("SELECT doc FROM ErpReqAggSummaryEntity doc "
	        + "WHERE doc.summaryDate BETWEEN :summaryDate AND :summaryDateTo "
	        + "AND doc.isActive = true")
	public List<ErpReqAggSummaryEntity> findActiveRecordsBetweenSummaryDates(
	        @Param("summaryDate") LocalDate summaryDate,
	        @Param("summaryDateTo") LocalDate summaryDateTo);
	
	@Modifying
	@Query("UPDATE ErpReqAggSummaryEntity log SET log.isPushedtoCloud = :isPushedtoCloud "
	        + "WHERE log.summaryDate BETWEEN :summaryDate AND :summaryDateTo")
	int updatePushedCloudStatuses(
	        @Param("isPushedtoCloud") boolean isPushedtoCloud,
	        @Param("summaryDate") LocalDate summaryDate,
	        @Param("summaryDateTo") LocalDate summaryDateTo);
	
	@Modifying
	@Query("UPDATE ErpReqAggSummaryEntity log SET log.isActive = :isActive "
	        + "WHERE log.region = :region and log.summaryDate BETWEEN :summaryDate AND :summaryDateTo")
	int updateIsActiveStatuses(
	        @Param("isActive") boolean isActive,
	        @Param("summaryDate") LocalDate summaryDate,
	        @Param("summaryDateTo") LocalDate summaryDateTo,
	        @Param("region") String region);

}