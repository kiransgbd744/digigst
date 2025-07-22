/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1DocTypeSummaryEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Anx1DocTypeSummaryAtGstnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1DocTypeSummaryAtGstnRepository
		extends CrudRepository<GetAnx1DocTypeSummaryEntity, Long> {

}