package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.MakerCheckerWorkflowEntityMappingEntity;

@Repository(value = "MakerCheckerWorkflowEntityMappingRepository")
@Transactional
public interface MakerCheckerWorkflowEntityMappingRepository
		extends CrudRepository<MakerCheckerWorkflowEntityMappingEntity, Long> {

	public MakerCheckerWorkflowEntityMappingEntity findByEntityIdAndWorkflowTypeAndIsDeleteFalse(
			@Param("entityId") Long entityId,
			@Param("workflowType") String workflowType);

	@Modifying
	@Query("UPDATE MakerCheckerWorkflowEntityMappingEntity set action=:action WHERE "
			+ "id=:id and isDelete=false")
	public void updateCheckWorkFlowMaster(@Param("id") Long id,
			@Param("action") boolean action);
	
	
	public List<MakerCheckerWorkflowEntityMappingEntity> findByEntityIdAndIsDeleteFalse(
			@Param("entityId") Long entityId);
}
