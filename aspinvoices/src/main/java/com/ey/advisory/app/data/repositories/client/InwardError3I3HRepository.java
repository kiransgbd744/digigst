package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HErrorEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("InwardError3I3HRepository")
public interface InwardError3I3HRepository
		extends JpaRepository<InwardTable3I3HErrorEntity, Long>,
		JpaSpecificationExecutor<InwardTable3I3HErrorEntity> {

}
