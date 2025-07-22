package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.AutoDraftAttributeConfig;

/**
 * @author SivaReddy
 *
 */
@Repository("AutoDrafAttrRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface AutoDrafAttrRepository
		extends JpaRepository<AutoDraftAttributeConfig, Long>,
		JpaSpecificationExecutor<AutoDraftAttributeConfig> {
	
	public List<AutoDraftAttributeConfig> findByIsActive(boolean isActive);

}
