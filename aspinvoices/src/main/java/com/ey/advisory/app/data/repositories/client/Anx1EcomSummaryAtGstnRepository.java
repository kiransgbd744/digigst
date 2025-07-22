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

import com.ey.advisory.app.data.entities.client.GetAnx1EcomSecSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1EcomSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1EcomSummaryAtGstnRepository
		extends CrudRepository<GetAnx1EcomSecSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1EcomSecSummaryEntity b SET b.isDelete = true WHERE"
			+ " b.ecomSummaryId =:id ")
	void softlyDeleteEcoSecSumData(@Param("id") Long id);

}
