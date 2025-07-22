package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BSummaryEntity;

@Repository("Gstr8AB2BSummaryDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8AB2BSummaryDetailsRepository
		extends CrudRepository<Gstr8AGetB2BSummaryEntity, Long> {

	List<Gstr8AGetB2BSummaryEntity> findByCgstinAndFinYearAndIsDeleteFalse(
			String gstin, String finYear);

	@Modifying
	@Query("Update Gstr8AGetB2BSummaryEntity SET isDelete = true  WHERE "
			+ "finYear = :finYear AND cgstin = :gstin AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, @Param("finYear") String finYear);
	
	@Modifying
	@Query("UPDATE Gstr8AGetB2BSummaryEntity SET isDelete = true, updatedOn = :updatedOn, updatedBy = :updatedBy "
	     + "WHERE finYear = :finYear AND cgstin = :gstin AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, 
	                @Param("finYear") String finYear, 
	                @Param("updatedOn") LocalDateTime updatedOn, 
	                @Param("updatedBy") String updatedBy);
	
	

}