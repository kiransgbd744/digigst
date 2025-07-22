/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.RequestIdWiseEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("RequestIdWiseRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RequestIdWiseRepository
		extends CrudRepository<RequestIdWiseEntity, Long> {

}
