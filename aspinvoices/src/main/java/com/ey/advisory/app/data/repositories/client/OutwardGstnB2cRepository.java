/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardGstnB2cEntity;
*//**
 * 
 * @author Mahesh.Golla
 *
 *//*
@Service("OutwardGstnB2cRepository")
public interface OutwardGstnB2cRepository
		extends JpaRepository<OutwardGstnB2cEntity, Long>,
		JpaSpecificationExecutor<OutwardGstnB2cEntity> {
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardGstnB2cEntity b SET b.isDelete = TRUE "
			+ "WHERE b.b2cGstnKey IN (:b2cGstnKey) ")

	public void UpdateSameGstnKey(@Param("b2cGstnKey") String b2cGstnKey);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardGstnB2cEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.retPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.gstnBatchId = 0 AND doc.isDelete = false")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardGstnB2cEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Transactional
	@Modifying
	@Query("UPDATE OutwardGstnB2cEntity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

}
*/