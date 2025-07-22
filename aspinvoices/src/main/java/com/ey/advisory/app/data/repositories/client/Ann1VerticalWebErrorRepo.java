package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Ann1VerticalWebError;


/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ann1VerticalWebErrorRepo")
public interface Ann1VerticalWebErrorRepo
		                     extends JpaRepository<Ann1VerticalWebError, Long>,
		                       JpaSpecificationExecutor<Ann1VerticalWebError> {

}
