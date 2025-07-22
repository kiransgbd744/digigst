package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.AutoRecon2AERPMetaEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("AutoRecon2AERPMetaRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface AutoRecon2AERPMetaRepository
		extends JpaRepository<AutoRecon2AERPMetaEntity, Long>,
		JpaSpecificationExecutor<AutoRecon2AERPMetaEntity> {

	@Modifying
	@Query("UPDATE AutoRecon2AERPMetaEntity e SET e.status =:status,"
			+ " e.updatedDate = CURRENT_TIMESTAMP,e.totalChunkRecordReceived =:totalChunkRecordReceived "
			+ " WHERE e.reconReportConfigId =:reconReportConfigId and e.gstin =:gstin and e.chunkId =:chunkId")
	public int updateErpPushStatus(@Param("status") String status,
			@Param("totalChunkRecordReceived") Long totalChunkRecordReceived,
			@Param("reconReportConfigId") Long reconReportConfigId,
			@Param("gstin") String gstin, @Param("chunkId") Integer chunkId);

}
