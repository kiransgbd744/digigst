package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnurHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnurHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("Gstr1AGetCdnurGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AGetCdnurGstnRepository
		extends CrudRepository<GetGstr1ACdnurHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1ACdnurHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn   WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteCdnurHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("SELECT COUNT(*) FROM GetGstr1ACdnurHeaderEntity "
			+ "WHERE gstin=:gstin AND derivedTaxperiod BETWEEN "
			+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin,
			@Param("derivedTaxPeriodFrom") int derivedTaxPeriodFrom,
			@Param("derivedTaxPeriodTo") int derivedTaxPeriodTo);

}