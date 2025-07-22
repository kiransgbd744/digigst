/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.EinvoicePayloadEntity;

import jakarta.transaction.Transactional;

/**
 * @author Sai.Pakanati
 *
 */
@Repository
@Transactional
public interface EinvoicePayloadRepository
		extends JpaRepository<EinvoicePayloadEntity, Long> {

	@Query("Select e from EinvoicePayloadEntity e where e.entityName = :entityName ORDER BY e.id DESC")
	List<EinvoicePayloadEntity> findByEntityName(@Param("entityName") String entityName);

}
