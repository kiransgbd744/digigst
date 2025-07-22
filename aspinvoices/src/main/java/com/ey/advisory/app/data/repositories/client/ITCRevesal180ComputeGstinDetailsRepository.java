package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.days.revarsal180.ITCRevesal180ComputeGstinDetailsEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("ITCRevesal180ComputeGstinDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ITCRevesal180ComputeGstinDetailsRepository
		extends CrudRepository<ITCRevesal180ComputeGstinDetailsEntity, Long>,
		JpaSpecificationExecutor<ITCRevesal180ComputeGstinDetailsEntity> {

	@Query("from ITCRevesal180ComputeGstinDetailsEntity where computeId =:computeId")
	public List<ITCRevesal180ComputeGstinDetailsEntity> FindByComputeId(
			@Param("computeId") Long computeId);

}
