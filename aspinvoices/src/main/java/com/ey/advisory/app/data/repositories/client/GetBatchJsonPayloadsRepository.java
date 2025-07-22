/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.GetBatchJsonPayloadsEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("GetBatchJsonPayloadsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetBatchJsonPayloadsRepository
		extends CrudRepository<GetBatchJsonPayloadsEntity, Long> {

}
