package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.DRCReasonMasterEntity;

/**
 * 
 * @author Siva.Reddy
 *
 *         Repository for DRCReasonMasterEntity class
 */
@Repository("DRCReasonMasterRepository")
public interface DRCReasonMasterRepository
		extends JpaRepository<DRCReasonMasterEntity, Long>,
		JpaSpecificationExecutor<DRCReasonMasterEntity> {

	@Query("SELECT g FROM DRCReasonMasterEntity g")
	List<DRCReasonMasterEntity> findAll();
}
