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

import com.ey.advisory.app.data.entities.client.InvDocSeriesPayloadEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("InvDocSeriesPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InvDocSeriesPayloadRepository
		extends CrudRepository<InvDocSeriesPayloadEntity, Long> {

	@Modifying
	@Query("UPDATE InvDocSeriesPayloadEntity inv SET inv.status=:status,"
			+ "inv.modifiedOn = :modifiedOn,inv.errorCode = :errorCode,"
			+ "inv.jsonErrorResponse = :jsonErrorResp "
			+ "WHERE inv.controlPayloadID =:cntrlPayloadId ")
	public void updateErrorStatus(
			@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp);

	@Modifying
	@Query("UPDATE InvDocSeriesPayloadEntity inv SET inv.status=:status,"
			+ "inv.modifiedOn = :modifiedOn "
			+ "WHERE inv.controlPayloadID =:cntrlPayloadId ")
	public void updateStatus(@Param("cntrlPayloadId") String cntrlPayloadId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn);;

}
