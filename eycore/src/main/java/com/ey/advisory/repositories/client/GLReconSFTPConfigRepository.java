package com.ey.advisory.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.domain.client.GlReconSFTPConfigEntity;


/**
 * @author Sakshi.jain
 *
 */
@Repository("GLReconSFTPConfigRepository")
public interface GLReconSFTPConfigRepository
		extends JpaRepository<GlReconSFTPConfigEntity, Long> {

	}
