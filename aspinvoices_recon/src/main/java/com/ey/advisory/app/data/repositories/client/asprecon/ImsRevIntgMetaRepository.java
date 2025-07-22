package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.ims.handlers.ImsRevIntgMetaEntity;

/**
 * @author Ravindra V S
 *
 */
@Repository("ImsRevIntgMetaRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface ImsRevIntgMetaRepository
		extends JpaRepository<ImsRevIntgMetaEntity, Long>,
		JpaSpecificationExecutor<ImsRevIntgMetaEntity> {

	@Modifying
	@Query("UPDATE ImsRevIntgMetaEntity e SET e.status =:status,"
			+ " e.updatedDate = CURRENT_TIMESTAMP,e.totalChunkRecordReceived =:totalChunkRecordReceived "
			+ " WHERE e.batchId =:batchId and e.gstin =:gstin "
			+ " and e.headerChunkId =:headerChunkId")
	public int updateErpPushStatus(@Param("status") String status,
			@Param("totalChunkRecordReceived") Long totalChunkRecordReceived,
			@Param("batchId") Long batchId, @Param("gstin") String gstin, 
			@Param("headerChunkId") Integer headerChunkId);

}
