package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.OutwardB2cErrorEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("OutwardB2cErrorRepository")
public interface OutwardB2cErrorRepository
		extends JpaRepository<OutwardB2cErrorEntity, Long>, 
		                   JpaSpecificationExecutor<OutwardB2cErrorEntity> {

}
