package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Getgstr6IsdItcSummaryEntity;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Repository("Getgstr6IsdItcSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Getgstr6IsdItcSummaryRepository extends 
            CrudRepository<Getgstr6IsdItcSummaryEntity, Long> {
	
	
	
	@Modifying
	@Query("UPDATE Getgstr6IsdItcSummaryEntity b SET b.isdelete = true "
			+ "WHERE b.isdelete = false AND "
			+ "b.gstIn =:sGstin AND b.taxperiod =:taxPeriod")
	void softlyDeleteBasedOnGstinAndTaxPeriod(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
	
}
