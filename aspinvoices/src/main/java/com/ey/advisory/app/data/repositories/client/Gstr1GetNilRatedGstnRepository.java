package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1NilRatedEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1GetNilRatedGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetNilRatedGstnRepository
		extends CrudRepository<GetGstr1NilRatedEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1NilRatedEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteNilHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
	
	@Query("SELECT COUNT(*) FROM GetGstr1NilRatedEntity "
			+ "WHERE gstin=:gstin AND derivedTaxperiod BETWEEN "
			+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin,
			@Param("derivedTaxPeriodFrom") int derivedTaxPeriodFrom,
			@Param("derivedTaxPeriodTo") int derivedTaxPeriodTo);

}
