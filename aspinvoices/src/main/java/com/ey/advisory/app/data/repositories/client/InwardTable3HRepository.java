package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InwardTable3IDetailsEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("InwardTable3HRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InwardTable3HRepository
		extends JpaRepository<InwardTable3IDetailsEntity, Long>,
		JpaSpecificationExecutor<InwardTable3IDetailsEntity> {

	/**
	 * @param table3h3Ikey
	 */

	@Modifying
	@Query("UPDATE InwardTable3IDetailsEntity b SET b.isDelete = TRUE "
			+ "WHERE b.table3h3iInvKey IN (:table3h3Ikey) ")
	public void UpdateSameInvKey(@Param("table3h3Ikey") String table3h3Ikey);

	@Modifying
	@Query("UPDATE InwardTable3IDetailsEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.custGstin = :cgstin "
			+ "AND doc.isDelete = false AND doc.id <= :vMaxId")
	void updateSumryBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("cgstin") String cgstin, @Param("vMaxId") Long vMaxId);

	@Modifying
	@Query("UPDATE InwardTable3IDetailsEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE InwardTable3IDetailsEntity doc SET "
			+ "doc.isGstnError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT COUNT(*) FROM InwardTable3IDetailsEntity "
			+ "WHERE custGstin=:gstin AND returnPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);
}
