/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.AdditionalDocDetails;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("AdditionalDocDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AdditionalDocDetailsRepository
		extends JpaRepository<AdditionalDocDetails, Long>,
		JpaSpecificationExecutor<AdditionalDocDetails> {

}