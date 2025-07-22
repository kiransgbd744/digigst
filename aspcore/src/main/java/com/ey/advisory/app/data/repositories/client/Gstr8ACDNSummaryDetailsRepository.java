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

import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNSummaryEntity;

@Repository("Gstr8ACDNSummaryDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8ACDNSummaryDetailsRepository
		extends CrudRepository<Gstr8AGetCDNSummaryEntity, Long> {

	List<Gstr8AGetCDNSummaryEntity> findByCgstinAndFinYearAndIsDeleteFalse(
			String gstin, String finYear);

	@Modifying
	@Query("Update Gstr8AGetCDNSummaryEntity SET isDelete = true  WHERE "
			+ "finYear = :finYear AND cgstin = :gstin AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, @Param("finYear") String finYear);
	
	@Modifying
	@Query("UPDATE Gstr8AGetCDNSummaryEntity SET isDelete = true, updatedOn = :updatedOn, updatedBy = :updatedBy "
	     + "WHERE finYear = :finYear AND cgstin = :gstin AND isDelete = false")
	void softDelete(@Param("gstin") String gstin, 
	                @Param("finYear") String finYear, 
	                @Param("updatedOn") LocalDateTime updatedOn, 
	                @Param("updatedBy") String updatedBy);
}