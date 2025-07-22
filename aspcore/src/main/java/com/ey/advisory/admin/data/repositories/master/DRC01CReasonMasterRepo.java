package com.ey.advisory.admin.data.repositories.master;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.DRC01CReasonMasterEntity;


/**
 *
 *         Repository for DRCReasonMasterEntity class
 */
@Repository("DRC01CReasonMasterRepo")
public interface DRC01CReasonMasterRepo
		extends JpaRepository<DRC01CReasonMasterEntity, Long>,
		JpaSpecificationExecutor<DRC01CReasonMasterEntity> {

	@Query("SELECT g FROM DRC01CReasonMasterEntity g")
	List<DRC01CReasonMasterEntity> findAll();
}
