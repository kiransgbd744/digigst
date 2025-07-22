/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ErpBatchJsonPayloadsEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("ErpBatchJsonPayloadsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ErpBatchJsonPayloadsRepository
extends CrudRepository<ErpBatchJsonPayloadsEntity, Long> {

}