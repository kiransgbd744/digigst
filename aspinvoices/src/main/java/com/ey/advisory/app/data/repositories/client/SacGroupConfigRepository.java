package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.sac.SacGroupConfigEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("SacGroupConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = 
Propagation.REQUIRED)
public interface SacGroupConfigRepository
		extends JpaRepository<SacGroupConfigEntity, Long>,
		JpaSpecificationExecutor<SacGroupConfigEntity> {

	@Query(" from SacGroupConfigEntity WHERE "
			+ " configValue =:configValue AND groupCode =:groupCode AND "
			+ " entityId =:entityId")
	public SacGroupConfigEntity findUrlByConfigValueAndGroupCode(
			@Param("configValue") String configValue,
			@Param("groupCode") String groupCode,
			@Param("entityId") Integer entityId);
	
	@Query("Select entity from SacGroupConfigEntity entity WHERE"
			+ " isActive=TRUE ORDER BY ID ASC")
	public List<SacGroupConfigEntity> findAllActiveProc();

}
