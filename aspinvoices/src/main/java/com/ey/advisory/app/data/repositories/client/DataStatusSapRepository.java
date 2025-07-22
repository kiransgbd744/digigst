package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.DataStatusSapEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("DataStatusSapRepository")
public interface DataStatusSapRepository
		extends JpaRepository<DataStatusSapEntity, Long>,
		JpaSpecificationExecutor<DataStatusSapEntity> {
}
