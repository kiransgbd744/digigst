/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1ActionSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1ActionSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1ActionSummaryAtGstnRepository
		extends CrudRepository<GetAnx1ActionSummaryEntity, Long> {

}
