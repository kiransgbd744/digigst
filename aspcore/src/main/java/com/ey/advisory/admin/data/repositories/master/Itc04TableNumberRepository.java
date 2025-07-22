/**
 * 
 */
package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.Itc04TableNumberEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Itc04TableNumberRepository")
public interface Itc04TableNumberRepository
		extends JpaRepository<Itc04TableNumberEntity, Long>,
		JpaSpecificationExecutor<Itc04TableNumberEntity> {

	@Query("SELECT t FROM Itc04TableNumberEntity t")
	List<Itc04TableNumberEntity> findAll();

}