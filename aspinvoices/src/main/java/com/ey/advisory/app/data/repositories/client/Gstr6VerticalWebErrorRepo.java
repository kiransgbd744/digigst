package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("Gstr6VerticalWebErrorRepo")
public interface Gstr6VerticalWebErrorRepo
		                     extends JpaRepository<Gstr6VerticalWebError, Long>,
		                       JpaSpecificationExecutor<Gstr6VerticalWebError> {

}
