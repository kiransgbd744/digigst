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

import com.ey.advisory.app.data.entities.client.GetAnx1ExpwpExpwopSecSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1ExpwpwopSecSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1ExpwpwopSecSummaryAtGstnRepository
		extends CrudRepository<GetAnx1ExpwpExpwopSecSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1ExpwpExpwopSecSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.expwpSummaryId =:id ")
	void softlyDeleteExpwopSumData(@Param("id") Long id);

}
