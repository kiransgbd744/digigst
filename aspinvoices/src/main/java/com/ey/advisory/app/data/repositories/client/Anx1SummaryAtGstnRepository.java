/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1SummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */

@Repository("Anx1SummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1SummaryAtGstnRepository
		extends CrudRepository<GetAnx1SummaryEntity, Long> {
	
	@Modifying
	@Query("UPDATE GetAnx1SummaryEntity b SET b.isDelete = true WHERE"
			+ " b.gstin =:gstin AND b.retPeriod =:retPeriod")
	void softlyDeleteAnx1Data(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);

}
