package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3bVerticalWebError;


/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr3bVerticalWebErrorRepo")
public interface Gstr3bVerticalWebErrorRepo
		                     extends JpaRepository<Gstr3bVerticalWebError, Long>,
		                       JpaSpecificationExecutor<Gstr3bVerticalWebError> {

}
