package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.DongleMappingEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("DongleMappingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DongleMappingRepository
		extends CrudRepository<DongleMappingEntity, Long> {

	List<DongleMappingEntity> findByGstinAndIsActiveTrue(String gstin);
}
