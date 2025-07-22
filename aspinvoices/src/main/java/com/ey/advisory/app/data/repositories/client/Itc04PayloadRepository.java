/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04PayloadEntity;

/**
 * @author Mahesh.Golla
 *
 */
@Repository("Itc04PayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04PayloadRepository
		extends CrudRepository<Itc04PayloadEntity, Long> {

	@Modifying
	@Query("UPDATE Itc04PayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn,op.errorCode = :errorCode,"
			+ "op.jsonErrorResponse = :jsonErrorResp ,"
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount "
			+ "WHERE op.payloadId =:payloadId AND op.sourceId = :sourceId")
	public void updateCpiStatus(@Param("payloadId") String payloadId,
			@Param("sourceId") String sourceId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCode") String errorCode,
			@Param("jsonErrorResp") String jsonErrorResp,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);
	
	@Query("SELECT op.payloadId FROM Itc04PayloadEntity op "
			+ "WHERE op.payloadId =:payloadId AND op.sourceId = :sourceId")
	List<Object> findBypayloadIdAndSourceId(
			@Param("payloadId") String payloadId,
			@Param("sourceId") String sourceId);
	
	@Modifying
	@Query("UPDATE Itc04PayloadEntity op SET op.status=:status,"
			+ "op.modifiedOn = :modifiedOn, "
			+ "op.errorCount = :errorCount,op.totalCount = :totalCount "
			+ "WHERE op.payloadId =:payloadId AND op.sourceId = :sourceId")
	public void updateStatus(@Param("payloadId") String payloadId,
			@Param("sourceId") String sourceId,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,
			@Param("errorCount") Integer errorCount,
			@Param("totalCount") Integer totalCount);

}