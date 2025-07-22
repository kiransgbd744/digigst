/*package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04InvoicesEntity;

*//**
 * 
 * @author Balakrishna.S
 *
 *//*
@Repository("Itc04InvoicesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04InvoicesRepository extends CrudRepository<Itc04InvoicesEntity, Long>{
	
	
	@Query("select MAX(e.createdOn) FROM Itc04InvoicesEntity e WHERE e.gstin IN (:gstin) and "
			+ "e.returnPeriod = :taxPeriod "
			+ "and is_delete = false ")
	public LocalDateTime findMaxCreatedDate(@Param("gstin") List<String> gstin,
			@Param("returnPeriod") String taxPeriod );

		
	
}
*/