package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04SectionSummaryISDEntity;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Repository("Itc04SectionSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04SectionSummaryRepository extends 
            CrudRepository<Itc04SectionSummaryISDEntity, Long> {
	

	
	@Modifying
	@Query("UPDATE Itc04SectionSummaryISDEntity b SET b.isdelete = true "
			+ "WHERE b.isdelete = false AND "
			+ "b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	void softlyDeleteBasedOnGstinAndTaxPeriod(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
	
	
}
