/**
 * 
 */
package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.Itc04JwTypeEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Itc04JwTypeRepository")
public interface Itc04JwTypeRepository
		extends JpaRepository<Itc04JwTypeEntity, Long>,
		JpaSpecificationExecutor<Itc04JwTypeEntity> {

	@Query("SELECT t FROM Itc04JwTypeEntity t")
	List<Itc04JwTypeEntity> findAll();

}