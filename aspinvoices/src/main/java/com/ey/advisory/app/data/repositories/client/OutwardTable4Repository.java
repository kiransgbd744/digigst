package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardTable4Entity;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("OutwardTable4Repository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardTable4Repository
		extends JpaRepository<OutwardTable4Entity, Long>, 
		                         JpaSpecificationExecutor<OutwardTable4Entity> {

	/**
	 * @param Table 4
	 */
	
	@Modifying
	@Query("UPDATE OutwardTable4Entity b SET b.isDelete = TRUE "
			+ "WHERE b.table4Invkey IN (:table4Key) ")
	public void UpdateSameInvKey(@Param("table4Key") String table4Key);
	
	
	@Modifying
	@Query("UPDATE OutwardTable4Entity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.retPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :vMaxId")
	public void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("vMaxId") Long vMaxId);
	
	@Modifying
	@Query("UPDATE OutwardTable4Entity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	public void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE OutwardTable4Entity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	public void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

}
