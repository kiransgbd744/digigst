package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr2bvs3bStatusEntity;

@Repository("Gstr2bVs3bStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2bVs3bStatusRepository
		extends JpaRepository<Gstr2bvs3bStatusEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr2bvs3bStatusEntity SET isDelete=true "
			+ " WHERE gstin=:gstin AND taxPeriodFrom=:taxPeriodFrom and "
			+ "taxPeriodTo=:taxPeriodTo AND isDelete=false ")
	void gstr2bvs3bInActiveUpdate(@Param("gstin") String gstin,
			@Param("taxPeriodFrom") Integer taxPeriodFrom,
			@Param("taxPeriodTo") Integer taxPeriodTo);

	@Modifying
	@Query("UPDATE Gstr2bvs3bStatusEntity doc SET doc.completedOn= CURRENT_TIMESTAMP,status ='SUCCESS' "
			+ "WHERE doc.batchId=:batchId AND doc.isDelete = false")
	void gstr2bvs3bUpdateSuccessStatus(@Param("batchId") Long batchId);

	@Modifying
	@Query("UPDATE Gstr2bvs3bStatusEntity doc SET doc.completedOn= CURRENT_TIMESTAMP,status ='Failed' "
			+ "WHERE doc.batchId=:batchId AND doc.isDelete = false")
	void gstr2bvs3bUpdateFailedStatus(@Param("batchId") Long batchId);
	
	@Query("SELECT doc FROM Gstr2bvs3bStatusEntity doc "
			+ "WHERE doc.batchId=:batchId AND doc.isDelete = false")
	public List<Gstr2bvs3bStatusEntity> getGstr2bvs3bData(
			@Param("batchId") Long batchId);
}
