/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardTable4GstnEntity;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("OutwardGstnTable4Repository")
public interface OutwardGstnTable4Repository
		extends JpaRepository<OutwardTable4GstnEntity, Long>,
		JpaSpecificationExecutor<OutwardTable4GstnEntity> {
	
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardTable4GstnEntity b SET b.isDelete = TRUE "
			+ "WHERE b.table4Gstnkey IN (:table4Gstnkey) ")
	public void UpdateSameGstnKey(@Param("table4Gstnkey") String table4Gstnkey);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardTable4GstnEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.retPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.gstnBatchId = 0 AND doc.isDelete = false")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardTable4GstnEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardTable4GstnEntity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
	

}
*/