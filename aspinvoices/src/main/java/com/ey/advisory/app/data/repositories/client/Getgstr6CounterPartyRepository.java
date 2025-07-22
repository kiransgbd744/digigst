package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Getgstr6CounterPartyEntity;
/**
 * 
 * @author Siva.Nandam
 *
 */
@Repository("Getgstr6CounterPartyRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Getgstr6CounterPartyRepository extends 
            CrudRepository<Getgstr6CounterPartyEntity, Long> {
	
	
}
