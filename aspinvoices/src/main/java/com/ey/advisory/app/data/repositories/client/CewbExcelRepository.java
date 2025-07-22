package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.CewbExcelEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("CewbExcelRepository")
public interface CewbExcelRepository
		extends JpaRepository<CewbExcelEntity, Long>,
		JpaSpecificationExecutor<CewbExcelEntity> {

}
