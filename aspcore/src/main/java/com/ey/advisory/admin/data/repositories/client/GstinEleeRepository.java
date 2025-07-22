
package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;

@Component("GstinEleeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinEleeRepository
		extends JpaRepository<EntityInfoEntity, Long>,
		JpaSpecificationExecutor<EntityInfoEntity> {
	@Modifying
	@Query("UPDATE EntityInfoEntity SET isDelete = true WHERE entityName in (:entityNames)")
	public void updateAll(@Param("entityNames") List<String> entityNames);


}

