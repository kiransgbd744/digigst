package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;


@Repository("GstinValidatorConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = 
			Propagation.REQUIRED)
public interface GstinValidatorConfigRepository extends 
  JpaRepository<GstinValidatorEntity, Long>,
   JpaSpecificationExecutor<GstinValidatorEntity> {
	
	@Query("Select configEntity from GstinValidatorEntity configEntity where"
			+ " requestId =:requestId")
	public GstinValidatorEntity findByRequestId
				(@Param("requestId") Long requestId);
	
	@Query("Select configEntity from GstinValidatorEntity configEntity where "
			+ "  einvApplicable = :einvApplicable order by dateOfUpload desc")
	public List<GstinValidatorEntity> findByEinvApplicable(
			    @Param("einvApplicable") boolean einvApplicable);
	
	@Modifying
	@Query("UPDATE GstinValidatorEntity SET status =:status"
			+ " where requestId =:requestId")
	public void updateGstinValidatorStatus(@Param("status") String status ,
			@Param("requestId") Long requestId);
	
	@Modifying
	@Query("UPDATE GstinValidatorEntity SET status =:status,"
			+ "filePath =:filePath,completedOn =:completedOn,errorMsg =:errorMsg,docId =:docId"
			+ " WHERE requestId =:requestId")
	public void updateGstinValidatorDetails(@Param("status") String status
		, @Param("filePath") String filePath,@Param("completedOn") 
	     LocalDateTime completedOn, @Param("requestId") Long requestId,
	     @Param("errorMsg") String errorMsg, @Param("docId") String docId);


}
