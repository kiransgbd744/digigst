/*package com.ey.advisory.app.data.repositories.client;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.InwardTable3H3IGstnDetailsEntity;

*//**
 * 
 * @author Mahesh.Golla
 *
 *//*

@Service("InwardTable3H3IGstnRepository")
public interface InwardTable3H3IGstnRepository
		extends JpaRepository<InwardTable3H3IGstnDetailsEntity, Long>,
		JpaSpecificationExecutor<InwardTable3H3IGstnDetailsEntity> {

	*//**
	 * @param table3h3Ikey
	 *//*
	
	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3H3IGstnDetailsEntity b SET b.isDelete = TRUE "
			+ "WHERE b.table3h3iGstnKey IN (:table3h3iKey) ")
	public void UpdateSameKey(@Param("table3h3iKey") String table3h3iKey);

	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3H3IGstnDetailsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.custGstin = :cgstin "
			+ "AND doc.gstnBatchId = 0 AND doc.isDelete = false")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("cgstin") String cgstin);
	
	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3H3IGstnDetailsEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3H3IGstnDetailsEntity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	
}
*/