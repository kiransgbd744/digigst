package com.ey.advisory.app.data.repositories.client.gstr8;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr8.Gstr8DocErrorEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("Gstr8DocErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr8DocErrorRepository extends CrudRepository<Gstr8DocErrorEntity, Long> {

}
