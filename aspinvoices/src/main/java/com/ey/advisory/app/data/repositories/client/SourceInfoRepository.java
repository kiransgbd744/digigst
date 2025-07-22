/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.SourceInfoEntity;

/**
 * @author Siva
 *
 */
@Repository("SourceInfoRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SourceInfoRepository
		extends JpaRepository<SourceInfoEntity, Long>,
		JpaSpecificationExecutor<SourceInfoEntity> {

	public SourceInfoEntity findByGstinAndIsDeleteFalse(String gstin);

	public SourceInfoEntity findByEntityIdAndIsDeleteFalse(Long entityId);

	public SourceInfoEntity findByEntityIdAndGstinAndIsDeleteFalse(
			Long entityId, String gstin);

	public SourceInfoEntity findByEntityIdAndPlantAndGstinAndIsDeleteFalse(
			Long entityId, String plant, String gstin);

	@Query("SELECT e FROM SourceInfoEntity e WHERE e.entityId=:entityId "
			+ "AND e.isDelete=false")
	public List<SourceInfoEntity> findGstnBasedOnEntityId(
			final @Param("entityId") Long entityId);
	
	public SourceInfoEntity findByEntityIdAndGstinAndPlantIsNullAndIsDeleteFalse(
			Long entityId, String gstin);
	
	public SourceInfoEntity findByEntityIdAndGstinIsNullAndPlantIsNullAndIsDeleteFalse(Long entityId);
}