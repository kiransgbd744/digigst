package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.ApprovalPreferenceChildEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */@Repository("ApprovalPreferenceRepository")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public interface ApprovalPreferenceRepository
			extends JpaRepository<ApprovalPreferenceChildEntity, Long>,
			JpaSpecificationExecutor<ApprovalPreferenceChildEntity>  {
	 
	public List<ApprovalPreferenceChildEntity> findByMappingIdIn(List<Long> workFlowIds);

	

	}
