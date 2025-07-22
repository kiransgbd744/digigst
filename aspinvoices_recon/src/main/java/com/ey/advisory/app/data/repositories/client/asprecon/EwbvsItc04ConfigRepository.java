package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04ConfigEntity;

/**
 * @author Ravindra V S
 *
 */

@Repository("EwbvsItc04ConfigRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface EwbvsItc04ConfigRepository
		extends JpaRepository<EwbVsItc04ConfigEntity, Long>,
		JpaSpecificationExecutor<EwbVsItc04ConfigEntity> {

	@Modifying
	@Query("UPDATE EwbVsItc04ConfigEntity SET status =:status,"
			+ " completedOn =:completedOn"
			+ " where configId =:configId")
	public void updateReconConfigStatusAndReportName(
			@Param("status") String status,
			@Param("completedOn") LocalDateTime completedOn,
			@Param("configId") Long configId);

	public List<EwbVsItc04ConfigEntity> findByConfigId(Long configId);

}
