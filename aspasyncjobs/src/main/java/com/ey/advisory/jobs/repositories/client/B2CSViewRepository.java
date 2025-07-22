/*package com.ey.advisory.jobs.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.jobs.entities.B2CSEntity;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Repository("b2CSViewRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface B2CSViewRepository
		extends CrudRepository<B2CSEntity, Integer> {

	@Query("SELECT b from B2CSEntity b where b.returnPeriod=:returnPeriod")
	List<B2CSEntity> findByReturnPeriod(
			@Param("returnPeriod") int returnPeriod);

}
*/