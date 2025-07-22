package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.AutoReconRequestEntity;

@Repository("AutoReconRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface AutoReconRequestRepository
		extends JpaRepository<AutoReconRequestEntity, Long>,
		JpaSpecificationExecutor<AutoReconRequestEntity> {

	List<AutoReconRequestEntity> findByEntityIdOrderByRequestIdDesc(
			Long entityId, Pageable pageReq);

	@Query("select a.filePath from AutoReconRequestEntity a "
			+ "  where a.requestId = :requestId")
	public String getFilePath(@Param("requestId") Long requestId);

	@Query("select count(*) from AutoReconRequestEntity a "
			+ " where a.entityId = :entityId")
	int getCountOfCreatedBy(@Param("entityId") Long entityId);
	
	@Modifying
	@Query("UPDATE AutoReconRequestEntity SET status =:status, "
			+ " updatedOn =:updatedOn,filePath = :filePath  where "
			+ "  requestId =:requestId")
	public void updateStatus(
			@Param("status") String status, 
			@Param("updatedOn") LocalDateTime updatedOn, 
			@Param("requestId") Long requestId,
			@Param("filePath") String filePath);
	
	@Modifying
	@Query("UPDATE AutoReconRequestEntity SET status = :status, "
	        + "updatedOn = :updatedOn, filePath = :filePath, docId = :docId "
	        + "WHERE requestId = :requestId")
	public void updateStatus(
	        @Param("status") String status, 
	        @Param("updatedOn") LocalDateTime updatedOn, 
	        @Param("requestId") Long requestId,
	        @Param("filePath") String filePath,
	        @Param("docId") String docId);
	
	@Query("select count(*) from AutoReconRequestEntity a "
			+ " where a.status IN (:status)")
	int getCountOfStatus(@Param("status") List<String> status);
	

	@Query("SELECT a FROM AutoReconRequestEntity a WHERE a.status IN (:status)")
	List<AutoReconRequestEntity> findByStatusIn(@Param("status") List<String> status);

}
