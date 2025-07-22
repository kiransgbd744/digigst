package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BRule86BEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr3BRule86BRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BRule86BRepository
		extends CrudRepository<Gstr3BRule86BEntity, Long> {

	List<Gstr3BRule86BEntity> findByGstinAndTaxPeriodAndIsActiveTrue(
			String gstin, String taxPeriod);

	@Modifying
	@Query("update Gstr3BRule86BEntity e set isActive= FALSE "
			+ "where gstin =:gstin  AND taxPeriod =:taxPeriod AND isActive = TRUE")
	void updateIsActive(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod);

}
