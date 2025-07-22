package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.Anx1B2cProcEntity;

@Repository("Anx1B2cProcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1B2cProcRepository extends CrudRepository<Anx1B2cProcEntity, Long> {

	@Modifying
	@Query("UPDATE Anx1B2cProcEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true, doc.modifiedBy = 'SYSTEM', "
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
	
	@Modifying
	@Query("UPDATE Anx1B2cProcEntity doc SET "
			+ "doc.isSaved=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE Anx1B2cProcEntity doc SET "
			+ "doc.isError=true WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
}
