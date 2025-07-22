package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2BManualApiStatus;

/**
 * @author Siva.Reddy
 *
 */

@Repository("GetGstr2BManualApiStatusRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetGstr2BManualApiStatusRepo
		extends CrudRepository<Gstr2BManualApiStatus, Long>,
		JpaSpecificationExecutor<Gstr2BManualApiStatus> {

	@Modifying
	@Query("UPDATE Gstr2BManualApiStatus b SET b.fileCntStatus = :fileCntStatus, "
			+ " b.updatedOn =:updatedOn  "
			+ " WHERE b.invocationId = :invocationId ")
	void softlyDeleteB2bHeader(@Param("fileCntStatus") String fileCntStatus,
			@Param("invocationId") Long invocationId,
			@Param("updatedOn") LocalDateTime modifiedOn);

	public Optional<Gstr2BManualApiStatus> findByInvocationId(Long invocationId);
	
}
