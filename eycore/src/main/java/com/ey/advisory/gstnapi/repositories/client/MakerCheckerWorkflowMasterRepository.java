package com.ey.advisory.gstnapi.repositories.client;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.master.MakerCheckerWorkflowMaster;

@Repository("MakerCheckerWorkflowMasterRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface MakerCheckerWorkflowMasterRepository
		extends CrudRepository<MakerCheckerWorkflowMaster, Long> {

	public List<MakerCheckerWorkflowMaster> findByIsDeleteFalse();
}
