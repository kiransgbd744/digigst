package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr3BPayloadEntity;

/**
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr3BPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BPayloadRepository
		extends CrudRepository<Gstr3BPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr3BPayloadEntity gstr3b SET gstr3b.status=:status,"
			+ "gstr3b.modifiedOn = :modifiedOn,gstr3b.errorCode = :errorCode,"
			+ "gstr3b.jsonErrorResponse = :jsonErrorResp "
			+ "WHERE gstr3b.controlPayloadID =:cntrlPayloadId ")
	public void updateErrorStatus(
			@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp);

	@Modifying
	@Query("UPDATE Gstr3BPayloadEntity gstr3b SET gstr3b.status=:status,"
			+ "gstr3b.modifiedOn = :modifiedOn "
			+ "WHERE gstr3b.controlPayloadID =:cntrlPayloadId ")
	public void updateStatus(@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);;

}
