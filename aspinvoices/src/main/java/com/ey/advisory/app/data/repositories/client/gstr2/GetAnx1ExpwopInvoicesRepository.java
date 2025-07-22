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

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ExpwopInvoicesHeaderEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetAnx1ExpwopInvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx1ExpwopInvoicesRepository
		extends CrudRepository<GetAnx1ExpwopInvoicesHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1ExpwopInvoicesHeaderEntity b SET b.isDelete = true WHERE"
			+ " b.isDelete = false AND b.sGstin =:sGstin AND b.taxPeriod =:taxPeriod")
	void softlyDeleteExpwopHeader(@Param("sGstin") String sGstin,
			@Param("taxPeriod") String taxPeriod);
}
