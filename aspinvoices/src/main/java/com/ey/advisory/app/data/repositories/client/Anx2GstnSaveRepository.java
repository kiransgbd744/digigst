package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.Anx2GstnSaveEntity;

@Repository("Anx2GstnSaveRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx2GstnSaveRepository
		extends CrudRepository<Anx2GstnSaveEntity, Long> {

	@Modifying
	@Query("UPDATE Anx2GstnSaveEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSentToGstn=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,"
			+ "doc.modifiedOn = CURRENT_TIMESTAMP WHERE doc.id IN (:ids)")
	void updateBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("ids") List<Long> ids);
}