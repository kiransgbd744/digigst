package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ControlGstnGetStatusEntity;

/**
 * @author Saif.S
 *
 */
@Repository("ControlGstnGetStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ControlGstnGetStatusRepository
		extends CrudRepository<ControlGstnGetStatusEntity, Long>,
		JpaSpecificationExecutor<ControlGstnGetStatusEntity> {

	@Modifying
	@Query("UPDATE ControlGstnGetStatusEntity b SET b.jobStatus =:jobStatus, "
			+ " b.updatedOn =:updatedOn WHERE b.id =:id ")
	void updateJobStatus(@Param("jobStatus") String jobStatus,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("id") Long id);

	@Modifying
	@Query("UPDATE ControlGstnGetStatusEntity b SET b.jobStatus =:jobStatus, "
			+ " b.errorDesc =:errorDesc, b.updatedOn =:updatedOn  "
			+ " WHERE b.id =:id ")
	void updateJobStatusAndErrorDesc(@Param("jobStatus") String jobStatus,
			@Param("errorDesc") String errorDesc,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("id") Long id);

	List<ControlGstnGetStatusEntity> findByMonitorId(Long monitorId);
}
