/**
 * 
 */
package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.Itc04ReturnPeriodEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Itc04ReturnPeriodRepository")
public interface Itc04ReturnPeriodRepository
		extends JpaRepository<Itc04ReturnPeriodEntity, Long>,
		JpaSpecificationExecutor<Itc04ReturnPeriodEntity> {

	@Query("SELECT r FROM Itc04ReturnPeriodEntity r")
	List<Itc04ReturnPeriodEntity> findAll();

}