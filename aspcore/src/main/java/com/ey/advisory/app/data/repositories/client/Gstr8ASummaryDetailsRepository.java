package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr8AGetSummaryEntity;

@Repository("Gstr8ASummaryDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8ASummaryDetailsRepository
		extends CrudRepository<Gstr8AGetSummaryEntity, Long> {

	List<Gstr8AGetSummaryEntity> findByCgstinAndFinYearAndIsDeleteFalse(
			String gstin, String finYear);

	@Modifying
	@Query("Update Gstr8AGetSummaryEntity SET isDelete = true  WHERE "
			+ "finYear = :finYear AND cgstin = :gstin AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, @Param("finYear") String finYear);
	
	@Modifying
	@Query("UPDATE Gstr8AGetSummaryEntity SET isDelete = true WHERE "
	       + "finYear = :finYear AND cgstin = :gstin AND tableType = :tableType AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, @Param("finYear") String finYear, @Param("tableType") String tableType);


}