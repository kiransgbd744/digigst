package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardB2cEntity;

@Repository("OutwardB2cRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OutwardB2cRepository
		extends JpaRepository<OutwardB2cEntity, Long>,
		JpaSpecificationExecutor<OutwardB2cEntity> {

	@Modifying
	@Query("UPDATE OutwardB2cEntity b SET b.isDelete = TRUE "
			+ "WHERE b.b2cInvKey IN (:b2cInvKey) ")
	public void UpdateSameInvKey(@Param("b2cInvKey") String b2cInvKey);

	@Modifying
	@Query("UPDATE OutwardB2cEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.retPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :vMaxId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("vMaxId") Long vMaxId);

	@Modifying
	@Query("UPDATE OutwardB2cEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE OutwardB2cEntity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT COUNT(*) FROM OutwardB2cEntity "
			+ "WHERE sgstin=:gstin AND retPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);
}
