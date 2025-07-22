package com.ey.advisory.core.async.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.AccountToGroupMapping;

@Repository("accToGrpMappingRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AccountToGroupMappingRepository 
			extends JpaRepository<AccountToGroupMapping, Long>,
			JpaSpecificationExecutor<AccountToGroupMapping>{
	
	/**
	 * Locates the Accunt TO Group Mapping object by the auth detail returned
	 * by SAP ID provider.
	 * 
	 * @param principal
	 * @return
	 */
	public AccountToGroupMapping findByPrincipal(String principal);
	
}
