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

import com.ey.advisory.app.data.entities.client.GetAnx1ImpgsezRevSecSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1ImpgSezSecSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1ImpgSezSecSummaryAtGstnRepository
		extends CrudRepository<GetAnx1ImpgsezRevSecSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1ImpgsezRevSecSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.impgsezSummaryId =:id ")
	void softlyDeleteImpgSezSumData(@Param("id") Long id);

}
