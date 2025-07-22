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

import com.ey.advisory.app.data.entities.client.GetAnx1B2cSecSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */

@Repository("Anx1B2CSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1B2CSummaryAtGstnRepository
		extends CrudRepository<GetAnx1B2cSecSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1B2cSecSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.b2cSumId =:id ")
	void softlyDeleteB2csSumData(@Param("id") Long id);

}
