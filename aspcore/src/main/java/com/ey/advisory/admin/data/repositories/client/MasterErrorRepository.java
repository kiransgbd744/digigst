package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;

@Repository("MasterErrorRepository")
public interface MasterErrorRepository
		extends JpaRepository<MasterErrorEntity, Long> {
	
}
