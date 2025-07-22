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

import com.ey.advisory.app.data.entities.client.GetAnx1ImpsImpgMisSecSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1ImpsgMisSecSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1ImpsgMisSecSummaryAtGstnRepository
		extends CrudRepository<GetAnx1ImpsImpgMisSecSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1ImpsImpgMisSecSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.impsSummaryId =:id ")
	void softlyDeleteImpgMisSecSumData(@Param("id") Long id);

}
