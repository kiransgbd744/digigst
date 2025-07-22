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

import com.ey.advisory.app.data.entities.client.GetAnx1SectionSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1SecSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1SecSummaryAtGstnRepository
		extends CrudRepository<GetAnx1SectionSummaryEntity, Long> {
	
	@Modifying
	@Query("UPDATE GetAnx1SectionSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.secSummaryId =:id ")
	void softlyDeleteSecSumData(@Param("id") Long id);

}
