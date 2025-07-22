/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.NilNonExmptPayloadEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("NilNonExmptPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface NilNonExmptPayloadRepository
		extends CrudRepository<NilNonExmptPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE NilNonExmptPayloadEntity nil SET nil.status=:status,"
			+ "nil.modifiedOn = :modifiedOn,nil.errorCode = :errorCode,"
			+ "nil.jsonErrorResponse = :jsonErrorResp "
			+ "WHERE nil.controlPayloadID =:cntrlPayloadId ")
	public void updateErrorStatus(
			@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp);

	@Modifying
	@Query("UPDATE NilNonExmptPayloadEntity nil SET nil.status=:status,"
			+ "nil.modifiedOn = :modifiedOn "
			+ "WHERE nil.controlPayloadID =:cntrlPayloadId ")
	public void updateStatus(@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);;

}
