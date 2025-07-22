package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ReturnFilingConfigEntity;

@Repository("ReturnFilingConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface ReturnFilingConfigRepository extends
	JpaRepository<ReturnFilingConfigEntity,Long>,
	JpaSpecificationExecutor<ReturnFilingConfigEntity> {
	
	@Modifying
	@Query("UPDATE ReturnFilingConfigEntity SET status =:status"
			+ " where requestId =:requestId")
	public void updateReturnFilingStatus(@Param("status") String status ,
			@Param("requestId") Long requestId);
	
	@Modifying
	@Query("UPDATE ReturnFilingConfigEntity SET status =:status,"
			+ "filePath =:filePath,completedOn =:completedOn,errorMsg =:errorMsg,docId =:docId"
			+ " WHERE requestId =:requestId")
	public void updateReturnFilingDetails(@Param("status") String status
		, @Param("filePath") String filePath,@Param("completedOn") 
	     LocalDateTime completedOn,@Param("errorMsg") String errorMsg, 
	     @Param("requestId") Long requestId,@Param("docId") String docId);
	
	@Query("Select configEntity from ReturnFilingConfigEntity configEntity where"
			+ " requestId =:requestId")
	public ReturnFilingConfigEntity findByRequestId
				(@Param("requestId") Long requestId);
	
	
	
}
