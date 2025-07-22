package com.ey.advisory.app.data.repositories.client.gstr7;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr7DocErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7DocErrorRepository extends CrudRepository<Gstr7DocErrorEntity, Long> {

}
