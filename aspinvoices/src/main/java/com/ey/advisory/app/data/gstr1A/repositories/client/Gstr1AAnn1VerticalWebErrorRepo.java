package com.ey.advisory.app.data.gstr1A.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AAnn1VerticalWebError;


/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AAnn1VerticalWebErrorRepo")
public interface Gstr1AAnn1VerticalWebErrorRepo
		                     extends JpaRepository<Gstr1AAnn1VerticalWebError, Long>,
		                       JpaSpecificationExecutor<Gstr1AAnn1VerticalWebError> {

}
