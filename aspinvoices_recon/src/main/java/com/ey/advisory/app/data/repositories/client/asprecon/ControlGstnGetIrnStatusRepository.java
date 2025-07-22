package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.data.entities.client.asprecon.ControlGstnGetIrnStatusEntity;



/**
 * @author Saif.S
 *
 */
@Repository("ControlGstnGetIrnStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ControlGstnGetIrnStatusRepository
		extends CrudRepository<ControlGstnGetIrnStatusEntity, Long>,
		JpaSpecificationExecutor<ControlGstnGetIrnStatusEntity> {

	@Modifying
	@Query("UPDATE ControlGstnGetIrnStatusEntity b SET b.jobStatus =:jobStatus, "
			+ " b.updatedOn =:updatedOn WHERE b.id =:id ")
	void updateJobStatus(@Param("jobStatus") String jobStatus,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("id") Long id);

	@Modifying
	@Query("UPDATE ControlGstnGetIrnStatusEntity b SET b.jobStatus =:jobStatus, "
			+ " b.errorDesc =:errorDesc, b.updatedOn =:updatedOn  "
			+ " WHERE b.id =:id ")
	void updateJobStatusAndErrorDesc(@Param("jobStatus") String jobStatus,
			@Param("errorDesc") String errorDesc,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("id") Long id);

	List<ControlGstnGetIrnStatusEntity> findByMonitorId(Long monitorId);
}
