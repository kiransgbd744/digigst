package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.InwardTable3IDetailsEntity;

/**
 * 
 * @author Sujith.Nanga
 *
 */

@Repository("Table3IRepository")
public interface Table3IRepository
		extends JpaRepository<InwardTable3IDetailsEntity, Long>,
		JpaSpecificationExecutor<InwardTable3IDetailsEntity> {
}
