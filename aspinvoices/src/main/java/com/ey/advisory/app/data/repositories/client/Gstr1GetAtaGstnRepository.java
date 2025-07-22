package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr1AtaHeaderEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr1GetAtaGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1GetAtaGstnRepository
		extends CrudRepository<GetGstr1AtaHeaderEntity, Long> {
	@Modifying
	@Query("UPDATE GetGstr1AtaHeaderEntity b SET b.isDelete = true,b.modifiedBy = 'SYSTEM'  , b.modifiedOn =:modifiedOn  WHERE"
			+ " b.isDelete = false AND b.gstin =:sGstin AND b.returnPeriod =:taxPeriod")
	int softlyDeleteAtaHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod,@Param("modifiedOn") LocalDateTime modifiedOn);
	
	@Query("SELECT COUNT(*) FROM GetGstr1AtaHeaderEntity "
			+ "WHERE gstin=:gstin AND derivedTaxperiod BETWEEN "
			+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo ")
	public int gstinCountByRetPerFromTo(@Param("gstin") String gstin,
			@Param("derivedTaxPeriodFrom") int derivedTaxPeriodFrom,
			@Param("derivedTaxPeriodTo") int derivedTaxPeriodTo);

}
