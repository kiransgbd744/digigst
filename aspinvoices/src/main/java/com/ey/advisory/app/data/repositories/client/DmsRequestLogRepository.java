/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.dms.DmsRequestLog;

/**
 * @author vishal.verma
 *
 */

@Repository("DmsRequestLogRepository")
public interface DmsRequestLogRepository
		extends JpaRepository<DmsRequestLog, Long> {

	Optional<DmsRequestLog> findByUuid(String uuid);
	
	@Modifying
	@Query("UPDATE DmsRequestLog log SET "
			+ " log.callbackResponse =:callbackResponse, "
			+ " log.updatedOn = CURRENT_TIMESTAMP where log.uuid = :uuid ")
	public void updateCallBackResponse(@Param("uuid") String uuid,
			@Param("callbackResponse") String callbackResponse);

}
