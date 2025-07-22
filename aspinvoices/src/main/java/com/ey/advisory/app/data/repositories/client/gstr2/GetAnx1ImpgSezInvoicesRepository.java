/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.gstr2;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpgSezInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetAnx1ImpgSezInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx1ImpgSezInvoicesRepository
		extends CrudRepository<GetAnx1ImpgSezInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1ImpgSezInvoicesHeaderEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.sGstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteImpgSezHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}