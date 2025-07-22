/*package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetGstr2aBatchEntity;

@Repository("Gstr2aBatchRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2aBatchRepository
		extends JpaRepository<GetGstr2aBatchEntity, Long>,
		JpaSpecificationExecutor<GetGstr2aBatchEntity> {

	@Modifying
	@Query("UPDATE GetGstr2aBatchEntity a SET STATUS = :status , "
			+ "modifiedOn = :modifiedOn where cGstin in :cGstin and "
			+ " IS_DELETE = FALSE")
	public void updateGstr2aBatchStausandDate(@Param("status") String status, 
			@Param("modifiedOn") LocalDateTime modifiedOn, 
			@Param("cGstin") List<String> cGstin); 

}
*/