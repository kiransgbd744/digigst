package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.GetGstr2aErpConsolidatedEntity;

import jakarta.transaction.Transactional;

@Repository("GetGstr2aErpConsolidatedRepository")
public interface GetGstr2aErpConsolidatedRepository
		extends JpaRepository<GetGstr2aErpConsolidatedEntity, Long>,
		JpaSpecificationExecutor<GetGstr2aErpConsolidatedEntity> {

	@Modifying
	@Transactional
	@Query("UPDATE GetGstr2aErpConsolidatedEntity SET "
			+ "erpBatchId=:erpBatchId WHERE cgstin=:cgstin ")
	public void updateErpBatchId(@Param("erpBatchId") Long erpBatchId,
			@Param("cgstin") String cgstin);
	
	@Modifying
	@Transactional
	@Query("UPDATE GetGstr2aErpConsolidatedEntity SET "
			+ "isSentToErp=true,sentToErpDate=:sentToErpDate WHERE cgstin=:cgstin "
			+ " and chunkId = :chunkId ")
	public void updateSentErp(@Param("sentToErpDate") LocalDate date,
			@Param("cgstin") String cgstin,
			@Param("chunkId") Integer chunkId);

	@Query("SELECT distinct chunkId from  GetGstr2aErpConsolidatedEntity WHERE "
			+ " cgstin=:cgstin and getBatchId = :batchId ")
	public List<Integer> getDistinctChunkIds(@Param("cgstin") String cgstin,
			@Param("batchId") Long batchId);

}
