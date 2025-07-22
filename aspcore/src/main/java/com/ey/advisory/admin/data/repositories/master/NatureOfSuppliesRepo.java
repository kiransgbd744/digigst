package com.ey.advisory.admin.data.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.NatureOfSupEntity;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("NatureOfSuppliesRepo")
public interface NatureOfSuppliesRepo
		extends JpaRepository<NatureOfSupEntity, Long>,
		                        JpaSpecificationExecutor<NatureOfSupEntity> {
	
}
